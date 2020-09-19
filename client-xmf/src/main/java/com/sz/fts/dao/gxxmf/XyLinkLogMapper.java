package com.sz.fts.dao.gxxmf;

import com.sz.fts.bean.gxxmf.XyLinkLog;

import java.util.List;

public interface XyLinkLogMapper {
    int deleteByPrimaryKey(Integer orderId);

    int insert(XyLinkLog record);

    int insertSelective(XyLinkLog record);

    XyLinkLog selectByPrimaryKey(Integer orderId);

    int updateByPrimaryKeySelective(XyLinkLog record);

    int updateByPrimaryKey(XyLinkLog record);

    XyLinkLog selectByYhCode(String uuid);

    List<XyLinkLog> selectList(XyLinkLog xmXmfLog);

    int selectCount(XyLinkLog xyXmfLog);
}