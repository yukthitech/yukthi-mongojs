package com.yukthitech.mongojs;

public class ScriptBean
{
	private String name;
	
	private String testScript;
	
	private boolean exceptionExpected;
	
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getTestScript()
	{
		return testScript;
	}

	public void setTestScript(String testScript)
	{
		this.testScript = testScript;
	}
	
	public boolean isExceptionExpected()
	{
		return exceptionExpected;
	}

	public void setExceptionExpected(boolean exceptionExpected)
	{
		this.exceptionExpected = exceptionExpected;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return name;
	}

}
