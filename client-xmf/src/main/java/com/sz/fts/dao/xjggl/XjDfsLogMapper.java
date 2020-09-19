package com.sz.fts.dao.xjggl;

import com.sz.fts.bean.xjggl.XjDfsLog;

import java.util.List;

public interface XjDfsLogMapper {
    int deleteByPrimaryKey(Integer logId);

    int insert(XjDfsLog record);

    int insertSelective(XjDfsLog record);

    XjDfsLog selectByPrimaryKey(Integer logId);

    int updateByPrimaryKeySelective(XjDfsLog record);

    int updateByPrimaryKey(XjDfsLog record);

    int selectCountByMobile(String mobile);

    List<XjDfsLog> selectAll();
}