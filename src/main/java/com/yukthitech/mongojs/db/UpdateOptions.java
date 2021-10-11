package com.yukthitech.mongojs.db;

public class UpdateOptions
{
	private boolean upsert;
	
	private boolean multi;

	public boolean isUpsert()
	{
		return upsert;
	}

	public void setUpsert(boolean upsert)
	{
		this.upsert = upsert;
	}

	public boolean isMulti()
	{
		return multi;
	}

	public void setMulti(boolean multi)
	{
		this.multi = multi;
	}
	
	public com.mongodb.client.model.UpdateOptions toMongoOptions()
	{
		com.mongodb.client.model.UpdateOptions options = new com.mongodb.client.model.UpdateOptions();
		options.upsert(upsert);
		
		return options;
	}
}
