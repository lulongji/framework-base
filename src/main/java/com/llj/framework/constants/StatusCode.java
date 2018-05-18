package com.llj.framework.constants;

/**
 * 状态常量
 * 
 * @author lu
 */
public interface StatusCode {

	// 状态
	String SUCCESS = "success";

	String RESULT = "result";

	String FAILURE = "failure";

	String ERROR = "error";

	String SESSION_ERROR = "sessionerror";

	String SESSION_USER = "sessionuser";

	String MSG = "msg";

	Boolean TRUE = true;

	Boolean FALSE = false;

	// 状态码
	short CODE_SUCCESS = 200;

	short CODE_FAILURE = 500;

}
