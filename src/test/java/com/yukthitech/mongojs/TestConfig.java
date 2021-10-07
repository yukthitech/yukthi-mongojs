package com.yukthitech.mongojs;

import java.util.ArrayList;
import java.util.List;

import com.yukthitech.mongojs.common.MongoJsArguments;

public class TestConfig
{
	private List<ScriptBean> scripBeans = new ArrayList<ScriptBean>();
	
	private MongoJsArguments mongoJsArguments;
	
	public void addScriptBean(ScriptBean bean)
	{
		this.scripBeans.add(bean);
	}

	public List<ScriptBean> getScripBeans()
	{
		return scripBeans;
	}

	public MongoJsArguments getMongoJsArguments()
	{
		return mongoJsArguments;
	}

	public void setMongoJsArguments(MongoJsArguments mongoJsArguments)
	{
		this.mongoJsArguments = mongoJsArguments;
	}
}
