package com.sz.fts.bean.wxpay;

import java.io.Serializable;

public class WxCallbackOrder implements Serializable {
    private Integer orderId;

    private String transactionId;

    private String orderSeq;

    private String uptranSeq;

    private String fee;

    private String spId;

    private String cmpDate;

    private String timeStamp;

    private String payTime;

    private String respCode;

    private String respDesc;

    private String extData1;

    private String extData2;

    private String extend1;

    private String extend2;

    private String extend3;

    private String extend4;

    private static final long serialVersionUID = 1L;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId == null ? null : transactionId.trim();
    }

    public String getOrderSeq() {
        return orderSeq;
    }

    public void setOrderSeq(String orderSeq) {
        this.orderSeq = orderSeq == null ? null : orderSeq.trim();
    }

    public String getUptranSeq() {
        return uptranSeq;
    }

    public void setUptranSeq(String uptranSeq) {
        this.uptranSeq = uptranSeq == null ? null : uptranSeq.trim();
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee == null ? null : fee.trim();
    }

    public String getSpId() {
        return spId;
    }

    public void setSpId(String spId) {
        this.spId = spId == null ? null : spId.trim();
    }

    public String getCmpDate() {
        return cmpDate;
    }

    public void setCmpDate(String cmpDate) {
        this.cmpDate = cmpDate == null ? null : cmpDate.trim();
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp == null ? null : timeStamp.trim();
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime == null ? null : payTime.trim();
    }

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode == null ? null : respCode.trim();
    }

    public String getRespDesc() {
        return respDesc;
    }

    public void setRespDesc(String respDesc) {
        this.respDesc = respDesc == null ? null : respDesc.trim();
    }

    public String getExtData1() {
        return extData1;
    }

    public void setExtData1(String extData1) {
        this.extData1 = extData1 == null ? null : extData1.trim();
    }

    public String getExtData2() {
        return extData2;
    }

    public void setExtData2(String extData2) {
        this.extData2 = extData2 == null ? null : extData2.trim();
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