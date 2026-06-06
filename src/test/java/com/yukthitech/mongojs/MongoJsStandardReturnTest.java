package com.yukthitech.mongojs;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.graalvm.polyglot.Value;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.yukthitech.ccg.xml.XMLBeanParser;
import com.yukthitech.mongojs.common.MongoJsArguments;

public class MongoJsStandardReturnTest
{
	private MongoJsEngine mongoJsEngine;
	
	@BeforeClass
	public void setUp() throws Exception
	{
		TestConfig beans = new TestConfig();
		XMLBeanParser.parse(MongoJsStandardReturnTest.class.getResourceAsStream("/script-test-data.xml"), beans);
		
		MongoJsArguments args = beans.getMongoJsArguments();
		mongoJsEngine = new MongoJsEngine(args);
		mongoJsEngine.loadClassMethods(TestMongoJsEngine.class);
		
		mongoJsEngine.executeScript(
				"db.MJS_TEST1.deleteMany({});"
				+ "db.MJS_TEST1.insert({\"name\": \"testName\", \"key\": \"k1\", \"value\": 1});");
	}
	
	@Test
	public void testFindOneReturnsMap()
	{
		Object result = mongoJsEngine.executeScript("db.MJS_TEST1.findOne({\"name\": \"testName\"});");
		
		Assert.assertFalse(result instanceof Value);
		Assert.assertTrue(result instanceof Map);
	}
	
	@Test
	public void testFindReturnsListOfMaps()
	{
		Object result = mongoJsEngine.executeScript("db.MJS_TEST1.find({\"name\": \"testName\"});");
		
		Assert.assertFalse(result instanceof Value);
		Assert.assertTrue(result instanceof List);
		
		List<?> list = (List<?>) result;
		Assert.assertFalse(list.isEmpty());
		Assert.assertTrue(list.get(0) instanceof Map);
	}
	
	@Test
	public void testPrimitiveReturn()
	{
		Object result = mongoJsEngine.executeScript("42;");
		
		Assert.assertFalse(result instanceof Value);
		Assert.assertEquals(result, 42);
	}
	
	@Test
	public void testStringReturn()
	{
		Object result = mongoJsEngine.executeScript("'hello';");
		
		Assert.assertFalse(result instanceof Value);
		Assert.assertEquals(result, "hello");
	}
	
	@Test
	public void testNullReturn()
	{
		Object result = mongoJsEngine.executeScript("db.MJS_TEST1.findOne({\"name\": \"nonexistent\"});");
		
		Assert.assertNull(result);
	}
	
	@Test
	public void testFindOneReturnsDateAsJavaUtilDate()
	{
		mongoJsEngine.executeScript(
				"db.MJS_TEST1.deleteMany({\"name\": \"d1\"});"
				+ "db.MJS_TEST1.insert({\"name\": \"d1\", \"createdAt\": now()});");
		
		@SuppressWarnings("unchecked")
		Map<String, Object> result = (Map<String, Object>) mongoJsEngine.executeScript(
				"db.MJS_TEST1.findOne({\"name\": \"d1\"});");
		
		Assert.assertNotNull(result);
		Assert.assertTrue(result.get("createdAt") instanceof Date);
		Assert.assertFalse(result.get("createdAt") instanceof Map);
	}
	
	@Test
	public void testDateRoundTripQuery()
	{
		mongoJsEngine.executeScript(
				"db.MJS_TEST1.deleteMany({\"name\": \"d2\"});"
				+ "var createdAt = now();"
				+ "db.MJS_TEST1.insert({\"name\": \"d2\", \"createdAt\": createdAt});"
				+ "var doc = db.MJS_TEST1.findOne({\"name\": \"d2\"});"
				+ "var found = db.MJS_TEST1.findOne({\"name\": \"d2\", \"createdAt\": doc.createdAt});"
				+ "assertEquals(found.name, \"d2\");");
	}
	
	@Test
	public void testExtendedJsonDateInUpdate()
	{
		@SuppressWarnings("unchecked")
		Map<String, Object> updated = (Map<String, Object>) mongoJsEngine.executeScript(
				"db.MJS_TEST1.deleteMany({\"name\": \"d3\"});"
				+ "var createdAt = now();"
				+ "db.MJS_TEST1.insert({\"name\": \"d3\", \"createdAt\": createdAt});"
				+ "var doc = db.MJS_TEST1.findOne({\"name\": \"d3\"});"
				+ "db.MJS_TEST1.update({\"name\": \"d3\"}, {\"$set\": {\"updatedAt\": doc.createdAt}});"
				+ "db.MJS_TEST1.findOne({\"name\": \"d3\"});");
		
		Assert.assertNotNull(updated);
		Assert.assertTrue(updated.get("updatedAt") instanceof Date);
	}
}
