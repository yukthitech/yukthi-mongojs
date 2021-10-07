package com.yukthitech.mongojs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.mongojs.common.MongoJsArguments;
import com.yukthitech.utils.cli.CommandLineOptions;
import com.yukthitech.utils.cli.MissingArgumentException;
import com.yukthitech.utils.cli.OptionsFactory;

/**
 * Main class which would do the db versioing.
 * @author akiran
 */
public class Main
{
	private static Logger logger = LogManager.getLogger(Main.class);
	
	private static final String COMMAND_SYNTAX = String.format("java %s args...", Main.class.getName());
	
	/**
	 * Parses and load command line arguments into {@link MongoJsArguments}
	 * @param args
	 * @return
	 */
	private static MongoJsArguments loadArguments(String args[])
	{
		CommandLineOptions commandLineOptions = OptionsFactory.buildCommandLineOptions(MongoJsArguments.class);
		MongoJsArguments basicArguments = null;
		
		try
		{
			basicArguments = (MongoJsArguments) commandLineOptions.parseBean(args);
		}catch(MissingArgumentException ex)
		{
			System.err.println("Error: " + ex.getMessage());
			System.err.println(commandLineOptions.fetchHelpInfo(COMMAND_SYNTAX));
			
			System.exit(-1);
		}catch(Exception ex)
		{
			ex.printStackTrace();

			System.err.println("Error: " + ex.getMessage());
			System.err.println(commandLineOptions.fetchHelpInfo(COMMAND_SYNTAX));
			
			System.exit(-1);
		}
		
		return basicArguments;
	}
	
	public static int execute(String[] args)
	{
		MongoJsArguments argumentBean = loadArguments(args);
		return 0;
	}

	public static void main(String[] args)
	{
		int exitCode = execute(args);
		System.exit(exitCode);
	}
}
