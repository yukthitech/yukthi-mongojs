package com.yukthitech.mongojs.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.yukthitech.mongojs.MongoJsUtils;
import com.yukthitech.mongojs.MongoMethods;

public class JsMongoCollection
{
	private static Logger logger = LogManager.getLogger(JsMongoCollection.class);
	
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
	
	@SuppressWarnings("unchecked")
	public void insertMany(List<Map<String, Object>> docs)
	{
		docs = (List<Map<String, Object>>) MongoJsUtils.unwrapObject(docs);
		
		List<Document> docLst = docs
				.stream()
				.map(map -> new Document(map))
				.collect(Collectors.toList());

		collection.insertMany(docLst);
	}

	public Object findOne(Map<String, Object> query) throws Exception
	{
		return findOne(query, null);
	}
	
	public Object findOne(Map<String, Object> query, Map<String, Object> projection) throws Exception
	{
		List<Document> docLst = find(query, projection);
		
		if(CollectionUtils.isEmpty(docLst))
		{
			return null;
		}
		
		return docLst.get(0);
	}

	public Object find(Map<String, Object> query) throws Exception
	{
		return find(query, null);
	}
	
	@SuppressWarnings("unchecked")
	public List<Document> find(Map<String, Object> query, Map<String, Object> projection) throws Exception
	{
		query = (Map<String, Object>) MongoJsUtils.unwrapObject(query);
		projection = (Map<String, Object>) MongoJsUtils.unwrapObject(projection);
		
		logger.debug("Executing equivalent of query: db.{}.find({}, {})", 
				this.name, MongoMethods.toJson(query), MongoMethods.toJson(projection));
		
		Document filterDoc = new Document(query);
		FindIterable<Document> resIt = collection.find(filterDoc);
		
		if(projection != null)
		{
			resIt.projection(new Document(projection));
		}
		
		MongoDocList resLst = new MongoDocList();
		
		for(Document res : resIt)
		{
			resLst.add(res);
		}
		
		return resLst;
	}
	
	@SuppressWarnings("unchecked")
	public Object aggregate(List<Map<String, Object>> pipeline) throws Exception
	{
		pipeline = (List<Map<String, Object>>) MongoJsUtils.unwrapObject(pipeline);
		
		List<Document> pipelineDocs = new ArrayList<Document>();
		
		for(Map<String, Object> doc : pipeline)
		{
			pipelineDocs.add(new Document(doc));
		}
		
		AggregateIterable<Document> resIt = collection.aggregate(pipelineDocs);
		
		MongoDocList resLst = new MongoDocList();
		
		for(Document res : resIt)
		{
			resLst.add(res);
		}
		
		return resLst;
	}

	@SuppressWarnings("unchecked")
	public long deleteOne(Map<String, Object> query)
	{
		query = (Map<String, Object>) MongoJsUtils.unwrapObject(query);
		
		DeleteResult res = collection.deleteOne(new Document(query));
		return res.getDeletedCount();
	}

	@SuppressWarnings("unchecked")
	public long deleteMany(Map<String, Object> query)
	{
		query = (Map<String, Object>) MongoJsUtils.unwrapObject(query);
		
		DeleteResult res = collection.deleteMany(new Document(query));
		return res.getDeletedCount();
	}
	
	@SuppressWarnings("unchecked")
	public long count(Map<String, Object> query)
	{
		query = (Map<String, Object>) MongoJsUtils.unwrapObject(query);
		return collection.countDocuments(new Document(query));
	}

	public long update(Map<String, Object> filter, Map<String, Object> updates)
	{
		return update(filter, updates, null);
	}

	@SuppressWarnings("unchecked")
	public long update(Map<String, Object> filter, Map<String, Object> updates, Map<String, Object> optionsMap)
	{
		filter = (Map<String, Object>) MongoJsUtils.unwrapObject(filter);
		updates = (Map<String, Object>) MongoJsUtils.unwrapObject(updates);
		UpdateOptions options = MongoJsUtils.convert(optionsMap, UpdateOptions.class);
		
		com.mongodb.client.model.UpdateOptions mongoOptions = (options == null) 
				? new com.mongodb.client.model.UpdateOptions() 
				: options.toMongoOptions();
		UpdateResult res = null;
		
		if(options != null && options.isMulti())
		{
			res = collection.updateMany(new Document(filter), new Document(updates), mongoOptions);
		}
		else
		{
			res = collection.updateOne(new Document(filter), new Document(updates), mongoOptions);
		}
		
		return res.getMatchedCount();
	}
}
