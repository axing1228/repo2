package com.sz.fts.dao.ksxmf;

import com.sz.fts.bean.ksxmf.KsXmfLog;

public interface KsXmfLogMapper {
    int deleteByPrimaryKey(Integer orderId);

    int insert(KsXmfLog record);

    int insertSelective(KsXmfLog record);

    KsXmfLog selectByPrimaryKey(Integer orderId);

    int updateByPrimaryKeySelective(KsXmfLog record);

    int updateByPrimaryKey(KsXmfLog record);

    int selectByMobile(String mobile);
}