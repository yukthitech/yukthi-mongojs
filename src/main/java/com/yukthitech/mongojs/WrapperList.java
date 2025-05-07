package com.yukthitech.mongojs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WrapperList<T>
{
	private List<T> objects;

	public WrapperList(Collection<? extends T> c)
	{
		this.objects = new ArrayList<>(c);
	}
	
	public List<T> getObjects()
	{
		return objects;
	}
}
