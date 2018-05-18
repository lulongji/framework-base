package com.llj.framework.page;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询参数对象
 * 
 * @author dubl
 */
public class Page<T> {

	/** 页号 */
	private Integer pageNo = 1;

	/** 每页显示记录数 */
	private Integer pageSize = 10;

	/** 是否使用分页查询 */
	private boolean usePage = true;

	/** 查询的排序字段集合 */
	private List<String> orderFields = new ArrayList<String>();

	/** 查询的结果总页数 */
	private Integer totalPageNum;

	/** 查询的结果总记录数 */
	private Integer totalNum;

	/** 查询的结果集合 */
	private List<T> queryList;
	
	/**
	 * 获取从查询询结果记录中开始提取记录的顺序号
	 * 
	 * @return
	 */
	public Integer getStart() {
		return (pageNo - 1) * pageSize;
	}

	/**
	 * 获取查询提取记录的个数
	 * 
	 * @return
	 */
	public Integer getCount() {
		return pageSize;
	}

	public Integer getTotalPageNum() {
		return totalPageNum;
	}

	/**
	 * 获取页号
	 */
	public Integer getPageNo() {
		return pageNo;
	}

	/**
	 * 获取每页记录数
	 */
	public Integer getPageSize() {
		return pageSize;
	}

	/**
	 * 获取是否使用分页
	 * 
	 * @return
	 */
	public boolean isUsePage() {
		return usePage;
	}

	/**
	 * 获得排序字段集合
	 * 
	 * @return
	 */
	public List<String> getOrderFields() {
		return orderFields;
	}

	/**
	 * 获取查询总记录数
	 * 
	 * @return
	 */
	public Integer getTotalNum() {
		return totalNum;
	}

	/**
	 * 获取查询结果集合
	 * 
	 * @return
	 */
	public List<T> getQueryList() {
		return queryList;
	}

	/**
	 * 设置页号
	 */
	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	/**
	 * 设置每页记录数
	 */
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * 设置是否使用分页
	 */
	public void setUsePage(boolean usePage) {
		this.usePage = usePage;
	}

	/**
	 * 设置是排序字段集合
	 */
	public void setOrderFields(List<String> orderFields) {
		this.orderFields = orderFields;
	}

	/**
	 * 设置查询总记录数
	 * 
	 * @param totalNum
	 *            查询总记录数
	 */
	public void setTotalNum(Integer totalNum) {
		this.totalNum = totalNum;
		if (pageSize == 0) {
			pageSize = 10;
		}
		this.totalPageNum = totalNum % pageSize == 0 ? totalNum / pageSize
				: totalNum / pageSize + 1;
	}

	/**
	 * 设置查询的结果集合
	 * 
	 * @param queryResult
	 *            查询结果集合
	 */
	public void setQueryList(List<T> queryList) {
		this.queryList = queryList;
	}

	public void setTotalPageNum(Integer totalPageNum) {
		this.totalPageNum = totalPageNum;
	}

}
