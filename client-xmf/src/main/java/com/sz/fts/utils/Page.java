package com.sz.fts.utils;
 /**
 * @Title: Page.java 
 * @Prject: donateproject
 * @Package: com.wechat.module.utils 
 * @Description: TODO
 * @author: 赵代志  
 * @date: 2017年5月22日 上午8:48:00 
 * @version: V1.0   
 * Copyright © 2017 江苏鸿信系统集成有限公司. All rights reserved.
 */
public class Page {
	
	public static final int DEFAULT_PAGE_SIZE=20;
	/**
	 * 页面大小
	 */
	private int pagesize;
	/**
	 * 当前页
	 */
	private int currentpage;
	/**
	 * 总记录数
	 */
	private int totalcount;
	/**
	 * 总页数
	 */
	private int totalpages;
	/**
	 * 开始
	 */
	private int pageStart;
	/**
	 * 截止
	 */
	private int pageEnd;
	
	public Page(){
		this.pagesize=DEFAULT_PAGE_SIZE;		
	}
	
	public Page(int pageSize){
		this.pagesize=pageSize;		
	}
	
	
	public int getPagesize() {
		return pagesize;
	}
	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}
	public int getCurrentpage() {
		return currentpage;
	}
	public void setCurrentpage(int currentpage) {
		this.currentpage = currentpage;
	}
	public int getTotalcount() {
		return totalcount;
	}
	public void setTotalcount(int totalcount) {
		this.totalcount = totalcount;
	}
	public int getTotalpages() {
		return totalpages;
	}
	public void setTotalpages(int totalpages) {
		this.totalpages = totalpages;
	}
	public int getPageStart() {
		return pageStart;
	}
	public void setPageStart(int pageStart) {
		this.pageStart = pageStart;
	}
	public int getPageEnd() {
		return pageEnd;
	}
	public void setPageEnd(int pageEnd) {
		this.pageEnd = pageEnd;
	}
	
	

}
