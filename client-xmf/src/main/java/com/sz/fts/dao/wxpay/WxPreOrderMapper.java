package com.sz.fts.dao.wxpay;

import com.sz.fts.bean.wxpay.WxPreOrder;

public interface WxPreOrderMapper {
    int deleteByPrimaryKey(Integer orderId);

    int insert(WxPreOrder record);

    int insertSelective(WxPreOrder record);

    WxPreOrder selectByPrimaryKey(Integer orderId);

    int updateByPrimaryKeySelective(WxPreOrder record);

    int updateByPrimaryKey(WxPreOrder record);
}