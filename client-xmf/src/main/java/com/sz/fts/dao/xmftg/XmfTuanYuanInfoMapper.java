package com.sz.fts.dao.xmftg;

import com.sz.fts.bean.xmftg.XmfTuanYuanInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface XmfTuanYuanInfoMapper {
    int deleteByPrimaryKey(Integer tyId);

    int insert(XmfTuanYuanInfo record);

    int insertSelective(XmfTuanYuanInfo record);

    XmfTuanYuanInfo selectByPrimaryKey(Integer tyId);

    int updateByPrimaryKeySelective(XmfTuanYuanInfo record);

    int updateByPrimaryKey(XmfTuanYuanInfo record);

    List<XmfTuanYuanInfo> selectList(XmfTuanYuanInfo info);

    int selectCountByFlag(String flag);

    int updateAllByFlag(String flag);

    XmfTuanYuanInfo selectByFlagAndOpenId(@Param("flag") String flag, @Param("openId") String openId);

    int selectCount(XmfTuanYuanInfo tuanYuanInfo);
}