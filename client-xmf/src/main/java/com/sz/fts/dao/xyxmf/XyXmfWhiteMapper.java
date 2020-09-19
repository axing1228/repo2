package com.sz.fts.dao.xyxmf;

import com.sz.fts.bean.xyxmf.XyXmfWhite;

import java.util.List;

public interface XyXmfWhiteMapper {
    int deleteByPrimaryKey(Integer whiteId);

    int insert(XyXmfWhite record);

    int insertSelective(XyXmfWhite record);

    XyXmfWhite selectByPrimaryKey(Integer whiteId);

    int updateByPrimaryKeySelective(XyXmfWhite record);

    int updateByPrimaryKey(XyXmfWhite record);

    List<XyXmfWhite> selectByMobile(String mobile);

    int deleteByMobile(String mobile);
}