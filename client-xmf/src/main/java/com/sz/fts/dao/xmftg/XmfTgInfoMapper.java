package com.sz.fts.dao.xmftg;

import com.sz.fts.bean.xmftg.XmfTgInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface XmfTgInfoMapper {
    int deleteByPrimaryKey(Integer tgId);

    int insert(XmfTgInfo record);

    int insertSelective(XmfTgInfo record);

    XmfTgInfo selectByPrimaryKey(Integer tgId);

    int updateByPrimaryKeySelective(XmfTgInfo record);

    int updateByPrimaryKey(XmfTgInfo record);

    List<XmfTgInfo> selectList(XmfTgInfo info);

    XmfTgInfo selectTgInfoByFlag(String flag);

    XmfTgInfo selectTgInfoByOpenIdAndStatus(@Param("openId") String openId, @Param("status") int status);
}