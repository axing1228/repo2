package com.sz.fts.dao.xjggl;

import com.sz.fts.bean.xjggl.XjDfsWhite;

public interface XjDfsWhiteMapper {
    int deleteByPrimaryKey(Integer whiteId);

    int insert(XjDfsWhite record);

    int insertSelective(XjDfsWhite record);

    XjDfsWhite selectByPrimaryKey(Integer whiteId);

    int updateByPrimaryKeySelective(XjDfsWhite record);

    int updateByPrimaryKey(XjDfsWhite record);

    XjDfsWhite selectByMobile(String mobile);
}