package com.sz.fts.dao.wxpay;

import com.sz.fts.bean.wxpay.WxCallbackOrder;

public interface WxCallbackOrderMapper {
    int deleteByPrimaryKey(Integer orderId);

    int insert(WxCallbackOrder record);

    int insertSelective(WxCallbackOrder record);

    WxCallbackOrder selectByPrimaryKey(Integer orderId);

    int updateByPrimaryKeySelective(WxCallbackOrder record);

    int updateByPrimaryKey(WxCallbackOrder record);
}