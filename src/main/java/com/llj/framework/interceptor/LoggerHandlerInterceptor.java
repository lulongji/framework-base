package com.llj.framework.interceptor;

import com.llj.framework.uuid.UUIDGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登陆过滤
 *
 * @author lu
 */
public class LoggerHandlerInterceptor extends HandlerInterceptorAdapter {
	/**
	 * 日志
	 */
	private static Logger logger = LogManager.getLogger(LoggerHandlerInterceptor.class.getName());

	/**
	 * Handler执行完成之后调用这个方法
	 */
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exc) throws Exception {

	}

	/**
	 * Handler执行之后，ModelAndView返回之前调用这个方法
	 */
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
	}

	/**
	 * Handler执行之前调用这个方法
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		try {
			ThreadContext.put("threadId", new UUIDGenerator().generate());
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return false;
	}
}
