package com.llj.framework.hessian;

import org.springframework.remoting.caucho.HessianServiceExporter;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.util.NestedServletException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

/**
 * 
 * @author lu
 *
 */
public class QDHessianServiceExporter extends HessianServiceExporter {

	/**
	 * Processes the incoming Hessian request and creates a Hessian response.
	 */
	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		if (!"POST".equals(request.getMethod())) {
			throw new HttpRequestMethodNotSupportedException(request.getMethod(), new String[]{"POST"},
					"HessianServiceExporter only supports POST requests");
		}

		handleHessianHeader(request);

		response.setContentType(CONTENT_TYPE_HESSIAN);
		try {
			invoke(request.getInputStream(), response.getOutputStream());
		} catch (Throwable ex) {
			throw new NestedServletException("Hessian skeleton invocation failed", ex);
		}
	}

	private void handleHessianHeader(HttpServletRequest request) {
		QDHessianHeaderContext.clear();
		Enumeration enumeration = request.getHeaderNames();
		QDHessianHeader hessianHeader = new QDHessianHeader();
		while (enumeration.hasMoreElements()) {
			String name = enumeration.nextElement().toString();
			String value = request.getHeader(name);
			hessianHeader.addHeader(name, value);
		}
		QDHessianHeaderContext.SetHessianHeader(hessianHeader);
	}

}
