package com.yukthitech.mongojs;

import java.util.Date;
import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Ensures Document → map conversion preserves Date epoch millis.
 * Regression: Document.toJson() relaxed ISO drops trailing fractional zeros
 * (e.g. 100ms → ".1Z"), and SimpleDateFormat mis-parsed those strings.
 */
public class DocToMapDateTest
{
	@Test
	public void testDateMillisPreservedForTruncatedFractionalSeconds() throws Exception
	{
		int[] millisSamples = { 0, 1, 10, 12, 100, 120, 123, 450, 500, 999 };
		
		for(int ms : millisSamples)
		{
			Date original = new Date(1_731_650_000_000L + ms);
			Document doc = new Document("time", original).append("name", "n");
			
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) MongoMethods.docToMap(doc);
			
			Object time = map.get("time");
			Assert.assertTrue(time instanceof Date, "ms=" + ms + " type=" + (time == null ? null : time.getClass()));
			Assert.assertEquals(((Date) time).getTime(), original.getTime(), "ms=" + ms);
		}
	}
	
	@Test
	public void testIsoDateParsesVariableFractionalSeconds() throws Exception
	{
		Assert.assertEquals(MongoMethods.ISODate("2024-11-15T05:53:20Z").getTime() % 1000, 0);
		Assert.assertEquals(MongoMethods.ISODate("2024-11-15T05:53:20.1Z").getTime() % 1000, 100);
		Assert.assertEquals(MongoMethods.ISODate("2024-11-15T05:53:20.12Z").getTime() % 1000, 120);
		Assert.assertEquals(MongoMethods.ISODate("2024-11-15T05:53:20.123Z").getTime() % 1000, 123);
	}
	
	@Test
	public void testNestedDateAndObjectIdPreserved() throws Exception
	{
		Date time = new Date(1_731_650_000_100L);
		ObjectId id = new ObjectId();
		Document doc = new Document("_id", id)
				.append("nested", new Document("time", time));
		
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) MongoMethods.docToMap(doc);
		
		Assert.assertEquals(map.get("_id"), id);
		
		@SuppressWarnings("unchecked")
		Map<String, Object> nested = (Map<String, Object>) map.get("nested");
		Assert.assertEquals(((Date) nested.get("time")).getTime(), time.getTime());
	}
	
	@Test
	public void testExtendedJsonDateStringStillParsed()
	{
		Map<String, Object> input = Map.of("$date", "2024-11-15T05:53:20.1Z");
		Object parsed = MongoJsUtils.fromBsonExtendedValue(input);
		
		Assert.assertTrue(parsed instanceof Date);
		Assert.assertEquals(((Date) parsed).getTime() % 1000, 100);
	}
}
