package com.sz.fts.bean.xmf;

import java.io.Serializable;

public class XmfTcInfo implements Serializable {
    private Integer taocanId;

    private String taocanName;

    private String description;

    private String price;

    private String yjMoney;

    private String tcUrl;

    private String source;

    private String area;

    private String createTime;

    private String extend1;

    private String extend2;

    private String extend3;
    private int count ;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    private int bigenPage=0;

    private int endPage=10;

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

    public Integer getTaocanId() {
        return taocanId;
    }

    public void setTaocanId(Integer taocanId) {
        this.taocanId = taocanId;
    }

    public String getTaocanName() {
        return taocanName;
    }

    public void setTaocanName(String taocanName) {
        this.taocanName = taocanName == null ? null : taocanName.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price == null ? null : price.trim();
    }

    public String getYjMoney() {
        return yjMoney;
    }

    public void setYjMoney(String yjMoney) {
        this.yjMoney = yjMoney == null ? null : yjMoney.trim();
    }

    public String getTcUrl() {
        return tcUrl;
    }

    public void setTcUrl(String tcUrl) {
        this.tcUrl = tcUrl == null ? null : tcUrl.trim();
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source == null ? null : source.trim();
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area == null ? null : area.trim();
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