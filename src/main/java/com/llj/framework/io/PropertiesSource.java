package com.llj.framework.io;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;

/**
 * 
 * @author lu
 *
 */
public class PropertiesSource implements InitializingBean {

	private String path;

	private PropertiesResourceParser propertiesResourceParser;

	private static Map<String, String> props;

	/**
	 * 设置属性文件的解析器
	 * 
	 * @param propertiesResourceParser
	 */
	public void setPropertiesResourceParser(PropertiesResourceParser propertiesResourceParser) {
		this.propertiesResourceParser = propertiesResourceParser;
	}
	/**
	 * 设置属性文件的路径
	 * 
	 * @param path
	 */
	public void setPath(String path) {
		this.path = path;
	}

	private void reload() {
		try {
			props = propertiesResourceParser.parse(this.path);
		} catch (ResourceParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 如果PropertiesSource中含有key的属性信息，返回{@code true}，反之返回{@code false}。
	 * 
	 * @param key
	 * @return
	 */
	public static boolean containsKey(String key) {
		return props.containsKey(key);
	}

	/**
	 * 获得key对应的属性值
	 * 
	 * @param key
	 * @return
	 */
	public static String getProperty(String key) {
		return props.get(key);
	}

	/**
	 * 获得key对应的属性值，如果属性值为{@code null}，那么返回 {@code defaultValue}。
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public String getProperty(String key, String defaultValue) {
		return props.get(key) == null ? defaultValue : props.get(key);
	}

	/**
	 * 如果不含有任何的属性信息，返回{@code true}，反之返回{@code false}。
	 * 
	 * @return
	 */
	public static boolean isEmpty() {
		return props.isEmpty();
	}

	public void afterPropertiesSet() throws Exception {
		reload();
	}
}
