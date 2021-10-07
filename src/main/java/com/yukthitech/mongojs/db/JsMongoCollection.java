package com.yukthitech.mongojs.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.yukthitech.mongojs.MongoJsUtils;

public class JsMongoCollection
{
	private static Logger logger = LogManager.getLogger(JsMongoCollection.class);
	
	private static ObjectMapper OBJ_MAPPER = new ObjectMapper();
	
	/**
	 * Database on which operations needs to be performed.
	 */
	private MongoDatabase database;
	
	/**
	 * Name of collection represented by this object.
	 */
	private String name;
	
	/**
	 * Mongo collection object.
	 */
	private MongoCollection<Document> collection;

	public JsMongoCollection(MongoDatabase database, String name)
	{
		this.database = database;
		this.name = name;
		
		this.collection = this.database.getCollection(this.name);
	}
	
	@SuppressWarnings("unchecked")
	public String insert(Map<String, Object> doc)
	{
		doc = (Map<String, Object>) MongoJsUtils.unwrapObject(doc);
		Document entityDoc = new Document(doc);
		collection.insertOne(entityDoc);
		
		String id = entityDoc.getObjectId("_id").toHexString();
		
		logger.debug("Inserted new doc into collection '{}' with id: {}", name, id);
		return id;
	}
	
	public Object find(Map<String, Object> query) throws Exception
	{
		return find(query, null);
	}
	
	@SuppressWarnings("unchecked")
	public Object find(Map<String, Object> query, Map<String, Object> projection) throws Exception
	{
		query = (Map<String, Object>) MongoJsUtils.unwrapObject(query);
		projection = (Map<String, Object>) MongoJsUtils.unwrapObject(projection);
		
		Document filterDoc = new Document(query);
		FindIterable<Document> resIt = collection.find(filterDoc);
		
		if(projection != null)
		{
			resIt.projection(new Document(projection));
		}
		
		List<Object> resLst = new ArrayList<Object>();
		
		for(Document res : resIt)
		{
			String json = res.toJson();
			resLst.add(OBJ_MAPPER.readValue(json, Object.class));
		}
		
		return resLst;
	}
}
