package com.llj.framework.io.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;

import com.llj.framework.io.PropertiesResourceParser;
import com.llj.framework.io.ResourceParseException;

/**
 * 
 * @author lu
 *
 */
public abstract class AbstractPropertiesResourceParser implements PropertiesResourceParser {

	private List<Properties> loadedPropertiesInstances = new ArrayList<Properties>();

	private String encoding;

	private ResourcePatternResolver resourceLoader = new PathMatchingResourcePatternResolver();

	public ResourcePatternResolver getResourceLoader() {
		return resourceLoader;
	}

	public void setResourceLoader(ResourcePatternResolver resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	public List<Properties> getLoadedPropertiesInstances() {
		return loadedPropertiesInstances;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public AbstractPropertiesResourceParser() {
	}

	public AbstractPropertiesResourceParser(String encoding, ResourcePatternResolver resourceLoader) {
		this.encoding = encoding;
		this.resourceLoader = resourceLoader;
	}

	public void loadProperties(Resource[] resources) throws ResourceParseException {
		if (resources != null) {
			for (Resource res : resources) {
				loadProperties(res);
			}
		}
	}

	private void loadProperties(Resource resource) throws ResourceParseException {
		InputStream is = null;
		try {
			PropertiesPersister propertiesPersister = new DefaultPropertiesPersister();
			Properties props = new Properties();
			is = resource.getInputStream();
			if (this.encoding != null) {
				propertiesPersister.load(props, new InputStreamReader(is, this.encoding));
			} else {
				propertiesPersister.load(props, is);
			}
			loadedPropertiesInstances.add(props);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new ResourceParseException("不支持以" + this.encoding + "编码格式来读取" + resource.getFilename() + "属性资源文件！");
		} catch (IOException e) {
			e.printStackTrace();
			throw new ResourceParseException("读取" + resource.getFilename() + "属性资源文件出错！");
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
