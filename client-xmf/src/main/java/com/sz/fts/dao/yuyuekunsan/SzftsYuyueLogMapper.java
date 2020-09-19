package com.sz.fts.dao.yuyuekunsan;

import com.sz.fts.bean.yuyuekunsan.SzftsYuyueLog;

import java.util.List;

public interface SzftsYuyueLogMapper {
    int deleteByPrimaryKey(Integer logNo);

    int insert(SzftsYuyueLog record);

    int insertSelective(SzftsYuyueLog record);

    SzftsYuyueLog selectByPrimaryKey(Integer logNo);

    int updateByPrimaryKeySelective(SzftsYuyueLog record);

    int updateByPrimaryKey(SzftsYuyueLog record);

    List<SzftsYuyueLog> selectList(SzftsYuyueLog record);

    int selectCount(SzftsYuyueLog record);

    int deleteByStatus();

    int deleteByTime(String createTime);

    List<SzftsYuyueLog> selectByStatus();
    List<SzftsYuyueLog> selectByTime(String createTime);
}