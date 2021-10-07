package com.yukthitech.mongojs;

public class ScriptBean
{
	private String name;
	
	private String testScript;
	
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
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return name;
	}

}
