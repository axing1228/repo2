package com.sz.fts.dao.wxpay;

import com.sz.fts.bean.wxpay.CActivityOrder;

public interface CActivityOrderMapper {
    int deleteByPrimaryKey(String orderNumber);

    int insert(CActivityOrder record);

    int insertSelective(CActivityOrder record);

    CActivityOrder selectByPrimaryKey(String orderNumber);

    int updateByPrimaryKeySelective(CActivityOrder record);

    int updateByPrimaryKey(CActivityOrder record);
}