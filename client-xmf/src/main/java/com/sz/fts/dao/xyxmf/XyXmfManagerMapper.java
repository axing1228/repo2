package com.sz.fts.dao.xyxmf;

import com.sz.fts.bean.xyxmf.XyXmfManager;

import java.util.List;

public interface XyXmfManagerMapper {
    int deleteByPrimaryKey(Integer managerId);

    int insert(XyXmfManager record);

    int insertSelective(XyXmfManager record);

    XyXmfManager selectByPrimaryKey(Integer managerId);

    int updateByPrimaryKeySelective(XyXmfManager record);

    int updateByPrimaryKey(XyXmfManager record);

    XyXmfManager selectManagerByOpenId(String openId);

    List<XyXmfManager> selectList(XyXmfManager manager);

    int selectCount(XyXmfManager manager);
}