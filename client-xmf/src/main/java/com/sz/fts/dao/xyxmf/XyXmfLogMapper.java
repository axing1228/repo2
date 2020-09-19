package com.sz.fts.dao.xyxmf;

import com.sz.fts.bean.xyxmf.XyXmfLog;

import java.util.List;

public interface XyXmfLogMapper {
    int deleteByPrimaryKey(Integer orderId);

    int insert(XyXmfLog record);

    int insertSelective(XyXmfLog record);

    XyXmfLog selectByPrimaryKey(Integer orderId);

    int updateByPrimaryKeySelective(XyXmfLog record);

    int updateByPrimaryKey(XyXmfLog record);

    List<XyXmfLog> selectList(XyXmfLog kdyyLog);

    int selectCount(XyXmfLog xyXmfLog);
}