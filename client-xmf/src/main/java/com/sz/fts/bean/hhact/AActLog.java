package com.sz.fts.bean.hhact;

import java.io.Serializable;

public class AActLog implements Serializable {
    private Integer logId;

    private String mobile;

    private String actName;

    private String openId;

    private String tjPhone;

    private String userName;

    private String actType;

    private String createTime;

    private String extend1;

    private Integer extend2;

    private String extend3;

    private String extend4;
    private String extend5;

    private String extend6;
    private String extend7;

    private String extend8;

    public String getExtend7() {
        return extend7;
    }

    public void setExtend7(String extend7) {
        this.extend7 = extend7;
    }

    public String getExtend8() {
        return extend8;
    }

    public void setExtend8(String extend8) {
        this.extend8 = extend8;
    }

    public String getExtend5() {
        return extend5;
    }

    public void setExtend5(String extend5) {
        this.extend5 = extend5;
    }

    public String getExtend6() {
        return extend6;
    }

    public void setExtend6(String extend6) {
        this.extend6 = extend6;
    }

    private int bigenPage = 0;

    private int endPage = 10;

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

    private static final long serialVersionUID = 1L;

    public Integer getLogId() {
        return logId;
    }

    public void setLogId(Integer logId) {
        this.logId = logId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public String getActName() {
        return actName;
    }

    public void setActName(String actName) {
        this.actName = actName == null ? null : actName.trim();
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId == null ? null : openId.trim();
    }

    public String getTjPhone() {
        return tjPhone;
    }

    public void setTjPhone(String tjPhone) {
        this.tjPhone = tjPhone == null ? null : tjPhone.trim();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public String getActType() {
        return actType;
    }

    public void setActType(String actType) {
        this.actType = actType == null ? null : actType.trim();
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

    public Integer getExtend2() {
        return extend2;
    }

    public void setExtend2(Integer extend2) {
        this.extend2 = extend2;
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