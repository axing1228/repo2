package com.sz.fts.dao.xjggl;

import com.sz.fts.bean.xjggl.XjGglLog;

import java.util.List;

public interface XjGglLogMapper {
    int deleteByPrimaryKey(Integer logId);

    int insert(XjGglLog record);

    int insertSelective(XjGglLog record);

    XjGglLog selectByPrimaryKey(Integer logId);

    int updateByPrimaryKeySelective(XjGglLog record);

    int updateByPrimaryKey(XjGglLog record);

    XjGglLog selectByMobile(String mobile);

    List<XjGglLog> selectAll();

    int selectByPrizeName(String prizeNameYes);
}