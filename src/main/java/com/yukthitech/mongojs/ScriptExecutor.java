package com.yukthitech.mongojs;

import java.io.File;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;

import com.yukthitech.mongojs.common.MongoJsArguments;
import com.yukthitech.utils.cli.CommandLineOptions;
import com.yukthitech.utils.cli.MissingArgumentException;
import com.yukthitech.utils.cli.OptionsFactory;

/**
 * Main class which can be used to execute script files from command line.
 * @author akiran
 */
public class ScriptExecutor
{
	private static final String COMMAND_SYNTAX = String.format("java %s args...", ScriptExecutor.class.getName());
	
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
	
	public static void execute(String[] args) throws Exception
	{
		MongoJsArguments argumentBean = loadArguments(args);
		MongoJsEngine mongoJsEngine = new MongoJsEngine(argumentBean);
		
		String script = FileUtils.readFileToString(new File(argumentBean.getFile()), Charset.defaultCharset());
		
		mongoJsEngine.executeScript(script);
	}

	public static void main(String[] args) throws Exception
	{
		execute(args);
		System.exit(0);
	}
}
