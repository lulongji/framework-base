package com.llj.framework.log;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * 
 * @author lu
 *
 */
public interface ILogAspect {

	/**
	 * aop 代理接口操作
	 * 
	 * @param pjp
	 * @return
	 * @throws Throwable
	 */
	public Object doAround(ProceedingJoinPoint pjp) throws Throwable;
}
