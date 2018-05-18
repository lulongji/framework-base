package com.llj.framework.io;

import java.io.IOException;

import org.springframework.core.io.Resource;

/**
 * 本接口是属性文件资源解析的根接口，作为客户端访问的视图接口。
 * <p>
 * 本接口要求其实现类即可解析已加载的{@link Resource}类型的属性文件资源，同时也可解析指定路径下的属性文件资源。
 * 对于解析的结果可以返回包含属性信息的任何类型。如返回{@code Map<String,String>}
 * 类型结果的MapPropertiesResourceParser解析器。
 * </p>
 * 
 * @author lu
 * @version 1.0
 */
public interface PropertiesResourceParser {

	/**
	 * 解析指定的属性资源resources
	 * 
	 * @param resources
	 *            待解析的属性文件资源
	 * @return
	 * @throws ResourceParseException
	 *             解析属性资源时出现的异常
	 */
	<T> T parse(Resource[] resource) throws ResourceParseException;

	/**
	 * 解析path路径下的属性文件
	 * 
	 * @param path
	 *            待解析属性文件路径
	 * @return
	 * @throws ResourceParseException
	 *             解析属性资源时出现的异常
	 * @throws IOException
	 *             读取 path路径下的属性文件时抛出异常
	 */
	<T> T parse(String path) throws ResourceParseException, IOException;
}
