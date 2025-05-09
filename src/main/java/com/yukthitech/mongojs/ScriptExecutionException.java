package com.yukthitech.mongojs;

import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.PolyglotException.StackFrame;

import com.yukthitech.utils.exceptions.UtilsException;

public class ScriptExecutionException extends UtilsException
{
	private static final long serialVersionUID = 1L;

	public ScriptExecutionException(String message, Object... args)
	{
		super(message, args);
	}
	
	public static ScriptExecutionException newException(String message, Object... args)
	{
		Throwable th = UtilsException.getRootCause(args);
		
		if(!(th instanceof PolyglotException))
		{
			return new ScriptExecutionException(message, args);
		}
		
		PolyglotException polyEx = (PolyglotException) th;
		
		if(!polyEx.isHostException())
		{
			return new ScriptExecutionException(message, args);
		}
		
		Throwable actualEx = polyEx.asHostException();
		StringBuilder scriptStackTrace = new StringBuilder("Script Stack Trace:\n");
		
		for(StackFrame elem : polyEx.getPolyglotStackTrace())
		{
			if(elem.getSourceLocation() != null)
			{
				scriptStackTrace
					.append("\t")
					.append(elem.toHostFrame().toString())
					.append("\n");
			}
		}
		
		String finalMessage = UtilsException.buildMessage(message, args) + "\n" + scriptStackTrace;
		return new ScriptExecutionException(finalMessage, actualEx);
	}
}
