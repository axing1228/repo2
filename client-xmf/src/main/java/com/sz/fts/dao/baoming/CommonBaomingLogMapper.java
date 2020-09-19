package com.sz.fts.dao.baoming;

import com.sz.fts.bean.baoming.CommonBaomingLog;

import java.util.List;

public interface CommonBaomingLogMapper {
    int deleteByPrimaryKey(Integer logId);

    int insert(CommonBaomingLog record);

    int insertSelective(CommonBaomingLog record);

    CommonBaomingLog selectByPrimaryKey(Integer logId);

    int updateByPrimaryKeySelective(CommonBaomingLog record);

    int updateByPrimaryKey(CommonBaomingLog record);

    List<CommonBaomingLog> selectAll();

    CommonBaomingLog selectByMobile(String mobile);
}