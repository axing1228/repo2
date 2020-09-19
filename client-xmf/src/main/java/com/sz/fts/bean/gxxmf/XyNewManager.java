package com.sz.fts.bean.gxxmf;

import java.io.Serializable;

public class XyNewManager implements Serializable {
    private Integer managerId;

    private String openId;

    private String nickName;

    private String mobile;

    private Integer txMoney;

    private Integer jiesuanMoney;

    private Integer qudao;

    private String createTime;

    private String extend1;

    private String extend2;

    private String extend3;

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

    public Integer getTxMoney() {
        return txMoney;
    }

    public void setTxMoney(Integer txMoney) {
        this.txMoney = txMoney;
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
}