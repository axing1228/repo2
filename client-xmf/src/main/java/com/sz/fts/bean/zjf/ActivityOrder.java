package com.sz.fts.bean.zjf;

/**
 * @NAME ActivityOrder
 * @AUTHOR 朱建峰
 * @DATE 2019/5/7 0007 上午 9:14
 * @DESCRIPTION 订单字段
 */
public class ActivityOrder {

    private String userId;
    private String activityKey;
    private Integer orderStatus;
    private Integer completePayFlag;
    private String orderNumber;
    private String completePayTime;
    private String createTime;
    private String updateTime;
    private String extend1;
    private String extend2;
    private String extend3;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getActivityKey() {
        return activityKey;
    }

    public void setActivityKey(String activityKey) {
        this.activityKey = activityKey;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Integer getCompletePayFlag() {
        return completePayFlag;
    }

    public void setCompletePayFlag(Integer completePayFlag) {
        this.completePayFlag = completePayFlag;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getCompletePayTime() {
        return completePayTime;
    }

    public void setCompletePayTime(String completePayTime) {
        this.completePayTime = completePayTime;
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

    @Override
    public String toString() {
        return "ActivityOrder{" +
                "userId='" + userId + '\'' +
                ", activityKey='" + activityKey + '\'' +
                ", orderStatus=" + orderStatus +
                ", completePayFlag=" + completePayFlag +
                ", orderNumber='" + orderNumber + '\'' +
                ", completePayTime='" + completePayTime + '\'' +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", extend1='" + extend1 + '\'' +
                ", extend2='" + extend2 + '\'' +
                ", extend3='" + extend3 + '\'' +
                '}';
    }
}
