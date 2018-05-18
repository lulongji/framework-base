package com.llj.framework.hessian;

import com.caucho.hessian.client.HessianProxy;
import com.llj.framework.log.BaseResourceLog;
import com.llj.framework.log.LogContext;
import com.llj.framework.log.LogFoot;
import com.llj.framework.log.LogTypeEnum;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;

/**
 * 
 * @author lu
 *
 */
public class QDHessianProxy extends HessianProxy {

	QDHessianProxy(QDHessianProxyFactory factory, URL url) {
		super(url, factory);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		BaseResourceLog resourceLog = new BaseResourceLog();
		LogFoot logFoot = new LogFoot(LogTypeEnum.RPC.getValue());
		logFoot.setRequestPath(this.getURL().toString());
		doBefore(method, args, resourceLog, logFoot);
		Object o = null;
		try {
			o = super.invoke(proxy, method, args);
			doAfterSucess(method, args, resourceLog, logFoot, o);
		} catch (Exception e) {
			doAfterFail(method, args, resourceLog, logFoot, e);
			throw new Exception("RPC method " + method.getName() + " error" + e);
		}
		return o;
	}

	@Override
	protected void addRequestHeaders(URLConnection conn) {
		LogContext logContext = LogContext.get();
		if (logContext != null) {
			if (logContext.getRequestId() != null)
				conn.setRequestProperty("requestid", logContext.getRequestId());
			if (logContext.getSequence() != null)
				conn.setRequestProperty("sequence",
						logContext.getSequence_prefix() == null
								? String.valueOf(logContext.getSequence().intValue() - 1)
								: logContext.getSequence_prefix() + "_" + String.valueOf(logContext.getSequence().intValue() - 1));
			if (logContext.getUid() != null)
				conn.setRequestProperty("uid", logContext.getUid());
		}
	}

	public void doBefore(Method method, Object[] args, BaseResourceLog resourceLog, LogFoot logFoot) {
		resourceLog.doBeforeService(logFoot);
	}

	public void doAfterSucess(Method method, Object[] args, BaseResourceLog resourceLog, LogFoot logFoot, Object o) {
		resourceLog.doAferService(logFoot, method.getName(), true, args, o);
	}

	public void doAfterFail(Method method, Object[] args, BaseResourceLog resourceLog, LogFoot logFoot, Exception e) {
		resourceLog.doAferService(logFoot, method.getName(), false, args, e);
	}

}
