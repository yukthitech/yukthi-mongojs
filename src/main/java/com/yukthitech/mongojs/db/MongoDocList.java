package com.yukthitech.mongojs.db;

import java.util.AbstractList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.graalvm.polyglot.Value;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.yukthitech.mongojs.MongoJsUtils;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class MongoDocList extends AbstractList<Document>
{
	private FindIterable<Document> documentIt;
	
	private List<Document> listView;
	
	private MongoCursor<Document> docCursor;
	
	public MongoDocList(FindIterable<Document> documentIt)
	{
		this.documentIt = documentIt;
	}

	public void forEach(Value callback)
	{
		try
		{
			for(Document doc : documentIt)
			{
				callback.execute(doc);
			}
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while invoking callback. Error: " + ex, ex);
		}
	}
	
	@SuppressWarnings("unchecked")
	public MongoDocList sort(Map<String, Object> criteria)
	{
		criteria = (Map<String, Object>) MongoJsUtils.unwrapObject(criteria);
		documentIt.sort(new Document(criteria));
		
		return this;
	}
	
	public MongoDocList limit(int limit)
	{
		documentIt.limit(limit);
		return this;
	}
	
	public Document first()
	{
		return documentIt.first();
	}
	
	private MongoCursor<Document> getCursor()
	{
		if(this.docCursor != null)
		{
			return docCursor;
		}
		
		return (docCursor = documentIt.cursor());
	}
	
	public boolean hasNext()
	{
		return getCursor().hasNext();
	}
	
	public Document next()
	{
		return getCursor().next();
	}
	
	public boolean getHasNext()
	{
		return getCursor().hasNext();
	}
	
	public Document getNext()
	{
		return getCursor().next();
	}
	
	private List<Document> listView()
	{
		if(listView != null)
		{
			return listView;
		}
		
		List<Document> lst = new LinkedList<Document>();
		
		for(Document doc : documentIt)
		{
			lst.add(doc);
		}
		
		this.listView = lst;
		return lst;
	}

	@Override
	public Document get(int index)
	{
		return listView().get(index);
	}

	@Override
	public int size()
	{
		return listView().size();
	}
}
