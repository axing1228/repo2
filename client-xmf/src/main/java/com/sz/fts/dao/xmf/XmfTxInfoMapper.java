package com.sz.fts.dao.xmf;

import com.sz.fts.bean.xmf.XmfTxInfo;

import java.util.List;

public interface XmfTxInfoMapper {
    int deleteByPrimaryKey(Integer txInfoId);

    int insert(XmfTxInfo record);

    int insertSelective(XmfTxInfo record);

    XmfTxInfo selectByPrimaryKey(Integer txInfoId);

    int updateByPrimaryKeySelective(XmfTxInfo record);

    int updateByPrimaryKey(XmfTxInfo record);

    List<XmfTxInfo> selectTxInfoByOpenId(XmfTxInfo info);

    List<XmfTxInfo> selectList(XmfTxInfo txInfo);

    int selectCount(XmfTxInfo txInfo);
}