package com.sz.fts.dao.yuyuekunsan;


import com.sz.fts.bean.yuyuekunsan.YYOrderKS;

import java.util.List;

public interface YYOrderKSMapperTwo {
    int deleteByPrimaryKey(Integer orderId);

    int insert(YYOrderKS record);

    int insertSelective(YYOrderKS record);

    YYOrderKS selectByPrimaryKey(Integer orderId);

    int updateByPrimaryKeySelective(YYOrderKS record);

    int updateByPrimaryKey(YYOrderKS record);


    YYOrderKS selectByJuBen(String extend2);

    List<YYOrderKS> selectList(YYOrderKS record);

    int selectCount(YYOrderKS record);

    int deleteByStatus();
    int deleteByTime(String createTime);
    List<YYOrderKS> selectByStatus();
    List<YYOrderKS> selectByTime(String createTime);

}