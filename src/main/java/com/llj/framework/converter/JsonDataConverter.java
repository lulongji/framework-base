package com.llj.framework.converter;

import com.alibaba.fastjson.serializer.ValueFilter;

public class JsonDataConverter implements ValueFilter {

	public Object process(Object object, String name, Object value) {
	    return value == null ? "" : value;
	}
}
