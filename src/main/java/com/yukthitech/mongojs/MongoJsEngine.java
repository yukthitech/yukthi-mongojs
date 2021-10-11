package com.yukthitech.mongojs;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
	 * Mongo client connection.
	 */
	private MongoClient mongoClient;
	
	/**
	 * Database on which operations needs to be performed.
	 */
	private MongoDatabase database;
	
	private JsMongoDatabase mongoDb;
	
	/**
	 * Nahron Engine to execute java-script.
	 */
	private ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
	
	public MongoJsEngine(MongoJsArguments args)
	{
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
				
		if(credential != null)
		{
			this.mongoClient = new MongoClient(mongoHosts, credential, clientOptions);
		}
		else
		{
			this.mongoClient = new MongoClient(mongoHosts, clientOptions);
		}
		
		this.database = mongoClient.getDatabase(database);
		this.mongoDb = new JsMongoDatabase(this.database);
		
		loadClassMethods(MongoMethods.class);
		this.engine.put("db", mongoDb);
		
		logger.debug("Connected to mongocluster {} successfully", replicas);
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
		
		this.engine.put(name, new JsMethodWrapper(method, this.mongoDb));
	}

	/**
	 * Executes specified script. 
	 * In script, entries specified in param-map are accessible as global variables.
	 * @param script
	 * @param paramMap
	 */
	public void executeScript(String script)
	{
		if(StringUtils.isBlank(script))
		{
			throw new NullPointerException("Script is null or empty");
		}
		
		try
		{
			engine.eval(script);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while executing below script:\n{}", script, ex);
		}
	}
}
