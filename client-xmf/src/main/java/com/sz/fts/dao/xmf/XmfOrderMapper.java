package com.sz.fts.dao.xmf;

import com.sz.fts.bean.xmf.XmfOrder;

import java.util.List;

public interface XmfOrderMapper {
    int deleteByPrimaryKey(Integer orderId);

    int insert(XmfOrder record);

    int insertSelective(XmfOrder record);

    XmfOrder selectByPrimaryKey(Integer orderId);

    int updateByPrimaryKeySelective(XmfOrder record);

    int updateByPrimaryKey(XmfOrder record);
    // 推荐信息
    List<XmfOrder> selectList(XmfOrder xmfOrder);

    int selectCount(XmfOrder xmfOrder);
}