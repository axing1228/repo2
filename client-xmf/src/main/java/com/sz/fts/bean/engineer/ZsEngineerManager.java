package com.sz.fts.bean.engineer;

import java.io.Serializable;

public class ZsEngineerManager implements Serializable {
    private Integer managerId;

    private String openId;

    private String userName;

    private String mobile;

    private String txInfo;

    private Integer txMoney;

    private String starLevel;

    private Integer jiesuanMoney;

    private Integer qudao;

    private String createTime;

    private String extend1;

    private String extend2;

    private String extend3;

    private String extend4;

    private int bigenPage=0;

    private int endPage=10;
    private String startTime;

    private String endTime;

    public int getBigenPage() {
        return bigenPage;
    }

    public void setBigenPage(int bigenPage) {
        this.bigenPage = bigenPage;
    }

    public int getEndPage() {
        return endPage;
    }

    public void setEndPage(int endPage) {
        this.endPage = endPage;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    private static final long serialVersionUID = 1L;

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId == null ? null : openId.trim();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public String getTxInfo() {
        return txInfo;
    }

    public void setTxInfo(String txInfo) {
        this.txInfo = txInfo == null ? null : txInfo.trim();
    }

    public Integer getTxMoney() {
        return txMoney;
    }

    public void setTxMoney(Integer txMoney) {
        this.txMoney = txMoney;
    }

    public String getStarLevel() {
        return starLevel;
    }

    public void setStarLevel(String starLevel) {
        this.starLevel = starLevel == null ? null : starLevel.trim();
    }

    public Integer getJiesuanMoney() {
        return jiesuanMoney;
    }

    public void setJiesuanMoney(Integer jiesuanMoney) {
        this.jiesuanMoney = jiesuanMoney;
    }

    public Integer getQudao() {
        return qudao;
    }

    public void setQudao(Integer qudao) {
        this.qudao = qudao;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime == null ? null : createTime.trim();
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