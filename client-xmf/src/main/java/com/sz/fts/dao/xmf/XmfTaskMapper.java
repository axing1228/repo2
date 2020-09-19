package com.sz.fts.dao.xmf;

import com.sz.fts.bean.xmf.XmfTask;
import org.apache.ibatis.annotations.Param;

public interface XmfTaskMapper {
    int deleteByPrimaryKey(Integer taskId);

    int insert(XmfTask record);

    int insertSelective(XmfTask record);

    XmfTask selectByPrimaryKey(Integer taskId);

    int updateByPrimaryKeySelective(XmfTask record);

    int updateByPrimaryKey(XmfTask record);

    XmfTask selectTaskByOpenIdAndToday(@Param("openId") String openId, @Param("today") String today);
}