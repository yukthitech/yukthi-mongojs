package com.yukthitech.mongojs;

import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.yukthitech.mongojs.db.JsMongoDatabase;
import com.yukthitech.utils.CommonUtils;

public class MongoMethods
{
	public static ObjectMapper OBJ_MAPPER = new ObjectMapper();

	public static ObjectId ObjectId(String id)
	{
		return new ObjectId(id);
	}
	
	public static void deleteProperty(Map<String, Object> obj, String attrName)
	{
		obj.remove(attrName);
	}
	
	public static Object docToMap(Document doc) throws Exception
	{
		String json = doc.toJson();
		return OBJ_MAPPER.readValue(json, Object.class);
	}
	
	public static String toJson(Object obj)
	{
		try
		{
			return OBJ_MAPPER.writeValueAsString(obj);
		}catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while converting object to json", ex);
		}
	}
	
	public static Long NumberLong(Object val)
	{
		if(val instanceof Number)
		{
			return ((Number) val).longValue();
		}
		
		return Long.parseLong("" + val);
	}

	public static Integer NumberInt(Object val)
	{
		if(val instanceof Number)
		{
			return ((Number) val).intValue();
		}
		
		return Integer.parseInt("" + val);
	}
	
	public static String fetchId(JsMongoDatabase mongoDb, String collectionName, String fld, Object value)
	{
		MongoCollection<Document> mongoCollection = mongoDb.getMongoCollection(collectionName);
		FindIterable<Document> docIt = mongoCollection.find(new Document(CommonUtils.toMap(fld, value)));

		Document doc = docIt.first();
		
		if(doc == null)
		{
			return null;
		}
		
		return doc.get("_id").toString();
	}
}
