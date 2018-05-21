package com.llj.framework.page;

import java.io.Serializable;

/**
 * JSON返回对象
 * 
 * @author lu
 */
public class JsonResult implements Serializable {

	private static final long serialVersionUID = 3178456659833086583L;
	/** 状态码 */
	private String code;

	/** 信息 */
	private String info;

	/** 返回内容 */
	private Object result;

	public static JsonResult success() {
		JsonResult result = new JsonResult();
		result.setCode("200");
		result.setInfo("success");
		return result;
	}

	/**
	 * @desc 创建成功结果集
	 * @param result
	 * @return
	 */
	public static JsonResult success(Object result) {
		JsonResult res = new JsonResult("200", "success");
		res.setResult(result);
		return res;
	}

	public static JsonResult failure() {
		JsonResult result = new JsonResult();
		result.setCode("500");
		result.setInfo("失败");
		return result;
	}

	public static JsonResult failure(String info) {
		JsonResult result = failure();
		result.setInfo(info);
		return result;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	/**
	 * @param code
	 * @param info
	 * @param result
	 */
	private JsonResult(String code, String info, Object result) {
		super();
		this.code = code;
		this.info = info;
		this.result = result;
	}

	/**
	 * @param code
	 * @param info
	 */
	private JsonResult(String code, String info) {
		super();
		this.code = code;
		this.info = info;
	}

	/**
	 * 
	 */
	private JsonResult() {
		super();
	}
}
