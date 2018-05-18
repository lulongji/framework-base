package com.llj.framework.log;

import com.llj.framework.utils.json.FastjsonUtils;
import com.llj.framework.utils.data.MyStringUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 日志追踪系统中的日志格式
 *
 */
public class LogFoot {
	private static Logger logger = LogManager.getLogger("tracer");
	// public static final String EMPTY = "";
	// public static final String SEPERATOR = "-";
	public AtomicInteger sequence;

	String id = ""; // 每个request从头到尾都有唯一的id
	String step; // 请求中的步骤数
	String sequencePrefix;
	String type;
	String userName = "";
	String requestPath = "";
	String method = "";
	Boolean succ = true;
	String optTime = "";
	String pro_time = "";
	String local_ip = "";
	String request_ip = "";
	String jvmname = "";
	String thread = "";
	Long endTimeMillis = 0L;
	Long startTimeMillis = 0L;
	Map<?, ?> inputParamMap = new HashMap<>(); // for http请求参数
	Object[] arguments = new Object[0];// for rpc/resource/method 请求参数
	Map<String, Object> outputParamMap = new HashMap<String, Object>();

	public LogFoot() {
		// this.id = IDGenerator.generateWithSeq();
		if (LogContext.get() != null) {
			this.sequencePrefix = LogContext.get().getSequence_prefix();
		}
	}

	public LogFoot(String type) {
		this.type = type;
		if (LogContext.get() != null) {
			this.sequencePrefix = LogContext.get().getSequence_prefix();
		}
	}

	public void setSequence(int sequence) {
		if (MyStringUtil.isEmpty(sequencePrefix)) {
			this.step = "" + sequence;
		} else {
			this.step = sequencePrefix + "_" + sequence;
		}
	}

	// /* generate sequence for thread-across or process-across */
	// private String inheriteSequence() {
	// return (this.sequencePrefix == null ? EMPTY := this.sequencePrefix +
	// SEPERATOR) + this.sequence
	// .get();
	// }

	public void setSequence(AtomicInteger sequence) {
		this.sequence = sequence;
	}

	public void setSucc(Boolean succ) {
		this.succ = succ;
	}

	/**
	 * 构造函数 以下为必填字段
	 * 
	 * @param type
	 * @param requestPath
	 * @param method
	 * @param optTime
	 * @param inputParamMap
	 * @param outputParamMap
	 */
	public LogFoot(String type, String requestPath, String method, Boolean succ, String optTime, Map<?, ?> inputParamMap,
			Map<String, Object> outputParamMap) {
		this.type = type;
		this.requestPath = requestPath;
		this.method = method;
		this.succ = succ;
		this.optTime = optTime;
		this.inputParamMap = inputParamMap;
		this.outputParamMap = outputParamMap;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRequestPath() {
		return requestPath;
	}

	public void setRequestPath(String requestPath) {
		this.requestPath = requestPath;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getOptTime() {
		return optTime;
	}

	public void setOptTime(String optTime) {
		this.optTime = optTime;
	}

	public String getPro_time() {
		return pro_time;
	}

	public void setPro_time(String pro_time) {
		this.pro_time = pro_time;
	}

	public String getLocal_ip() {
		return local_ip;
	}

	public void setLocal_ip(String local_ip) {
		this.local_ip = local_ip;
	}

	public String getRequest_ip() {
		return request_ip;
	}

	public void setRequest_ip(String request_ip) {
		this.request_ip = request_ip;
	}

	public String getJvmname() {
		return jvmname;
	}

	public void setJvmname(String jvmname) {
		this.jvmname = jvmname;
	}

	public String getThread() {
		return thread;
	}

	public void setThread(String thread) {
		this.thread = thread;
	}

	public Map<?, ?> getInputParamMap() {
		return inputParamMap;
	}

	public void setInputParamMap(Map<?, ?> inputParamMap) {
		this.inputParamMap = inputParamMap;
	}

	public Map<String, Object> getOutputParamMap() {
		return outputParamMap;
	}

	public void setOutputParamMap(Map<String, Object> outputParamMap) {
		this.outputParamMap = outputParamMap;
	}

	public Long getEndTimeMillis() {
		return endTimeMillis;
	}

	public void setEndTimeMillis(Long endTimeMillis) {
		this.endTimeMillis = endTimeMillis;
	}

	public Long getStartTimeMillis() {
		return startTimeMillis;
	}

	public void setStartTimeMillis(Long startTimeMillis) {
		this.startTimeMillis = startTimeMillis;
	}

	public String getSequencePrefix() {
		return sequencePrefix;
	}

	public void setSequencePrefix(String sequencePrefix) {
		this.sequencePrefix = sequencePrefix;
	}

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Object[] getArguments() {
		return arguments;
	}

	public void setArguments(Object[] arguments) {
		this.arguments = arguments;
	}

	/**
	 * 日志输出
	 */
	public String doPrint() {
		String log = "";
		try {
			String request_param = "";
			if (inputParamMap != null & inputParamMap.size() > 0) {
				try {
					request_param = FastjsonUtils.beanToJson(inputParamMap);
				} catch (Exception e) {
				}
			} else if (arguments != null && arguments.length > 0) {
				// fix me:=
				// 这里fastjson序列化对特殊字符处理有异常，http:=//i.dotidea.cn/2014/08/fastjson-serialize-overflow/
				try {
					request_param = FastjsonUtils.beanToJson(Arrays.asList(arguments));
				} catch (Exception e) {
				}
			}

			String response = "";
			if (outputParamMap != null && outputParamMap.size() > 0) {
				try {
					response = FastjsonUtils.beanToJson(outputParamMap);
					if (response.length() > 500) {// 限制response的大小
						response = response.substring(0, 500);
					}
				} catch (Exception e) {
				}
			}

			log = "" + "requestId:" + id + "\u0001step:" + step + "\u0001type:" + type + "\u0001user:" + userName + "\u0001url:" + requestPath
					+ "\u0001method:" + method + "\u0001succ:" + succ + "\u0001begin_time:" + optTime + "\u0001exe_time:"
					+ (endTimeMillis - startTimeMillis) + "ms" + "\u0001local_ip:" + local_ip + "\u0001request_ip:" + request_ip + "\u0001jvm:"
					+ jvmname + "\u0001thread:" + thread + "\u0001request_param:" + request_param + "\u0001" + "response:" + response;
			if (succ) {
				logger.info(log);
			} else {
				logger.error(log);
			}
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error(log);
		}
		return log;
	}

}
