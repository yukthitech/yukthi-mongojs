package com.yukthitech.mongojs;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.graalvm.polyglot.Value;

import com.yukthitech.mongojs.db.MongoAggrDocList;
import com.yukthitech.mongojs.db.MongoDocList;

public class MongoJsUtils
{
	@SuppressWarnings("unchecked")
	public static Object unwrapObject(Object input)
	{
		if(input == null)
		{
			return null;
		}
		
		if(input instanceof WrapperList)
		{
			input = ((WrapperList<Object>) input).getObjects();
		}
		
		if(input instanceof Value)
		{
			return unwrapValue((Value) input);
		}
		
		if(input instanceof List)
		{
			List<Object> res = new LinkedList<Object>();
			List<Object> inputLst = (List<Object>) input;
			
			for(Object obj : inputLst)
			{
				res.add(unwrapObject(obj));
			}
			
			return res;
		}
		
		if(input instanceof Map)
		{
			Map<Object, Object> res = new LinkedHashMap<Object, Object>();
			Map<Object, Object> inputMap = (Map<Object, Object>) input;
			
			for(Map.Entry<Object, Object> entry : inputMap.entrySet())
			{
				res.put(unwrapObject(entry.getKey()), unwrapObject(entry.getValue()));
			}
			
			return res;
		}
		
		return input;
	}
	
	private static Object unwrapValue(Value value)
	{
		if(value.isNull())
		{
			return null;
		}
		
		if(value.isBoolean())
		{
			return value.asBoolean();
		}
		
		if(value.isString())
		{
			return value.asString();
		}
		
		if(value.isNumber())
		{
			if(value.fitsInInt())
			{
				return value.asInt();
			}
			
			if(value.fitsInLong())
			{
				return value.asLong();
			}
			
			if(value.fitsInFloat())
			{
				return value.asFloat();
			}
			
			return value.asDouble();
		}
		
		if(value.isHostObject())
		{
			return value.asHostObject();
		}
		
		if(value.hasArrayElements())
		{
			List<Object> list = new LinkedList<Object>();
			
			for(long i = 0; i < value.getArraySize(); i++)
			{
				list.add(unwrapObject(value.getArrayElement(i)));
			}
			
			return list;
		}
		
		if(value.hasMembers())
		{
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			
			for(String key : value.getMemberKeys())
			{
				map.put(key, unwrapObject(value.getMember(key)));
			}
			
			return map;
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static Object standardizeValue(Object input)
	{
		if(input == null)
		{
			return null;
		}
		
		if(input instanceof WrapperList)
		{
			input = ((WrapperList<Object>) input).getObjects();
		}
		
		if(input instanceof Value)
		{
			return standardizeValue(unwrapObject(input));
		}
		
		if(input instanceof Document)
		{
			return documentToStandardMap((Document) input);
		}
		
		if(input instanceof MongoDocList)
		{
			return standardizeDocList((MongoDocList) input);
		}
		
		if(input instanceof MongoAggrDocList)
		{
			return standardizeAggrDocList((MongoAggrDocList) input);
		}
		
		if(input instanceof List)
		{
			List<Object> res = new ArrayList<Object>();
			
			for(Object item : (List<Object>) input)
			{
				res.add(standardizeValue(item));
			}
			
			return res;
		}
		
		if(input instanceof Map && !(input instanceof Document))
		{
			return fromBsonExtendedValue(input);
		}
		
		if(isStandardScalar(input))
		{
			return input;
		}
		
		try
		{
			String json = MongoMethods.toJson(input);
			return MongoMethods.OBJ_MAPPER.readValue(json, Object.class);
		}
		catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while standardizing value of type: " + input.getClass().getName(), ex);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Document mapToDocument(Map<String, Object> map)
	{
		map = (Map<String, Object>) unwrapObject(map);
		Document doc = new Document();
		
		for(Map.Entry<String, Object> entry : map.entrySet())
		{
			doc.put(entry.getKey(), toBsonValue(entry.getValue()));
		}
		
		return doc;
	}
	
	@SuppressWarnings("unchecked")
	private static Object toBsonValue(Object value)
	{
		if(value == null)
		{
			return null;
		}
		
		value = unwrapObject(value);
		
		if(value instanceof Date || value instanceof ObjectId || value instanceof Document)
		{
			return value;
		}
		
		if(value instanceof Map)
		{
			Map<String, Object> map = (Map<String, Object>) value;
			
			if(map.size() == 1 && map.containsKey("$oid"))
			{
				return new ObjectId(String.valueOf(map.get("$oid")));
			}
			
			if(map.size() == 1 && map.containsKey("$date"))
			{
				return toDateFromExtendedJson(map.get("$date"));
			}
			
			Document nested = new Document();
			
			for(Map.Entry<String, Object> entry : map.entrySet())
			{
				nested.put(entry.getKey(), toBsonValue(entry.getValue()));
			}
			
			return nested;
		}
		
		if(value instanceof List)
		{
			List<Object> res = new ArrayList<Object>();
			
			for(Object item : (List<Object>) value)
			{
				res.add(toBsonValue(item));
			}
			
			return res;
		}
		
		return value;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> documentToStandardMap(Document doc)
	{
		try
		{
			return (Map<String, Object>) MongoMethods.docToMap(doc);
		}
		catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while converting document to map", ex);
		}
	}
	
	private static List<Map<String, Object>> standardizeDocList(MongoDocList docList)
	{
		List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
		
		for(int i = 0; i < docList.size(); i++)
		{
			res.add(docList.get(i));
		}
		
		return res;
	}
	
	private static List<Map<String, Object>> standardizeAggrDocList(MongoAggrDocList docList)
	{
		List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
		
		for(int i = 0; i < docList.size(); i++)
		{
			res.add(docList.get(i));
		}
		
		return res;
	}
	
	@SuppressWarnings("unchecked")
	public static Object fromBsonExtendedValue(Object input)
	{
		if(input == null)
		{
			return null;
		}
		
		if(isStandardScalar(input))
		{
			return input;
		}
		
		if(input instanceof List)
		{
			List<Object> res = new ArrayList<Object>();
			
			for(Object item : (List<Object>) input)
			{
				res.add(fromBsonExtendedValue(item));
			}
			
			return res;
		}
		
		if(input instanceof Map && !(input instanceof Document))
		{
			Map<Object, Object> inputMap = (Map<Object, Object>) input;
			
			if(inputMap.size() == 1 && inputMap.containsKey("$date"))
			{
				return toDateFromExtendedJson(inputMap.get("$date"));
			}
			
			Map<String, Object> res = new LinkedHashMap<String, Object>();
			
			for(Map.Entry<Object, Object> entry : inputMap.entrySet())
			{
				res.put(String.valueOf(entry.getKey()), fromBsonExtendedValue(entry.getValue()));
			}
			
			return res;
		}
		
		return input;
	}
	
	static Date toDateFromExtendedJson(Object value)
	{
		if(value == null)
		{
			return null;
		}
		
		if(value instanceof Date)
		{
			return (Date) value;
		}
		
		if(value instanceof Number)
		{
			return new Date(((Number) value).longValue());
		}
		
		if(value instanceof Map)
		{
			Map<?, ?> map = (Map<?, ?>) value;
			
			if(map.size() == 1 && map.containsKey("$numberLong"))
			{
				return new Date(Long.parseLong(String.valueOf(map.get("$numberLong"))));
			}
		}
		
		if(value instanceof String)
		{
			String dateStr = (String) value;
			
			try
			{
				return new Date(Long.parseLong(dateStr));
			}
			catch(NumberFormatException ex)
			{
				try
				{
					return MongoMethods.ISODate(dateStr);
				}
				catch(Exception parseEx)
				{
					throw new IllegalStateException("Unable to parse BSON date string: " + dateStr, parseEx);
				}
			}
		}
		
		throw new IllegalStateException("Unable to convert value to Date. Type: " + value.getClass().getName());
	}
	
	private static boolean isStandardScalar(Object input)
	{
		return input instanceof String
				|| input instanceof Boolean
				|| input instanceof Character
				|| input instanceof Number
				|| input instanceof Date;
	}
	
	public static <T> T convert(Object input, Class<T> type)
	{
		if(input == null)
		{
			return null;
		}
		
		try
		{
			input = unwrapObject(input);
			String json = MongoMethods.toJson(input);
			
			return (T) MongoMethods.OBJ_MAPPER.readValue(json, type);
		}catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while converting input to specified type: " + type.getName(), ex);
		}
	}

}
