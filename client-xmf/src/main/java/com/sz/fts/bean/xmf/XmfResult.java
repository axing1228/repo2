package com.sz.fts.bean.xmf;

/**
 * @author 征华兴
 * @date 下午 1:14  2018/4/24 0024
 * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
 */
public class XmfResult {

    private String userName;

    private String taocanName;

    private String status;

    private String yjMoney;

    private String createTime;

    private String beiZhu;

    private String mobile;

    private  String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTaocanName() {
        return taocanName;
    }

    public void setTaocanName(String taocanName) {
        this.taocanName = taocanName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getYjMoney() {
        return yjMoney;
    }

    public void setYjMoney(String yjMoney) {
        this.yjMoney = yjMoney;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getBeiZhu() {
        return beiZhu;
    }

    public void setBeiZhu(String beiZhu) {
        this.beiZhu = beiZhu;
    }

    @Override
    public String toString() {
        return "XmfResult{" +
                "userName='" + userName + '\'' +
                ", taocanName='" + taocanName + '\'' +
                ", status='" + status + '\'' +
                ", yjMoney='" + yjMoney + '\'' +
                ", createTime='" + createTime + '\'' +
                ", beiZhu='" + beiZhu + '\'' +
                '}';
    }
}
