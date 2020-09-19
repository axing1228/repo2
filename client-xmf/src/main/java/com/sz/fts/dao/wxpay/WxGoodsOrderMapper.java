package com.sz.fts.dao.wxpay;

import com.sz.fts.bean.wxpay.WxGoodsOrder;

public interface WxGoodsOrderMapper {
    int deleteByPrimaryKey(Integer orderId);

    int insert(WxGoodsOrder record);

    int insertSelective(WxGoodsOrder record);

    WxGoodsOrder selectByPrimaryKey(Integer orderId);

    int updateByPrimaryKeySelective(WxGoodsOrder record);

    int updateByPrimaryKey(WxGoodsOrder record);

    WxGoodsOrder selectByOrderSeq(String orderSeq);
}