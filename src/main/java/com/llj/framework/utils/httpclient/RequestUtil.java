package com.llj.framework.utils.httpclient;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author lu
 *
 */
public class RequestUtil {

	/**
	 * 获取Client IP : 此方法能够穿透squid 和 proxy
	 *
	 * @param request
	 * @return .
	 */
	public static String getClientIpAddress(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (ip.indexOf(",") > 0)
			ip = ip.substring(0, ip.indexOf(","));
		return ip;
	}

}
