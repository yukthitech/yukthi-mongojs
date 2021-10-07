package com.yukthitech.mongojs;

import org.bson.types.ObjectId;

import com.yukthitech.mongojs.db.JsMongoDatabase;

public class MongoMethods
{
	public static ObjectId ObjectId(JsMongoDatabase database, String id)
	{
		return new ObjectId(id);
	}
}
