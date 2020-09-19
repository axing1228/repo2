package com.sz.fts.utils.lottery;

import java.io.Serializable;
import java.math.BigDecimal;

public class FivePrize implements Serializable {

	private Integer rowid;

	/**
	 * 奖品名称
	 */
	private String prizename;

	/**
	 * 奖品当前数量
	 */
	private Integer prizenowcount;

	/**
	 * 奖品之前数量
	 */
	private Integer prizeoldpcount;

	/**
	 * 奖品概率
	 */
	private String prizeprobability;

	private String prizeimg1;

	private String prizeimg2;

	/**
	 * 奖品开始时间
	 */
	private String prizebegintime;

	/**
	 * 奖品结束时间
	 */
	private String prizeendtime;

	/**
	 * 奖品状态
	 */
	private Integer prizestatus;


	private Integer activitysign;

	private String cashtime;

	private String extend1;

	private String extend2;

	private String extend3;

	private String extend4;

	private Integer extend5;

	private Integer extend6;
	
	  /**
     * 中奖概率
     */
    private BigDecimal probability;

	public BigDecimal getProbability() {
		return probability;
	}

	public void setProbability(BigDecimal probability) {
		this.probability = probability;
	}

	private static final long serialVersionUID = 1L;

	public FivePrize() {
		super();
	}

	public FivePrize(Integer activitysign) {
		this.activitysign = activitysign;
	}

	public FivePrize(Integer rowid,Integer prizestatus,Integer activitysign) {
		this.rowid = rowid;
		this.prizestatus = prizestatus;
		this.activitysign = activitysign;
	}

	public FivePrize(Integer activitysign, Integer prizestatus) {
		this.activitysign = activitysign;
		this.prizestatus = prizestatus;
	}

	public Integer getRowid() {
		return rowid;
	}

	public void setRowid(Integer rowid) {
		this.rowid = rowid;
	}

	public String getPrizename() {
		return prizename;
	}

	public void setPrizename(String prizename) {
		this.prizename = prizename == null ? null : prizename.trim();
	}

	public Integer getPrizenowcount() {
		return prizenowcount;
	}

	public void setPrizenowcount(Integer prizenowcount) {
		this.prizenowcount = prizenowcount;
	}

	public Integer getPrizeoldpcount() {
		return prizeoldpcount;
	}

	public void setPrizeoldpcount(Integer prizeoldpcount) {
		this.prizeoldpcount = prizeoldpcount;
	}

	public String getPrizeprobability() {
		return prizeprobability;
	}

	public void setPrizeprobability(String prizeprobability) {
		this.prizeprobability = prizeprobability;
	}

	public String getPrizeimg1() {
		return prizeimg1;
	}

	public void setPrizeimg1(String prizeimg1) {
		this.prizeimg1 = prizeimg1 == null ? null : prizeimg1.trim();
	}

	public String getPrizeimg2() {
		return prizeimg2;
	}

	public void setPrizeimg2(String prizeimg2) {
		this.prizeimg2 = prizeimg2 == null ? null : prizeimg2.trim();
	}

	public String getPrizebegintime() {
		return prizebegintime;
	}

	public void setPrizebegintime(String prizebegintime) {
		this.prizebegintime = prizebegintime == null ? null : prizebegintime.trim();
	}

	public String getPrizeendtime() {
		return prizeendtime;
	}

	public void setPrizeendtime(String prizeendtime) {
		this.prizeendtime = prizeendtime == null ? null : prizeendtime.trim();
	}

	public Integer getPrizestatus() {
		return prizestatus;
	}

	public void setPrizestatus(Integer prizestatus) {
		this.prizestatus = prizestatus;
	}

	public Integer getActivitysign() {
		return activitysign;
	}

	public void setActivitysign(Integer activitysign) {
		this.activitysign = activitysign;
	}

	public String getCashtime() {
		return cashtime;
	}

	public void setCashtime(String cashtime) {
		this.cashtime = cashtime == null ? null : cashtime.trim();
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

	public Integer getExtend5() {
		return extend5;
	}

	public void setExtend5(Integer extend5) {
		this.extend5 = extend5;
	}

	public Integer getExtend6() {
		return extend6;
	}

	public void setExtend6(Integer extend6) {
		this.extend6 = extend6;
	}
}