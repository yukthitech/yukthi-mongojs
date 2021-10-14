package com.yukthitech.mongojs;

import java.lang.reflect.Method;
import java.util.Arrays;

import com.yukthitech.mongojs.db.JsMongoDatabase;

public class JsMethodWrapper implements JsMethod
{
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
		int len = needDbArg ? args.length + 1 : args.length;
		Object array[] = new Object[len];
		int startIdx = 0;
		
		if(needDbArg)
		{
			array[0] = database;
			startIdx = 1;
		}
		
		for(int i = 0; i < args.length; i++)
		{
			array[i + startIdx] = MongoJsUtils.unwrapObject(args[i]);
		}
		
		try
		{
			return actualMethod.invoke(null, array);
		} catch(Exception ex)
		{
			throw new IllegalStateException(String.format(
					"Method invocation resulted in error.\nMethod: %s.%s()\nArguments: %s",
					actualMethod.getDeclaringClass().getName(),
					actualMethod.getName(),
					Arrays.toString(array)
					), ex);
		}
	}
}
