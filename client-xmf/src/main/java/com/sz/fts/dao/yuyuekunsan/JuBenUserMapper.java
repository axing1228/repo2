package com.sz.fts.dao.yuyuekunsan;

import com.sz.fts.bean.yuyuekunsan.JuBenUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface JuBenUserMapper {
    int deleteByPrimaryKey(Integer userId);

    int insert(JuBenUser record);

    int insertSelective(JuBenUser record);

    JuBenUser selectByPrimaryKey(Integer userId);

    int updateByPrimaryKeySelective(JuBenUser record);

    int updateByPrimaryKey(JuBenUser record);

    JuBenUser selectByUserCode(@Param(value = "userCode") String userCode);

    List<JuBenUser> selectList(JuBenUser record);

    int selectCount(JuBenUser record);
}