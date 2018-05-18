package com.llj.framework.log;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * @author lu
 *
 */
public class APILogRecord {
	static final String pattern = "yyyy-MM-dd HH:mm:ss,S";
	static final String SPLIT = "\t";
	static final String preifx = "API_LOG ";
	private Date date;
	private String api;

	/**
	 * http method
	 */
	private String method;

	/**
	 * http response status
	 */
	private int responseStatus;

	/**
	 * 来源 appkey
	 */
	private String source;

	private String uid;

	/**
	 * 请求参数
	 */
	private Map<String, String[]> parameters = new LinkedHashMap<String, String[]>();

	/**
	 * 扩展属性
	 */
	private ExtAttr extAttr;

	// TODO
	// /**
	// * 调用方ip，如果是 内网服务端调用，该ip是服务器ip， 否则该ip与用户ip一致
	// */
	// private String clientIp;

	/**
	 * 用户ip,如果是内网服务器端调用，该ip是调用方通过 Api-RemoteIP机制传递的用户ip
	 */
	private String ip;

	/**
	 * 接口响应使用时间
	 */
	private long useTime;

	/**
	 * API请求响应response
	 */
	private String response;

	public APILogRecord() {
		this.extAttr = new ExtAttr();
	}

	public void setRequestFileds(Date data, String api, String method, String source, String uid, String ip) {
		this.setDate(data);
		this.setApi(api);
		this.setMethod(method);
		this.setSource(source);
		this.setUid(uid);
		this.setIp(ip);
	}

	@Deprecated
	public void setResponseFileds(int responseStatus, long usetime) {
		this.setResponseStatus(responseStatus);
		this.setUseTime(usetime);
	}

	public void setResponseFileds(int responseStatus, long usetime, String response) {
		this.setResponseStatus(responseStatus);
		this.setUseTime(usetime);
		this.setResponse(response);
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getApi() {
		return api;
	}

	public void setApi(String api) {
		this.api = api;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public int getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(int responseStatus) {
		this.responseStatus = responseStatus;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Map<String, String[]> getParameters() {
		return parameters;
	}

	public void putParameters(String key, String[] value) {
		this.parameters.put(key, value);
	}

	public void setExtAttr(ExtAttr extAttr) {
		this.extAttr = extAttr;
	}

	public ExtAttr getExtAttr() {
		return extAttr;
	}

	public long getUseTime() {
		return useTime;
	}

	public void setUseTime(long useTime) {
		this.useTime = useTime;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String print() {
		StringBuilder buf = new StringBuilder();
		buf.append(preifx);
		buf.append(DateFormatUtils.format(date, pattern));
		buf.append(SPLIT);
		buf.append(api);
		buf.append(SPLIT);
		buf.append(this.method);
		buf.append(SPLIT);
		buf.append(this.responseStatus);
		buf.append(SPLIT);
		if (this.useTime <= 0) {
			this.useTime = System.currentTimeMillis() - this.date.getTime();
		}
		buf.append(this.useTime);
		buf.append(SPLIT);
		buf.append(StringUtils.isBlank(source) ? "unknow" : source);
		buf.append(SPLIT);
		buf.append(uid);
		buf.append(SPLIT);
		for (Entry<String, String[]> e : this.parameters.entrySet()) {
			String key = e.getKey();
			String[] values = e.getValue();
			for (String value : values) {
				buf.append(key).append("=").append(value);
				buf.append("&");
			}
		}
		if (buf.charAt(buf.length() - 1) == '&') {
			buf.deleteCharAt(buf.length() - 1);
		}
		buf.append(SPLIT);
		buf.append(this.extAttr.toString());
		buf.append(SPLIT);
		buf.append(this.ip);
		return buf.toString();
	}

	/**
	 * 
	 * 
	 * 项目名称：framework-common 类名称：ExtAttr 类描述： request内部属性类 创建人：Administrator
	 * 创建时间：2015年6月8日 下午3:33:05 修改人：Administrator 修改时间：2015年6月8日 下午3:33:05 修改备注：
	 * 
	 * @version
	 *
	 */
	class ExtAttr {

		private Map<String, Object> extAttr = new HashMap<String, Object>();

		@SuppressWarnings("unchecked")
		public <T> T getAttr(String name) {
			return (T) this.extAttr.get(name);
		}

		public ExtAttr setAttr(String name, Object value) {
			this.extAttr.put(name, value);
			return this;
		}

		@Override
		public String toString() {
			if (extAttr.isEmpty()) {
				return "";
			}
			StringBuilder buffer = new StringBuilder();
			for (Entry<String, Object> entry : extAttr.entrySet()) {
				String key = entry.getKey();
				if (key == null) {
					continue;
				}
				String value = entry.getValue() == null ? "null" : entry.getValue().toString();
				buffer.append(key).append("=").append(value);
				buffer.append("&");
			}
			if (buffer.charAt(buffer.length() - 1) == '&') {
				buffer.deleteCharAt(buffer.length() - 1);
			}
			return buffer.toString();
		}
	}
}
