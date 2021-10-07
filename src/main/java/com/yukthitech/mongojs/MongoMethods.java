package com.yukthitech.mongojs;

import java.util.Map;

import org.bson.types.ObjectId;

public class MongoMethods
{
	public static ObjectId ObjectId(String id)
	{
		return new ObjectId(id);
	}
	
	public static void deleteProperty(Map<String, Object> obj, String attrName)
	{
		obj.remove(attrName);
	}
}
