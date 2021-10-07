package com.yukthitech.mongojs;

@FunctionalInterface
public interface JsMethod
{
	public Object call(Object... args);
}
