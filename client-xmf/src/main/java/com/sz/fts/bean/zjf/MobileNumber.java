package com.sz.fts.bean.zjf;

import java.math.BigDecimal;

/**
 * @NAME MobileNumber
 * @AUTHOR 朱建峰
 * @DATE 2019/6/24 0024 下午 2:20
 * @DESCRIPTION 选号列表
 */
public class MobileNumber {
    private BigDecimal extraPrice;
    private BigDecimal price;
    private Integer id;
    private Integer status;
    private Integer selectFlag;
    private Integer type;
    private String mobileNumber;
    private String activityKey;
    private String createTime;
    private String selectTime;
    private String sellTime;
    private String extend1;
    private Integer robFlag;

    public String getExtend1() {
        return extend1;
    }

    public void setExtend1(String extend1) {
        this.extend1 = extend1;
    }

    public Integer getRobFlag() {
        return robFlag;
    }

    public void setRobFlag(Integer robFlag) {
        this.robFlag = robFlag;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSelectFlag() {
        return selectFlag;
    }

    public void setSelectFlag(Integer selectFlag) {
        this.selectFlag = selectFlag;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getActivityKey() {
        return activityKey;
    }

    public void setActivityKey(String activityKey) {
        this.activityKey = activityKey;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getSelectTime() {
        return selectTime;
    }

    public void setSelectTime(String selectTime) {
        this.selectTime = selectTime;
    }

    public String getSellTime() {
        return sellTime;
    }

    public void setSellTime(String sellTime) {
        this.sellTime = sellTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getExtraPrice() {
        return extraPrice;
    }

    public void setExtraPrice(BigDecimal extraPrice) {
        this.extraPrice = extraPrice;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "MobileNumber{" +
                "extraPrice=" + extraPrice +
                ", price=" + price +
                ", id=" + id +
                ", status=" + status +
                ", selectFlag=" + selectFlag +
                ", type=" + type +
                ", mobileNumber='" + mobileNumber + '\'' +
                ", activityKey='" + activityKey + '\'' +
                ", createTime='" + createTime + '\'' +
                ", selectTime='" + selectTime + '\'' +
                ", sellTime='" + sellTime + '\'' +
                '}';
    }
}
