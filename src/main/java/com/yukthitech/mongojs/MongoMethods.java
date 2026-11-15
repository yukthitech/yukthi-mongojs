package com.yukthitech.mongojs;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.yukthitech.mongojs.db.JsMongoDatabase;
import com.yukthitech.utils.CommonUtils;

public class MongoMethods
{
	private static Logger logger = LogManager.getLogger(MongoMethods.class);
	
	public static ObjectMapper OBJ_MAPPER = new ObjectMapper();
	
	private static final String DEF_DATE_FORMAT = "yyyy-MM-dd";

	public static ObjectId ObjectId(String id)
	{
		return new ObjectId(id);
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> deleteProperty(Object obj, String attrName)
	{
		obj = MongoJsUtils.unwrapObject(obj);
		
		if(!(obj instanceof Map))
		{
			return null;
		}
		
		Map<String, Object> map = new java.util.LinkedHashMap<String, Object>((Map<String, Object>) obj);
		map.remove(attrName);
		
		return map;
	}
	
	public static Object docToMap(Object doc) throws Exception
	{
		if(doc == null)
		{
			return null;
		}
		
		if(doc instanceof Document)
		{
			// Walk the document tree directly so Date/ObjectId millis and types are preserved.
			// Document.toJson() relaxed ISO strings drop trailing fractional zeros and were
			// mis-parsed by SimpleDateFormat (e.g. ".1Z" -> 1ms instead of 100ms).
			return MongoJsUtils.documentToStandardMap((Document) doc);
		}
		
		if(doc instanceof Map)
		{
			return MongoJsUtils.standardizeValue(doc);
		}
		
		throw new IllegalArgumentException("docToMap() expects Document or Map. Specified type: " + doc.getClass().getName());
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
	
	public static Date ISODate(String dateStr) throws Exception
	{
		// Instant/OffsetDateTime accept variable-length fractional seconds from
		// MongoDB relaxed Extended JSON (e.g. ".1Z", ".12Z", or no fraction).
		try
		{
			return Date.from(Instant.parse(dateStr));
		}
		catch(DateTimeParseException ex)
		{
			return Date.from(OffsetDateTime.parse(dateStr).toInstant());
		}
	}

	public static Date date(String dateStr) throws Exception
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat(DEF_DATE_FORMAT);
		return dateFormat.parse(dateStr);
	}
	
	public static Date now()
	{
		return new Date();
	}
	
	public static WrapperList<Object> toWrapperList(Object... args)
	{
		return new WrapperList<>(Arrays.asList(args));
	}
	
	public static void print(Object... args)
	{
		if(args.length == 0)
		{
			return;
		}
		
		StringBuilder builder = new StringBuilder();
		
		for(int i = 0; i < args.length; i++)
		{
			Object val = args[i];
			
			if(!(val instanceof String))
			{
				val = toJson(val);
			}
			
			builder.append(val);
		}
		
		IMongoJsCustomizer customizer = MongoJsEngine.getCurrentEngine().getCustomizer();
		String finalMssg = builder.toString();
		
		if(customizer.printLog(finalMssg))
		{
			return;
		}
		
		logger.debug("[PRINT] {}", finalMssg);
	}
	
	public static Object unwrap(Object object)
	{
		return MongoJsUtils.unwrapObject(object);
	}
}
