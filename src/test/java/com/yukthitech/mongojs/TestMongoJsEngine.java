package com.yukthitech.mongojs;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.yukthitech.ccg.xml.XMLBeanParser;
import com.yukthitech.mongojs.db.JsMongoDatabase;

public class TestMongoJsEngine
{
	private MongoJsEngine mongoJsEngine;
	
	private Object[][] loadXmlFile(String file) throws Exception
	{
		TestConfig beans = new TestConfig();
		XMLBeanParser.parse(TestMongoJsEngine.class.getResourceAsStream(file), beans);
		
		int size = beans.getScripBeans().size();
		Object[][] rows = new Object[size][];
		
		for(int i = 0; i < size; i++)
		{
			rows[i] = new Object[] {beans.getScripBeans().get(i)};
		}
	
		this.mongoJsEngine = new MongoJsEngine(beans.getMongoJsArguments());
		
		this.mongoJsEngine.loadClassMethods(getClass());
		return rows;
	}
	
	public static void assertEquals(JsMongoDatabase database, Object actual, Object expected)
	{
		Assert.assertEquals(actual, expected);
	}
	
	@DataProvider(name = "jsonElDataProvider")
	public Object[][] getTestData() throws Exception
	{
		return loadXmlFile("/script-test-data.xml");
	}

	@Test(dataProvider =  "jsonElDataProvider")
	public void testJel(ScriptBean bean) throws Exception
	{
		mongoJsEngine.executeScript(bean.getTestScript());
	}
}
