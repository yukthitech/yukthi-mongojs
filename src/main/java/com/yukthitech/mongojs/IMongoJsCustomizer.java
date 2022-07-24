package com.yukthitech.mongojs;

/**
 * Used to customize default behavior of mongo js engine.
 * @author akranthikiran
 */
public interface IMongoJsCustomizer
{
	
	/**
	 * Expected to handle the printing of specified log message to log.
	 * Should return true, if default behavior should be avoided.
	 *
	 * @param mssg the mssg to be printed
	 * 
	 * @return true if default printing behavior should be avoided.
	 */
	public default boolean printLog(String mssg)
	{
		return false;
	}
}
