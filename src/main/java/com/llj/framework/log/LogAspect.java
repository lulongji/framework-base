package com.llj.framework.log;

import com.llj.framework.hessian.QDHessianHeaderContext;
import com.llj.framework.utils.httpclient.RequestUtil;
import com.llj.framework.utils.json.FastjsonUtils;
import com.llj.framework.utils.server.IPAddrUtil;
import com.llj.framework.utils.server.JVMUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 日志输出切面实现：
 * 
 * @author lu
 *
 */

public abstract class LogAspect implements ILogAspect {
	private static Logger logger = LogManager.getLogger("tracer");
	private String requestPath = null; // 请求地址
	private String userName = null; // 用户名
	private Map<?, ?> inputParamMap = null; // 传入参数
	private Map<String, Object> outputParamMap = null; // 存放输出结果
	private long startTimeMillis = 0; // 开始时间
	private long endTimeMillis = 0; // 结束时间
	private String type = null; // HTTP_LOG\RPC_LOG\API_LOG
								// RESOURCE_LOG:REDIS_LOG\MYSQL_LOG\MC_LOG
	private String method = null; // 调用的方法名

	/**
	 *
	 * @Title：doAround
	 * @Description: 环绕触发
	 * @param pjp
	 * @return
	 * @throws Throwable
	 */
	// @Around("execution(* com.llj.*.controller..*.*(..)) ||"
	// +"execution(* com.llj.*.common.service..*.*(..)) "
	// +"execution(* com.alibaba.druid.proxy..*.*(..)) ||"
	// +"execution(* com.alibaba.druid.sql..*.*(..)) ||"
	// +"execution(* com.alibaba.druid.stat..*.*(..)) ||"
	// +"execution(* com.alibaba.druid.support..*.*(..)) ||"
	// +"execution(* org.apache.ibatis.executor..*.*(..)) ||"
	// +"execution(* com.llj.framework.common.redis..*(..))||"
	// +"execution(* com.llj.framework.common.cache..*(..))||"
	// +"execution(* org.apache.ibatis.session.SqlSession.*(..)) ||"
	// +"execution(* org.mybatis.spring.SqlSessionTemplate.*(..))"
	// )
	public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
		Object result = doAroundService(pjp);
		return result;
	}

	protected Object doAroundService(ProceedingJoinPoint pjp) throws Throwable {
		LogFoot logFoot = new LogFoot();
		doBeforeService(logFoot);
		/**
		 * 1.获取request信息 2.根据request获取session 3.从session中取出登录用户信息
		 */
		try {
			RequestAttributes ra = RequestContextHolder.getRequestAttributes();
			ServletRequestAttributes sra = (ServletRequestAttributes) ra;
			HttpServletRequest request = sra.getRequest();

			// 从session中获取用户信息
			// String loginInfo = (String) session.getAttribute("username");
			// if(loginInfo != null && !"".equals(loginInfo)){
			// userName = operLoginModel.getLogin_Name();
			// }else{
			// userName = "用户未登录" ;
			// }
			// 获取请求地址
			requestPath = request.getRequestURI();
			logFoot.setRequestPath(requestPath);
			String targetClass = pjp.getTarget().toString();
			if (requestPath != null)
				type = fetchType(requestPath, targetClass);
			logFoot.setType(type);

			if (type != null && type.equals(LogTypeEnum.HTTP.getValue())) {
				// http请求获取输入参数
				inputParamMap = request.getParameterMap();
				logFoot.setInputParamMap(inputParamMap);
			} else {
				Object[] arguments = pjp.getArgs();
				logFoot.setArguments(arguments);
			}
			// 调用的方法名
			method = pjp.getSignature().toShortString();
			logFoot.setMethod(method);

			String JVM = JVMUtil.getJVMName();
			logFoot.setJvmname(JVM);

			String local_ip = IPAddrUtil.localAddress();
			logFoot.setLocal_ip(local_ip);

			String request_ip = RequestUtil.getClientIpAddress(request);
			logFoot.setRequest_ip(request_ip);

			Long thread = Thread.currentThread().getId();
			logFoot.setThread(thread.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// logFoot.doPrint();
		// 执行完方法的返回值：调用proceed()方法，就会触发切入点方法执行
		outputParamMap = new HashMap<String, Object>();
		Object result = "";
		Boolean succ = true;
		try {
			result = pjp.proceed();// result的值就是被拦截方法的返回值
			if (result == null) { // api 请求中 result没有返回值
				if (APILogger.getCurrRecord() != null && APILogger.getCurrRecord().getResponse() != null) { // 判断如果是api请求从localthread中apilog中获取
					result = APILogger.getCurrRecord().getResponse();
				}
			}

			if (result != null) {
				String resultStr = FastjsonUtils.beanToJson(result);
				if (resultStr.contains("\"code\":400") || resultStr.contains("\"code\":500") || resultStr.contains("\"code\":501")
						|| resultStr.contains("\"code\":502") || resultStr.contains("\"code\":503"))
					succ = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = e.getMessage();
			succ = false;
		}
		logFoot.setSucc(succ);

		outputParamMap.put("result", result);
		logFoot.setOutputParamMap(outputParamMap);

		endTimeMillis = System.currentTimeMillis(); // 记录方法执行完成的时间
		logFoot.setEndTimeMillis(endTimeMillis);
		logFoot.doPrint();
		// rpc或http是一个线程中tracer系统的入口，如果该接口返回，则清除线程中的context，然后再放回线程池中，防止干扰下次请求
		LogContext.remove();
		return result;
	}

	private void doBeforeService(LogFoot logFoot) {
		// 注意这里的逻辑：这个拦截入口是HTTP或者RPC，把之前的Threadlocal中context清空
		// fix me:非常小心 theadlocal和线程池同时使用时会有问题，线程会重复利用导致context会重复利用
		LogContext.remove();
		if (QDHessianHeaderContext.getHessianHeader().getHeader("requestid") != null
				&& QDHessianHeaderContext.getHessianHeader().getHeader("sequence") != null) {
			// 如果是rpc调用，需要继承客户端的LogContext
			LogContext.set(new LogContext(QDHessianHeaderContext.getHessianHeader().getHeader("requestid"),
					QDHessianHeaderContext.getHessianHeader().getHeader("sequence")));
			logFoot.setSequencePrefix(QDHessianHeaderContext.getHessianHeader().getHeader("sequence"));
			// QDHessianHeaderContext.close();
		} else {
			LogContext.set(new LogContext());
		}
		startTimeMillis = System.currentTimeMillis(); // 记录方法开始执行的时间
		logFoot.setId(LogContext.get().getRequestId());
		logFoot.setStartTimeMillis(startTimeMillis);
		String optTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startTimeMillis);
		logFoot.setOptTime(optTime);
		// step 递增
		int seqID = LogContext.get().addSequence();
		logFoot.setSequence(seqID);
		// LogContext.get().setSequence(seqID);
	}

	private String fetchType(String path, String targetClass) {
		String type = null;
		type = fetchTypeByPath(path);
		return type;
		// } else if (targetClass.contains("redis") ||
		// targetClass.contains("Jedis")) {
		// type = LogTypeEnum.RESOURCE_REDIS.getValue();
		// } else if (targetClass.contains("cache") ||
		// targetClass.contains("Cache")) {
		// type = LogTypeEnum.RESOURCE_MC.getValue();
		// } else if(targetClass.contains("mysql") ||
		// targetClass.contains("Mysql")){
		// type = LogTypeEnum.RESOURCE_MYSQL.getValue();
		// }
		// return type;
	}

	public abstract String fetchTypeByPath(String path);
	// String type = null;
	// if (path.contains("promotionbg")) {
	// type = LogTypeEnum.HTTP.getValue();
	// }else if (path.contains("promotion-web")) {
	// type = LogTypeEnum.RPC.getValue();
	// }
	// return type;
	// }

}
