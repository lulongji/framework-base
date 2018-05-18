package com.llj.framework.page;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.PropertyException;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * MyBatis分页拦截器
 * 
 * @author lu
 */
@Intercepts(@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}))
public class PaginationInterceptor implements Interceptor {
	private static final List<ResultMapping> EMPTY_RESULTMAPPING = new ArrayList<ResultMapping>(0);
	private static String dialect = ""; // 数据库方言

	public Object plugin(Object target) {
		if (target instanceof Executor) {
			return Plugin.wrap(target, this);
		} else {
			return target;
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public Object intercept(Invocation invocation) throws Throwable {
		final Object[] args = invocation.getArgs();
		if (args != null && args.length >= 2) {
			Object paramObject = args[1];
			if (paramObject instanceof Page<?> && ((Page<?>) paramObject).isUsePage()) {
				args[2] = RowBounds.DEFAULT;
				MappedStatement mappedStatement = (MappedStatement) args[0];
				BoundSql boundSql = mappedStatement.getBoundSql(paramObject);
				MappedStatement cloneMappedStatement = copyMappedStatement(mappedStatement, new NativeSqlSource(boundSql));
				args[0] = cloneMappedStatement;
				MetaObject msObject = SystemMetaObject.forObject(cloneMappedStatement);
				String sql = (String) msObject.getValue("sqlSource.boundSql.sql");
				Page<?> queryObj = (Page<?>) paramObject;
				// 求count - 重写sql
				msObject.setValue("sqlSource.boundSql.sql", getCountSql(sql));
				// 查询总数
				Object obj = invocation.proceed();

				int totalCount = (Integer) ((List) obj).get(0);
				int totalPage = totalCount / queryObj.getPageSize() + ((totalCount % queryObj.getPageSize() == 0) ? 0 : 1);
				// 如果当前页大于总页数且查询有结果 返回最后一页
				// if (queryObj.getPageNo() > totalPage && totalPage != 0) {
				// queryObj.setPageNo(totalPage);
				// }
				queryObj.setTotalNum(totalCount);
				queryObj.setTotalPageNum(totalPage);

				// 追加排序字段
				if (queryObj.getOrderFields() != null && queryObj.getOrderFields().size() > 0) {
					List<String> orderFields = queryObj.getOrderFields();
					sql += " order by ";
					for (int i = 0; i < orderFields.size(); i++) {
						sql += orderFields.get(i) + ",";
					}
					sql = sql.substring(0, sql.length() - 1);
				}

				msObject.setValue("sqlSource.boundSql.sql", getPageSql(sql, queryObj));
				// 恢复类型
				msObject.setValue("resultMaps", mappedStatement.getResultMaps());
				// 执行分页查询
				obj = invocation.proceed();
				// 得到处理结果
				queryObj.setQueryList((List) obj);
				// 返回结果
				return obj;
			}
		}

		return invocation.proceed();
	}

	/**
	 * 获取分页sql - 如果要支持其他数据库，修改这里就可以
	 * 
	 * @param sql
	 * @param page
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private String getPageSql(String sql, Page page) {
		StringBuilder pageSql = new StringBuilder(200);
		if ("mysql".equals(dialect)) {
			pageSql.append(sql);
			pageSql.append(" limit " + page.getStart() + "," + page.getPageSize());
		} else if ("oracle".equals(dialect)) {
			pageSql.append("select * from ( select temp.*, rownum row_id from ( ");
			pageSql.append(sql);
			pageSql.append(" ) temp ");
			pageSql.append(") where row_id > " + page.getStart()).append(" and row_id<=" + (page.getStart() + page.getPageSize()));
		}
		return pageSql.toString();
	}

	private String getCountSql(String sql) {
		return "select count(0) from (" + sql + ") tmp_count";
	}

	private MappedStatement copyMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
		MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId() + "_分页", newSqlSource,
				ms.getSqlCommandType());
		builder.resource(ms.getResource());
		builder.fetchSize(ms.getFetchSize());
		builder.statementType(ms.getStatementType());
		builder.keyGenerator(ms.getKeyGenerator());
		if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
			StringBuffer keyProperties = new StringBuffer();
			for (String keyProperty : ms.getKeyProperties()) {
				keyProperties.append(keyProperty).append(",");
			}
			keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
			builder.keyProperty(keyProperties.toString());
		}
		builder.timeout(ms.getTimeout());
		builder.parameterMap(ms.getParameterMap());
		// 由于resultMaps第一次需要返回int类型的结果，所以这里需要生成一个resultMap - 防止并发错误
		List<ResultMap> resultMaps = new ArrayList<ResultMap>();
		ResultMap resultMap = new ResultMap.Builder(ms.getConfiguration(), ms.getId(), int.class, EMPTY_RESULTMAPPING).build();
		resultMaps.add(resultMap);
		builder.resultMaps(resultMaps);
		builder.resultSetType(ms.getResultSetType());
		builder.cache(ms.getCache());
		builder.flushCacheRequired(ms.isFlushCacheRequired());
		builder.useCache(ms.isUseCache());

		return builder.build();
	}

	private class NativeSqlSource implements SqlSource {
		BoundSql boundSql;

		public NativeSqlSource(BoundSql boundSql) {
			this.boundSql = boundSql;
		}

		public BoundSql getBoundSql(Object parameterObject) {
			return boundSql;
		}
	}

	public void setProperties(Properties p) {
		dialect = p.getProperty("dialect");
		if (dialect != null && dialect.equals("")) {
			try {
				throw new PropertyException("dialect property is not found!");
			} catch (PropertyException e) {
				e.printStackTrace();
			}
		}
	}
}
