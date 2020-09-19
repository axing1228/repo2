package com.sz.fts.bean.xyxmf;

import java.io.Serializable;

public class XyXmfWhite implements Serializable {
    private Integer whiteId;

    private String mobile;

    private String type;

    private String extend1;

    private String extend2;

    private String extend3;

    private String extend4;

    private static final long serialVersionUID = 1L;

    public Integer getWhiteId() {
        return whiteId;
    }

    public void setWhiteId(Integer whiteId) {
        this.whiteId = whiteId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
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