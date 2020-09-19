package com.sz.fts.bean.xmf;

import java.io.Serializable;

public class XmfTxInfo implements Serializable {
    private Integer txInfoId;

    private Integer orderId;

    private String openId;

    private String llbOpenId;

    private String paymentTime;

    private String createTime;

    private String paymentNo;

    private String txMoney;

    private String txMonth;

    private String nickName;

    private String mobile;

    private Integer qudao;

    private String extend1;

    private String extend2;

    private String extend3;
    private int bigenPage = 0;

    private int endPage = 10;
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

    public Integer getTxInfoId() {
        return txInfoId;
    }

    public void setTxInfoId(Integer txInfoId) {
        this.txInfoId = txInfoId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId == null ? null : openId.trim();
    }

    public String getLlbOpenId() {
        return llbOpenId;
    }

    public void setLlbOpenId(String llbOpenId) {
        this.llbOpenId = llbOpenId == null ? null : llbOpenId.trim();
    }

    public String getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(String paymentTime) {
        this.paymentTime = paymentTime == null ? null : paymentTime.trim();
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime == null ? null : createTime.trim();
    }

    public String getPaymentNo() {
        return paymentNo;
    }

    public void setPaymentNo(String paymentNo) {
        this.paymentNo = paymentNo == null ? null : paymentNo.trim();
    }

    public String getTxMoney() {
        return txMoney;
    }

    public void setTxMoney(String txMoney) {
        this.txMoney = txMoney == null ? null : txMoney.trim();
    }

    public String getTxMonth() {
        return txMonth;
    }

    public void setTxMonth(String txMonth) {
        this.txMonth = txMonth == null ? null : txMonth.trim();
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName == null ? null : nickName.trim();
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public Integer getQudao() {
        return qudao;
    }

    public void setQudao(Integer qudao) {
        this.qudao = qudao;
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
}