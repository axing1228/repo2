package com.sz.fts.dao.xmf;

import com.sz.fts.bean.xmf.YYOrderKS;

import java.util.List;

public interface YYOrderKSMapper {
    int deleteByPrimaryKey(Integer orderId);

    int insert(YYOrderKS record);

    int insertSelective(YYOrderKS record);

    YYOrderKS selectByPrimaryKey(Integer orderId);

    int updateByPrimaryKeySelective(YYOrderKS record);

    int updateByPrimaryKey(YYOrderKS record);


    List<YYOrderKS> selectList(YYOrderKS record);

    int selectCount(YYOrderKS record);

    YYOrderKS selectByOrderSeq(String orderSeq);
}