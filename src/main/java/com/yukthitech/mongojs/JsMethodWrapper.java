package com.yukthitech.mongojs;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.mongojs.db.JsMongoDatabase;

public class JsMethodWrapper implements JsMethod
{
	private static Logger logger = LogManager.getLogger(JsMethodWrapper.class);
	
	/**
	 * Actual method to be invoked.
	 */
	private Method actualMethod;
	
	private JsMongoDatabase database;
	
	private boolean needDbArg;
	
	public JsMethodWrapper(Method actualMethod, JsMongoDatabase database)
	{
		this.actualMethod = actualMethod;
		this.database = database;
		
		Class<?> paramTypes[] = actualMethod.getParameterTypes(); 
		needDbArg = paramTypes.length > 0 ? paramTypes[0].equals(JsMongoDatabase.class) : false;
	}
	
	@Override
	public Object call(Object... args)
	{
		args = (args == null) ? new Object[0] : args;
		int len = needDbArg ? args.length + 1 : args.length;
		List<Object> argLst = new ArrayList<Object>(len);
		
		if(needDbArg)
		{
			argLst.add(database);
		}
		
		Parameter params[] = actualMethod.getParameters();
		
		for(int i = 0; i < args.length; i++)
		{
			if(!params[i].isVarArgs())
			{
				argLst.add(MongoJsUtils.unwrapObject(args[i]));
				continue;
			}
			
			List<Object> varArg = new ArrayList<Object>();
			
			for(int j = i; j < args.length; j++)
			{
				varArg.add(MongoJsUtils.unwrapObject(args[j]));
			}

			Object varArr[] = (Object[]) Array.newInstance(params[i].getType().getComponentType(), varArg.size());
			argLst.add(varArg.toArray(varArr));
			break;
		}
		
		try
		{
			return actualMethod.invoke(null, argLst.toArray());
		} catch(Exception ex)
		{
			logger.error("Method invocation resulted in error.\nMethod: {}.{}()\nArguments: {}", actualMethod.getDeclaringClass().getName(),
					actualMethod.getName(),
					argLst,
					ex);
			throw new IllegalStateException(String.format(
					"Method invocation resulted in error.\nMethod: %s.%s()\nArguments: %s\nError: %s",
					actualMethod.getDeclaringClass().getName(),
					actualMethod.getName(),
					argLst,
					ex), ex);
		}
	}
}
