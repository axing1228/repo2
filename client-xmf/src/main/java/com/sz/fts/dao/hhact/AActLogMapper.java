package com.sz.fts.dao.hhact;

import com.sz.fts.bean.hhact.AActLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AActLogMapper {
    int deleteByPrimaryKey(Integer logId);

    int insert(AActLog record);

    int insertSelective(AActLog record);

    AActLog selectByPrimaryKey(Integer logId);

    int updateByPrimaryKeySelective(AActLog record);

    int updateByPrimaryKey(AActLog record);

    List<AActLog> selectListByMobileAndType(AActLog log);

    int selectDistinctCount(AActLog log);

    List<AActLog> selectTop();

    int selectByPrizeNameAndExtend1(@Param("prize") String prizeName, @Param("month") String month);

    List<AActLog> selectList(AActLog aActLog);

    int selectCount(AActLog aActLog);

    AActLog selectByOrderSeq(String orderSeq);
}