package com.yukthitech.mongojs;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jdk.nashorn.api.scripting.JSObject;

@SuppressWarnings("restriction")
public class MongoJsUtils
{
	@SuppressWarnings("unchecked")
	public static Object unwrapObject(Object input)
	{
		if(!(input instanceof JSObject))
		{
			return input;
		}
		
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

}
