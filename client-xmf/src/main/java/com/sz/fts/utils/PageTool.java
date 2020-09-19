package com.sz.fts.utils;
 /**
 * @Title: PageTool.java 
 * @Prject: donateproject
 * @Package: com.wechat.module.utils 
 * @Description: TODO
 * @author: 赵代志  
 * @date: 2017年5月23日 下午2:28:46 
 * @version: V1.0   
 * Copyright © 2017 江苏鸿信系统集成有限公司. All rights reserved.
 */
public class PageTool {
	
	/**
	 * 数据库分页：0代表oracle，1代表mysql
	 * @param currentpage
	 * @param sign
	 * @return
	 */
	public static Page getPage(int currentpage,int sign){
		Page page=new Page();
		page.setCurrentpage(currentpage);
		
		//oracle
		if(sign==0){
			page.setPageStart((page.getCurrentpage()-1)*page.getPagesize()+1);
			page.setPageEnd(page.getCurrentpage()*page.getPagesize());
		}
		//mysql
		if(sign==1){
			page.setPageStart((page.getCurrentpage()-1)*page.getPagesize());
			page.setPageEnd(page.getPagesize());
		}
		return page;
	}
	
	/**
	 * 数据库分页：0代表oracle，1代表mysql
	 * @param currentpage
	 * @param sign
	 * @return
	 */
	public static Page getPage2(int currentpage,int sign){
		Page page=new Page(10);
		page.setCurrentpage(currentpage);
		
		//oracle
		if(sign==0){
			page.setPageStart((page.getCurrentpage()-1)*page.getPagesize()+1);
			page.setPageEnd(page.getCurrentpage()*page.getPagesize());
		}
		//mysql
		if(sign==1){
			page.setPageStart((page.getCurrentpage()-1)*page.getPagesize());
			page.setPageEnd(page.getPagesize());
		}
		return page;
	}
	
	/**
	 * 数据库分页：0代表oracle，1代表mysql
	 * @param currentpage
	 * @param sign
	 * @return
	 */
	public static Page getPage3(int currentpage,int sign){
		Page page=new Page(5);
		page.setCurrentpage(currentpage);
		
		//oracle
		if(sign==0){
			page.setPageStart((page.getCurrentpage()-1)*page.getPagesize()+1);
			page.setPageEnd(page.getCurrentpage()*page.getPagesize());
		}
		//mysql
		if(sign==1){
			page.setPageStart((page.getCurrentpage()-1)*page.getPagesize());
			page.setPageEnd(page.getPagesize());
		}
		return page;
	}

}
