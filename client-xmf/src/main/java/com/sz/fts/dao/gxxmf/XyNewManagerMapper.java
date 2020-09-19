package com.sz.fts.dao.gxxmf;

import com.sz.fts.bean.gxxmf.XyNewManager;
import org.apache.ibatis.annotations.Param;

public interface XyNewManagerMapper {
    int deleteByPrimaryKey(Integer managerId);

    int insert(XyNewManager record);

    int insertSelective(XyNewManager record);

    XyNewManager selectByPrimaryKey(Integer managerId);

    int updateByPrimaryKeySelective(XyNewManager record);

    int updateByPrimaryKey(XyNewManager record);

    XyNewManager selectManagerByOpenIdAndSource(@Param("openId") String openId, @Param("qudao") int qudao);
}