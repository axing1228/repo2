package com.sz.fts.dao.gxxmf;

import com.sz.fts.bean.gxxmf.XyNewOrder;

import java.util.List;

public interface XyNewOrderMapper {
    int deleteByPrimaryKey(Integer orderId);

    int insert(XyNewOrder record);

    int insertSelective(XyNewOrder record);

    XyNewOrder selectByPrimaryKey(Integer orderId);

    int updateByPrimaryKeySelective(XyNewOrder record);

    int updateByPrimaryKey(XyNewOrder record);

    List<XyNewOrder> selectList(XyNewOrder xmfOrder);
}