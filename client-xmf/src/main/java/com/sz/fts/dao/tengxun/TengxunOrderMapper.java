package com.sz.fts.dao.tengxun;

import com.sz.fts.bean.tengxun.TengxunOrder;

import java.util.List;

public interface TengxunOrderMapper {
    int deleteByPrimaryKey(Integer orderId);

    int insert(TengxunOrder record);

    int insertSelective(TengxunOrder record);

    TengxunOrder selectByPrimaryKey(Integer orderId);

    int updateByPrimaryKeySelective(TengxunOrder record);

    int updateByPrimaryKey(TengxunOrder record);

    TengxunOrder selectByMobile(String phone);

    List<TengxunOrder> selectAll();
}