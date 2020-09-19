package com.sz.fts.utils.pojo;

import java.io.Serializable;

public class AppInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3434960070119511167L;
	private Integer appId;
	private String token;
	private String key;
	public Integer getAppId() {
		return appId;
	}
	public void setAppId(Integer appId) {
		this.appId = appId;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	

}
