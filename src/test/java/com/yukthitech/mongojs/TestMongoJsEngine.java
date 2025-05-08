package com.yukthitech.mongojs;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.yukthitech.ccg.xml.XMLBeanParser;

public class TestMongoJsEngine
{
	private Object[][] loadXmlFile(String file) throws Exception
	{
		TestConfig beans = new TestConfig();
		XMLBeanParser.parse(TestMongoJsEngine.class.getResourceAsStream(file), beans);
		
		int size = beans.getScripBeans().size();
		Object[][] rows = new Object[size][];
		
		MongoJsEngine mongoJsEngine = new MongoJsEngine(beans.getMongoJsArguments());
		mongoJsEngine.loadClassMethods(getClass());
		
		for(int i = 0; i < size; i++)
		{
			rows[i] = new Object[] {beans.getScripBeans().get(i), mongoJsEngine};
		}
	
		return rows;
	}
	
	public static void assertEquals(Object actual, Object expected)
	{
		Assert.assertEquals(actual, expected);
	}
	
	@DataProvider(name = "jsonElDataProvider")
	public Object[][] getTestData() throws Exception
	{
		return loadXmlFile("/script-test-data.xml");
	}

	@Test(dataProvider =  "jsonElDataProvider")
	public void testMongoJs(ScriptBean bean, MongoJsEngine mongoJsEngine) throws Exception
	{
		mongoJsEngine.executeScript(bean.getTestScript());
	}
}
