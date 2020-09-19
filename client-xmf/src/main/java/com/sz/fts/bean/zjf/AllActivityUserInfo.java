package com.sz.fts.bean.zjf;

/*
* 所有登记接口公用用户信息类
* user 朱建峰
* */

public class AllActivityUserInfo {

    private String id;
    private String activityKey;// 活动唯一key
    private String openId;// 用户openId
    private String mobileNumber;// 手机号码
    private String createTime; // 创建时间
    private String updateTime;// 修改时间
    private String broadbandNumber; // 宽带账号
    private String userName; // 用户昵称
    private String userAddress; // 用户地址
    private String identityCard; // 身份证
    private String extend1;// 拓展1
    private String extend2;// 拓展2
    private String extend3;// 拓展3

    public String getBroadbandNumber() {
        return broadbandNumber;
    }

    public void setBroadbandNumber(String broadbandNumber) {
        this.broadbandNumber = broadbandNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getIdentityCard() {
        return identityCard;
    }

    public void setIdentityCard(String identityCard) {
        this.identityCard = identityCard;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActivityKey() {
        return activityKey;
    }

    public void setActivityKey(String activityKey) {
        this.activityKey = activityKey;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }


    public String getExtend1() {
        return extend1;
    }

    public void setExtend1(String extend1) {
        this.extend1 = extend1;
    }

    public String getExtend2() {
        return extend2;
    }

    public void setExtend2(String extend2) {
        this.extend2 = extend2;
    }

    public String getExtend3() {
        return extend3;
    }

    public void setExtend3(String extend3) {
        this.extend3 = extend3;
    }
}
