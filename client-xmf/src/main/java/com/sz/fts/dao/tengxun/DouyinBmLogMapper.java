package com.sz.fts.dao.tengxun;

import com.sz.fts.bean.tengxun.DouyinBmLog;

import java.util.List;

public interface DouyinBmLogMapper {
    int deleteByPrimaryKey(Integer logId);

    int insert(DouyinBmLog record);

    int insertSelective(DouyinBmLog record);

    DouyinBmLog selectByPrimaryKey(Integer logId);

    int updateByPrimaryKeySelective(DouyinBmLog record);

    int updateByPrimaryKey(DouyinBmLog record);

    DouyinBmLog selectByMobile(String mobile);

    List<DouyinBmLog> selectAll();
}