package com.sz.fts.dao.xyxmf;

import com.sz.fts.bean.xyxmf.XyXmfOrder;

import java.util.List;

public interface XyXmfOrderMapper {
    int deleteByPrimaryKey(Integer orderId);

    int insert(XyXmfOrder record);

    int insertSelective(XyXmfOrder record);

    XyXmfOrder selectByPrimaryKey(Integer orderId);

    int updateByPrimaryKeySelective(XyXmfOrder record);

    int updateByPrimaryKey(XyXmfOrder record);

    List<XyXmfOrder> selectList(XyXmfOrder xmfOrder);

    int selectCount(XyXmfOrder xmfOrder);
}