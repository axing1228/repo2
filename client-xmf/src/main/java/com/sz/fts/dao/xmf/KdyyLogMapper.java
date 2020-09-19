package com.sz.fts.dao.xmf;

import com.sz.fts.bean.xmf.KdyyLog;

import java.util.List;

public interface KdyyLogMapper {
    int deleteByPrimaryKey(Integer orderId);

    int insert(KdyyLog record);

    int insertSelective(KdyyLog record);

    KdyyLog selectByPrimaryKey(Integer orderId);

    int updateByPrimaryKeySelective(KdyyLog record);

    int updateByPrimaryKey(KdyyLog record);

    int selectByTaocanName(KdyyLog log);

    List<KdyyLog> selectList(KdyyLog yuyueOrder);

    int selectCount(KdyyLog log);
}