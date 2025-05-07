package com.yukthitech.mongojs;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoDatabase;
import com.yukthitech.mongojs.common.MongoJsArguments;
import com.yukthitech.mongojs.db.JsMongoDatabase;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class MongoJsEngine
{
	private static Logger logger = LogManager.getLogger(MongoJsEngine.class);

	/**
	 * The Constant HOST_PORT.
	 */
	private static final Pattern HOST_PORT = Pattern.compile("([\\w\\.\\-]+)\\:(\\d+)");
	
	/**
	 * Used to maintain current js engine during script execution.
	 */
	private static ThreadLocal<MongoJsEngine> currentEngine = new ThreadLocal<MongoJsEngine>();
	
	private static final IMongoJsCustomizer DEFAULT_CUSTOMIZER = new IMongoJsCustomizer()
	{
	};

	/**
	 * Database on which operations needs to be performed.
	 */
	private MongoDatabase database;
	
	private JsMongoDatabase mongoDb;
	
	private Context jsContext;
	
	
	private Map<String, Object> defaultBindings = new HashMap<String, Object>();
	
	/**
	 * Customizer for this engine.
	 */
	private IMongoJsCustomizer customizer = DEFAULT_CUSTOMIZER;
	
	@SuppressWarnings("resource")
	public MongoJsEngine(MongoJsArguments args)
	{
		jsContext = Context.newBuilder("js").allowAllAccess(true).build();
		
		if(jsContext == null)
		{
			throw new IllegalStateException("Failed to create js engine");
		}
		
		String user = args.getUserName();
		String password = args.getPassword();
		String database = args.getDbname();
		
		MongoCredential credential = (StringUtils.isNotBlank(user) && StringUtils.isNotBlank(password)) ? MongoCredential.createCredential(user, database, password.toCharArray()) : null;
		List<ServerAddress> mongoHosts = null;

		String replicas = args.getReplicas();
		mongoHosts = parse(replicas);
		
		MongoClientOptions clientOptions = MongoClientOptions.builder()
				.writeConcern(WriteConcern.ACKNOWLEDGED)
				.sslEnabled(args.isEnableSsl())
				.build();
				
		MongoClient mongoClient = null;
		
		if(credential != null)
		{
			mongoClient = new MongoClient(mongoHosts, credential, clientOptions);
		}
		else
		{
			mongoClient = new MongoClient(mongoHosts, clientOptions);
		}
		
		this.setDb(mongoClient.getDatabase(database));
		logger.debug("Connected to mongocluster {} successfully", replicas);
	}
	
	public MongoJsEngine(MongoDatabase mongoDatabase)
	{
		this.setDb(mongoDatabase);
	}
	
	/**
	 * Sets the customizer for this engine. If null is specified, default customizer will be used.
	 *
	 * @param customizer the new customizer for this engine
	 */
	public void setCustomizer(IMongoJsCustomizer customizer)
	{
		if(customizer == null)
		{
			customizer = DEFAULT_CUSTOMIZER;
		}
		
		this.customizer = customizer;
	}
	
	/**
	 * Gets the customizer for this engine.
	 *
	 * @return the customizer for this engine
	 */
	public IMongoJsCustomizer getCustomizer()
	{
		return customizer;
	}
	
	private void setDb(MongoDatabase database)
	{
		this.database = database;
		this.mongoDb = new JsMongoDatabase(this.database);
		
		loadClassMethods(MongoMethods.class);
		this.defaultBindings.put("db", mongoDb);
	}
	
	public void loadClassMethods(Class<?> cls)
	{
		Method methods[] = cls.getDeclaredMethods();
		
		for(Method met : methods)
		{
			if(!Modifier.isStatic(met.getModifiers()) || met.isSynthetic())
			{
				continue;
			}
			
			registerMethod(met.getName(), met);
		}
	}

	private List<ServerAddress> parse(String replicas)
	{
		String lst[] = replicas.trim().split("\\s*\\,\\s*");
		List<ServerAddress> serverAddresses = new ArrayList<>();
		
		for(String item : lst)
		{
			Matcher matcher = HOST_PORT.matcher(item);
			
			if(!matcher.matches())
			{
				throw new InvalidArgumentException("Invalid mongo host-port combination specified. It should be of format host:port. Specified Replicas: {}", replicas);
			}
			
			serverAddresses.add(new ServerAddress(matcher.group(1), Integer.parseInt(matcher.group(2))));
		}
		
		return serverAddresses;
	}
	
	public void registerMethod(String name, Method method)
	{
		logger.trace("Registering method {}.{}() as {}", method.getClass().getName(), method.getName(), name);
		
		this.defaultBindings.put(name, new JsMethodWrapper(method, this.mongoDb));
	}
	
	public static MongoJsEngine getCurrentEngine()
	{
		return currentEngine.get();
	}

	/**
	 * Executes specified script. 
	 * In script, entries specified in param-map are accessible as global variables.
	 * @param script
	 * @param paramMap
	 */
	public Object executeScript(String script)
	{
		if(StringUtils.isBlank(script))
		{
			throw new NullPointerException("Script is null or empty");
		}
		
		currentEngine.set(this);
		
		try
		{
			Value bindings = jsContext.getBindings("js");
			
			for(Map.Entry<String, Object> entry : this.defaultBindings.entrySet())
			{
				bindings.putMember(entry.getKey(), entry.getValue());
			}

			return MongoJsUtils.unwrapObject(jsContext.eval("js", script));
		} catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while executing below script:\n{}", script, ex);
		} finally
		{
			currentEngine.remove();
		}
	}
}
