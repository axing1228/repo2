package com.sz.fts.bean.own;

import java.io.Serializable;

public class OwnSMS implements Serializable {
	private Integer smsNo;

	private String smsTelephone;

	private String smsCode;

	private String smsCreate;

	private String extend1;

	private String extend2;

	private String extend3;

	private String extend4;

	private static final long serialVersionUID = 1L;

	public OwnSMS() {
		super();
		// TODO Auto-generated constructor stub
	}

	public OwnSMS(String smsTelephone, String smsCode, String smsCreate) {
		this.smsTelephone = smsTelephone;
		this.smsCode = smsCode;
		this.smsCreate = smsCreate;
	}

	public Integer getSmsNo() {
		return smsNo;
	}

	public void setSmsNo(Integer smsNo) {
		this.smsNo = smsNo;
	}

	public String getSmsTelephone() {
		return smsTelephone;
	}

	public void setSmsTelephone(String smsTelephone) {
		this.smsTelephone = smsTelephone == null ? null : smsTelephone.trim();
	}

	public String getSmsCode() {
		return smsCode;
	}

	public void setSmsCode(String smsCode) {
		this.smsCode = smsCode == null ? null : smsCode.trim();
	}

	public String getSmsCreate() {
		return smsCreate;
	}

	public void setSmsCreate(String smsCreate) {
		this.smsCreate = smsCreate == null ? null : smsCreate.trim();
	}

	public String getExtend1() {
		return extend1;
	}

	public void setExtend1(String extend1) {
		this.extend1 = extend1 == null ? null : extend1.trim();
	}

	public String getExtend2() {
		return extend2;
	}

	public void setExtend2(String extend2) {
		this.extend2 = extend2 == null ? null : extend2.trim();
	}

	public String getExtend3() {
		return extend3;
	}

	public void setExtend3(String extend3) {
		this.extend3 = extend3 == null ? null : extend3.trim();
	}

	public String getExtend4() {
		return extend4;
	}

	public void setExtend4(String extend4) {
		this.extend4 = extend4 == null ? null : extend4.trim();
	}
}