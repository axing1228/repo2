package com.sz.fts.dao.yuyuekunsan;

import com.sz.fts.bean.yuyuekunsan.YYTaoCanKS;

import java.util.List;

public interface YYTaoCanKSMapper {
    int deleteByPrimaryKey(Integer taocanId);

    int insert(YYTaoCanKS record);

    int insertSelective(YYTaoCanKS record);

    YYTaoCanKS selectByPrimaryKey(Integer taocanId);

    int updateByPrimaryKeySelective(YYTaoCanKS record);

    int updateByPrimaryKey(YYTaoCanKS record);


    List<YYTaoCanKS> selectList(YYTaoCanKS yuyueTaocan);

    int selectCount(YYTaoCanKS yuyueTaocan);
}