package com.sz.fts.dao.yuyuekunsan;

import com.sz.fts.bean.yuyuekunsan.YYPhoneKS;

import java.util.List;

public interface YYPhoneKSMapper {
    int deleteByPrimaryKey(Integer phoneId);

    int insert(YYPhoneKS record);

    int insertSelective(YYPhoneKS record);

    YYPhoneKS selectByPrimaryKey(Integer phoneId);

    int updateByPrimaryKeySelective(YYPhoneKS record);

    int updateByPrimaryKey(YYPhoneKS record);


    YYPhoneKS selectByMobile(String phoneNumber);

    List<YYPhoneKS> selectListInfo(YYPhoneKS record);

    int selectCount(YYPhoneKS record);
}