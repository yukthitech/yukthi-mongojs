package com.yukthitech.mongojs;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MongoJsUtils
{
	@SuppressWarnings("unchecked")
	public static Object unwrapObject(Object input)
	{
		if(input == null 
				|| (!input.getClass().getName().startsWith("com.oracle.truffle.polyglot") 
						&& !(input instanceof WrapperList)))
		{
			return input;
		}

		if(input instanceof WrapperList)
		{
			input = ((WrapperList<Object>) input).getObjects();
		}

		/*
		//if non nashron object is specified there is nothing to unwrap
		if(input == null || !input.getClass().getName().startsWith("com.oracle.truffle.polyglot"))
		{
			return input;
		}

		if(input instanceof JSObject)
		{
			JSObject jsObj = (JSObject) input;
			
			if(jsObj.isArray())
			{
				List<Object> res = new LinkedList<Object>();
				Map<Object, Object> inputMap = (Map<Object, Object>) input;
				int size = inputMap.size();
				
				for(int i = 0; i < size; i++)
				{
					res.add(unwrapObject(inputMap.get("" + i)));
				}
				
				return res;
			}
		}
		*/
		
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
