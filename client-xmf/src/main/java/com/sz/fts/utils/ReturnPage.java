package com.sz.fts.utils;

import java.io.Serializable;
import java.util.List;

/**
 * 分页处理
 * 
 * @see:
 * @Company:江苏鸿信系统集成有限公司微信开发组
 * @author 杨坚
 * @Time 2016年11月28日
 * @version 1.0v
 */
public class ReturnPage<T> implements Serializable {

	/**  */
	private static final long serialVersionUID = 1L;

	private int result;

	private String msg;

	/**
	 * 当前页数
	 */
	private int currentPage;

	/**
	 * 总统计
	 */
	private int totalCount;

	/**
	 * 数据列表
	 */
	private List<T> items;

	public ReturnPage() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ReturnPage(int result, String msg, int currentPage, int totalCount, List<T> items) {
		this.result = result;
		this.msg = msg;
		this.currentPage = currentPage;
		this.totalCount = totalCount;
		this.items = items;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public List<T> getItems() {
		return items;
	}

	public void setItems(List<T> items) {
		this.items = items;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}