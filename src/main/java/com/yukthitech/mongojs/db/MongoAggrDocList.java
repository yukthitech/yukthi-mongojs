package com.yukthitech.mongojs.db;

import java.util.AbstractList;
import java.util.LinkedList;
import java.util.List;

import org.bson.Document;
import org.graalvm.polyglot.Value;

import com.mongodb.client.AggregateIterable;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class MongoAggrDocList extends AbstractList<Document>
{
	private AggregateIterable<Document> documentIt;
	
	private List<Document> listView;
	
	public MongoAggrDocList(AggregateIterable<Document> documentIt)
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
			throw new InvalidStateException("An error occurred while invoking callback", ex);
		}
	}
	
	public Document first()
	{
		return documentIt.first();
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
