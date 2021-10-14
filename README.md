# yukthi-mongojs

This library is a wrapper over mongo-driver and provides ability to execute java-script queries (like monog-shell) within java code. Interall this uses "nashorn" javascript engine.

As of version 0.1 most of read and DML query flavors are supported. DDL is not yet added.

### Mongo queries supported

* **insert(docToInsert)** - Insert the document into target collection.
    * *Example*: db.getCollection("MJS_TEST1").insert({"key": "k1", "value": 1})

* **insertMany(docList)** - Insert multiple document into target collection.
    * *Example*: 
       ```javascript
    		db.MJS_TEST1.insertMany([  
			{"key": "k1", "value": 1},  
			{"key": "k2", "value": 2},  
			{"key": "k3", "value": 3},  
			{"key": "k4", "value": 4}  
		])
       ```
	
* **update(filter, updates)** - To update single document specified by filter.
    * *Example*: 
    	```javascript
		db.MJS_TEST1.update(  
			{"name": "testName", "list.key": "k1"},  
			{"$set": {"list.$.value": "val1-mod"}}  
		)
	```
* **update(filter, updates, options)** - To update document(s) specified by filter along with options. Supported options - multi, upsert
    * *Example*: 
 	```javascript
		db.MJS_TEST1.update(  
			{},  
			{"$set": {"val2": 1}}, {"multi": true}  
		)
	```
* **deleteOne(filter)** - Deletes single document specified by filter.
    * *Example*: db.MJS_TEST1.deleteOne({"name": "testName"})
* **deleteMany(filter)** - Deletes all matching document(s) specified by filter.
    * *Example*: db.MJS_TEST1.deleteMany({})
* **findOne(filter)** - Fetches first document specified by filter.
    * *Example*: db.MJS_TEST1.findOne({"name": "testName"})
* **findeOne(filter, projection)** - Fetches first document specified by filter and projection.
    * *Example*: 
	```javascript
		db.MJS_TEST1.findOne(  
			{"name": "testName"},  
			{  
				"name": 1,  
				"age": 1,  
				"list": {"$elemMatch": {"key": "k1"}}  
			})
	```
* **find(filter)** - Fetches all documents specified by filter.
    * *Example*: db.MJS_TEST1.find({})
* **find(filter, projection)** - Fetches all documents specified by filter and projection.
    * *Example*: 
    	```javascript
    		db.MJS_TEST1.findOne(  
			{},  
			{  
				"name": 1,  
				"age": 1,  
				"list": {"$elemMatch": {"key": "k1"}}  
			}
		)
	```
**For Each document flow**
The find() query results can be extended to process the document using javascript as shown below:  
```javascript
	db.MJS_TEST1.find({}).forEach(function(doc) {  
		db.MJS_TEST1.update(  
			{"_id": doc["_id"]},   
			{"$set": {"value": NumberInt(doc.value + 10)}}  
		);  
	});
```

### Extra function support

Below methods are accessible within scripts to support mongo-shell like functions and also for ease of commonly using functionality:  

Function                                                                                  |         Description
------------------------------------------------------------------------------------------|-----------------------------
ObjectId ObjectId(String id)                                                              |  Creates mongo object id which can be used while adding conditions on id column.
NumberLong(Object val)                                                                    |  Converts specified value into long value. Input can be number or number in string format.
NumberInt(Object val)                                                                     | Converts specified value into int value. Input can be number or number in string format.
ISODate(String dateStr)                                                                   | Converts specified ISO date string value into date. ISO format: yyyy-MM-dd'T'HH:mm:ss.SSSXXX. Eg: 2021-10-07T04:42:20.217-06:30
date(String dateStr)                                                                      |  Helps in creating simple date object. Input date string should of format yyyy-MM-dd. Eg: 2021-10-14
now()                                                                                     | Gets the current data/time object.
String fetchId(JsMongoDatabase mongoDb, String collectionName, String fld, Object value)  | A utility function to get _id field value from specified collection, with specified field and field-value combination.
void deleteProperty(Map<String, Object> obj, String attrName)                             | "delete" of standard javascript may not work in this scripts. In such cases, this function can be used to remove "attrName" from "obj" map.






    
