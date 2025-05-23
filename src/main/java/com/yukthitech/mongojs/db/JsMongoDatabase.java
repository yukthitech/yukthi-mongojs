package com.yukthitech.mongojs.db;

import java.util.AbstractMap;
import java.util.Set;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class JsMongoDatabase extends AbstractMap<String, JsMongoCollection>
{
	/**
	 * Database on which operations needs to be performed.
	 */
	private MongoDatabase database;

	public JsMongoDatabase(MongoDatabase database)
	{
		this.database = database;
	}

	public JsMongoCollection getCollection(String name)
	{
		return new JsMongoCollection(database, name);
	}
	
	public MongoCollection<Document> getMongoCollection(String name)
	{
		return database.getCollection(name);
	}
	
	@Override
	public boolean containsKey(Object key)
	{
		try
		{
			MongoCollection<Document> collection = database.getCollection("" + key);
			return (collection != null);
		}catch(Exception ex)
		{
			return false;
		}
	}

	@Override
	public Set<Entry<String, JsMongoCollection>> entrySet()
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public JsMongoCollection get(Object key)
	{
		return getCollection(key.toString());
	}
	
	@Override
	public String toString()
	{
		return "[JsMongoDatabase]";
	}
}
