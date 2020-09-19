package com.sz.fts.dao.dongwu;

import com.sz.fts.bean.dongwu.SzMobileUser;

public interface SzMobileUserMapper {
    int deleteByPrimaryKey(Integer userNo);

    int insert(SzMobileUser record);

    int insertSelective(SzMobileUser record);

    SzMobileUser findNSegmentCount(SzMobileUser record);

    SzMobileUser selectByPrimaryKey(Integer userNo);

    int updateByPrimaryKeySelective(SzMobileUser record);

    int updateByPrimaryKey(SzMobileUser record);

    int findSzMobileUserBySegment(String segment);
}