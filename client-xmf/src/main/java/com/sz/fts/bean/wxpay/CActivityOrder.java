package com.sz.fts.bean.wxpay;

import java.io.Serializable;

public class CActivityOrder implements Serializable {
    private String orderNumber;

    private String userId;

    private String activityKey;

    private String orderStatus;

    private String extend1;

    private String extend2;

    private String extend3;

    private String createTime;

    private String updateTime;

    private String completePayFlag;

    private String completePayTime;

    private static final long serialVersionUID = 1L;

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber == null ? null : orderNumber.trim();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    public String getActivityKey() {
        return activityKey;
    }

    public void setActivityKey(String activityKey) {
        this.activityKey = activityKey == null ? null : activityKey.trim();
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus == null ? null : orderStatus.trim();
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime == null ? null : createTime.trim();
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime == null ? null : updateTime.trim();
    }

    public String getCompletePayFlag() {
        return completePayFlag;
    }

    public void setCompletePayFlag(String completePayFlag) {
        this.completePayFlag = completePayFlag == null ? null : completePayFlag.trim();
    }

    public String getCompletePayTime() {
        return completePayTime;
    }

    public void setCompletePayTime(String completePayTime) {
        this.completePayTime = completePayTime == null ? null : completePayTime.trim();
    }
}