package com.llj.framework.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

/**
 * 
 * @author lu
 *
 */
public class DateConverter implements Converter<String, Date> {

	private final String LONG_DATA_FMT = "yyyy-MM-dd HH:mm:ss";
	private final String SHORT_DATA_FMT = "yyyy-MM-dd";

	private String[] patterns = new String[]{LONG_DATA_FMT, SHORT_DATA_FMT};

	public Date convert(String text) {
		Date date = null;
		try {
			date = parseText(text);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return date;
	}

	private Date parseText(String text) throws IllegalArgumentException {
		if (!StringUtils.hasText(text))
			return null;
		Date date = null;
		for (String pattern : patterns) {
			SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
			dateFormat.setLenient(false);
			try {
				date = dateFormat.parse(text);
				break;
			} catch (ParseException e) {
				continue;
			}
		}
		if (date == null) {
			throw new IllegalArgumentException("解析日期失败：无法将待转换的字符串(" + text + ")转换为日期类型！");
		}
		return date;
	}

	public void setPatterns(String[] patterns) {
		this.patterns = patterns;
	}
}
