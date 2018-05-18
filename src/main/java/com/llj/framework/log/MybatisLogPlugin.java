package com.llj.framework.log;

import com.llj.framework.utils.server.IPAddrUtil;
import com.llj.framework.utils.server.JVMUtil;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**   
 *    
 * 项目名称：framework-common   
 * 类名称：MybatisLogPlugin   
 * 类描述：  可恶的mybatis不支持cglib，所以无法切入，但是提供了拦截器接口，用于开展追踪日志
 * 创建人：qd   
 * 创建时间：2015年12月3日 下午3:26:05   
 * 修改人：qd   
 * 修改时间：2015年12月3日 下午3:26:05   
 * 修改备注：   
 * @version    
 *    
 */
@Intercepts({ @Signature(type = Executor.class, method = "update", args = {
		MappedStatement.class, Object.class}),
     @Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,
    RowBounds.class, ResultHandler.class }) })
public class MybatisLogPlugin implements Interceptor {
	
	public Object intercept(Invocation invocation) throws Throwable {
		LogFoot logFoot = new LogFoot();
		doBeforeInvocation(logFoot);
		Object result ="";
		try{
			  result = invocation.proceed();
		   doAfterInvocation(logFoot,invocation,true,result);
		}catch(Exception e){
			doAfterInvocation(logFoot,invocation,false,e);
			throw e;
		}
		return result;
	}

	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	public void setProperties(Properties properties) {
	}
	
	private void doBeforeInvocation(LogFoot logFoot){
    	//注意这里的逻辑：这个拦截入口是HTTP或者RPC，把之前的Threadlocal中context清空
    	//fix me:非常小心 theadlocal和线程池同时使用时会有问题，线程会重复利用导致context会重复利用
		try {
			if (LogContext.get() == null) {
				return ;
				//LogContext.set(new LogContext());
			}
			Long startTimeMillis = System.currentTimeMillis(); // 记录方法开始执行的时间
			logFoot.setId(LogContext.get().getRequestId());
			logFoot.setStartTimeMillis(startTimeMillis);
			String optTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startTimeMillis);
			logFoot.setOptTime(optTime);
			//step 递增
			int seqID = LogContext.get().addSequence();
			logFoot.setSequence(seqID);
		}catch (Exception e){
			e.printStackTrace();
		}
    }
	
	private void doAfterInvocation(LogFoot logFoot,Invocation invocation,Boolean succ,Object result){
		try {
			if (LogContext.get() == null) {
				return ;
				//LogContext.set(new LogContext());
			}


			logFoot.setType(LogTypeEnum.RESOURCE_MYSQL.getValue());

			String jdbcurl ="";
			try {
				Executor executor = (Executor) invocation.getTarget();
				 jdbcurl = executor.getTransaction().getConnection().getMetaData().getURL();
				logFoot.setRequestPath(jdbcurl);
			}catch (Exception e){
				e.printStackTrace();
			}

			Object[] arguments = invocation.getArgs();
			String sql = getSqlStatement(arguments);
			logFoot.setArguments(new Object[]{sql});

			//调用的方法名
			String method = invocation.getMethod().toString();
			logFoot.setMethod(method);

			String JVM = JVMUtil.getJVMName();
			logFoot.setJvmname(JVM);

			String local_ip = IPAddrUtil.localAddress();
			logFoot.setLocal_ip(local_ip);

			Long thread = Thread.currentThread().getId();
			logFoot.setThread(thread.toString());

			logFoot.setSucc(succ);

			//logFoot.doPrint();
			// 执行完方法的返回值：调用proceed()方法，就会触发切入点方法执行
			Map<String, Object> outputParamMap = new HashMap<String, Object>();
			outputParamMap.put("result", result);
			logFoot.setOutputParamMap(outputParamMap);

			Long endTimeMillis = System.currentTimeMillis(); // 记录方法执行完成的时间
			logFoot.setEndTimeMillis(endTimeMillis);
			logFoot.doPrint();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private String getSqlStatement(Object[] arguments){
		try {
			MappedStatement mappedStatement = (MappedStatement) arguments[0];
			Object parameter = null;
			if (arguments.length > 1) {
				parameter = arguments[1];
			}
			String sqlId = mappedStatement.getId();
			BoundSql boundSql = mappedStatement.getBoundSql(parameter);
			Configuration configuration = mappedStatement.getConfiguration();
			String sql = showSql(configuration, boundSql);
			StringBuilder str = new StringBuilder(100);
			str.append(sqlId);
			str.append(":");
			str.append(sql);
			str.append(":");
			return str.toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		return "unkwon sql";
	}
	
	public  String showSql(Configuration configuration, BoundSql boundSql) {
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
		try {
			if (parameterMappings.size() > 0 && parameterObject != null) {
				TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
				if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
					sql = sql.replaceFirst("\\?", getParameterValue(parameterObject));

				} else {
					MetaObject metaObject = configuration.newMetaObject(parameterObject);
					for (ParameterMapping parameterMapping : parameterMappings) {
						String propertyName = parameterMapping.getProperty();
						if (metaObject.hasGetter(propertyName)) {
							Object obj = metaObject.getValue(propertyName);
							sql = sql.replaceFirst("\\?", getParameterValue(obj));
						} else if (boundSql.hasAdditionalParameter(propertyName)) {
							Object obj = boundSql.getAdditionalParameter(propertyName);
							sql = sql.replaceFirst("\\?", getParameterValue(obj));
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
        return sql;
    }
	
	 private static String getParameterValue(Object obj) {
	        String value = null;
	        if (obj instanceof String) {
	            value = "'" + obj.toString() + "'";
	        } else if (obj instanceof Date) {
	            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
	            value = "'" + formatter.format(new Date()) + "'";
	        } else {
	            if (obj != null) {
	                value = obj.toString();
	            } else {
	                value = "";
	            }
	 
	        }
	        return value;
	    }
 
	
	
}
