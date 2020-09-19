package com.sz.fts.dao.xmf;

import com.sz.fts.bean.xmf.XmfTcInfo;

import java.util.List;

public interface XmfTcInfoMapper {
    int deleteByPrimaryKey(Integer taocanId);

    int insert(XmfTcInfo record);

    int insertSelective(XmfTcInfo record);

    XmfTcInfo selectByPrimaryKey(Integer taocanId);

    int updateByPrimaryKeySelective(XmfTcInfo record);

    int updateByPrimaryKey(XmfTcInfo record);

    // 套餐列表信息
    List<XmfTcInfo> selectList(XmfTcInfo tc);

    int selectCount(XmfTcInfo tcInfo);
}