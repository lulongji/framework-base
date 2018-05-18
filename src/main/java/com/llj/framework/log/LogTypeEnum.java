package com.llj.framework.log;

/**
 *
 * 日志追踪系统中的日志类型包括服务类和资源类 Created by lu on 2015/11/26.
 */
public enum LogTypeEnum {

	HTTP("HTTP_LOG"), RPC("RPC_LOG"), METHOD("METHOD_LOG"), // 方法内部的日志追踪
	RESOURCE_REDIS("RESOURCE_REDIS_LOG"), RESOURCE_MYSQL("RESOURCE_MYSQL_LOG"), RESOURCE_MC("RESOURCE_MC_LOG"), UNKOWN("UNKNOM_TYPE");

	private String value;

	private LogTypeEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
