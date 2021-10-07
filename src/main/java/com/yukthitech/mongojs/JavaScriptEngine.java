package com.yukthitech.mongojs;

import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Service to execute java-script.
 * @author akiran
 */
public class JavaScriptEngine
{
	/**
	 * Nahron Engine to execute java-script.
	 */
	private ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
	
	/**
	 * Executes specified script. 
	 * In script, entries specified in param-map are accessible as global variables.
	 * @param script
	 * @param paramMap
	 */
	public void executeScript(String script, Map<String, Object> paramMap)
	{
		if(StringUtils.isBlank(script))
		{
			throw new NullPointerException("Script is null or empty");
		}
		
		//logger.trace("Executing script: {}\nParameters: {}", script, paramMap);
		
		Bindings bindings = engine.createBindings();
		bindings.putAll(paramMap);
		
		try
		{
			engine.eval(script, bindings);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while executing below script:\n{}", script, ex);
		}
	}
}
