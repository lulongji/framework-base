package com.llj.framework.utils.json;

import java.util.List;

import com.alibaba.fastjson.JSON;

/**
 * @author lu
 * @desc
 */
public class JsonUtil {

	/**
	 * Json解析JavaBean
	 * 
	 * @param json
	 * @param clazz
	 * @return
	 */
	public static <T> T JsonStr2Java(String json, Class<T> clazz) {
		return JSON.parseObject(json, clazz);
	}

	/**
	 * Json解析JavaBean list
	 * 
	 * @param json
	 * @param clazz
	 * @return
	 */
	public static <T> List<T> JsonStr2JavaList(String json, Class<T> clazz) {
		return JSON.parseArray(json, clazz);
	}

	/**
	 * JavaBean->json文本
	 * 
	 * @param t
	 * @return
	 */
	public static <T> String Java2Json(T t) {
		return JSON.toJSONString(t);
	}

	/**
	 * List<JavaBean>->json文本
	 * 
	 * @param t
	 * @return
	 */
	public static <T> String JavaList2Json(List<T> tagList) {
		return JSON.toJSONString(tagList);
	}
}
