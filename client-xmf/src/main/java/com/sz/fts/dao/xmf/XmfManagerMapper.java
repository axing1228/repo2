package com.sz.fts.dao.xmf;

import com.sz.fts.bean.xmf.XmfManager;

import java.util.List;

public interface XmfManagerMapper {
    int deleteByPrimaryKey(Integer managerId);

    int insert(XmfManager record);

    int insertSelective(XmfManager record);

    XmfManager selectByPrimaryKey(Integer managerId);

    int updateByPrimaryKeySelective(XmfManager record);

    int updateByPrimaryKey(XmfManager record);

    XmfManager selectManagerByOpenId(String openId);

    List<XmfManager> selectList(XmfManager manager);

    int selectCount(XmfManager manager);
}