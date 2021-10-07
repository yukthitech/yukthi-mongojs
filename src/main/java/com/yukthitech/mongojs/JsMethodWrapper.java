package com.yukthitech.mongojs;

import java.lang.reflect.Method;

import com.yukthitech.mongojs.db.JsMongoDatabase;

public class JsMethodWrapper implements JsMethod
{
	/**
	 * Actual method to be invoked.
	 */
	private Method actualMethod;
	
	private JsMongoDatabase database;
	
	public JsMethodWrapper(Method actualMethod, JsMongoDatabase database)
	{
		this.actualMethod = actualMethod;
		this.database = database;
	}

	@Override
	public Object call(Object... args)
	{
		Object array[] = new Object[args.length + 1];
		
		array[0] = database;
		
		for(int i = 0; i < args.length; i++)
		{
			array[i + 1] = args[i];
		}
		
		try
		{
			return actualMethod.invoke(null, array);
		} catch(Exception ex)
		{
			throw new IllegalStateException("Method invocation resulted in error", ex);
		}
	}
}
