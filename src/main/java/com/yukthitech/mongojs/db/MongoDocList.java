package com.yukthitech.mongojs.db;

import java.lang.reflect.Method;
import java.util.LinkedList;

import org.bson.Document;

import com.yukthitech.utils.exceptions.InvalidStateException;

public class MongoDocList extends LinkedList<Document>
{
	private static final long serialVersionUID = 1L;
	
	public void forEach(Object callback)
	{
		try
		{
			Method method = callback.getClass().getMethod("call", Object.class, Object[].class);
			
			for(Document doc : this)
			{
				method.invoke(method, doc, doc);
			}
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while invoking callback", ex);
		}
	}
}
