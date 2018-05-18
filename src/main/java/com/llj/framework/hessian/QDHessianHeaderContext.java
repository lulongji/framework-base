package com.llj.framework.hessian;

public class QDHessianHeaderContext {
	private static final ThreadLocal<QDHessianHeader> THREAD_LOCAL = new ThreadLocal<>();

	public static QDHessianHeader getHessianHeader() {
		QDHessianHeader hessianHeader = THREAD_LOCAL.get();
		if (hessianHeader == null) {
			hessianHeader = new QDHessianHeader();
			THREAD_LOCAL.set(hessianHeader);
		}
		return hessianHeader;
	}

	public static void SetHessianHeader(QDHessianHeader hessianHeader) {
		THREAD_LOCAL.set(hessianHeader);
	}

	public static void clear() {
		THREAD_LOCAL.remove();
	}
}
