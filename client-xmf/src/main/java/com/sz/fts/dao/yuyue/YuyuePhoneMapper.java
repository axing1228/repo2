package com.sz.fts.dao.yuyue;

import com.sz.fts.bean.yuyue.YuyuePhone;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface YuyuePhoneMapper {
    int deleteByPrimaryKey(Integer phoneId);

    int insert(YuyuePhone record);

    int insertSelective(YuyuePhone record);

    YuyuePhone selectByPrimaryKey(Integer phoneId);

    int updateByPrimaryKeySelective(YuyuePhone record);

    int updateByPrimaryKey(YuyuePhone record);

    List<YuyuePhone> queryPhone(@Param("start") Integer start);

    List<YuyuePhone> queryBirthdayPhone(String phone);

    YuyuePhone selectByMobile(String phoneNumber);

    //int selectByPhoneNumberCount(String phoneNumber);

    int insertList(List<YuyuePhone> list);

    List<YuyuePhone> selectListInfo(YuyuePhone yuyuePhone);

    int selectCount(YuyuePhone yuyuePhone);

    List<YuyuePhone> selectAll();  // 查询所有

    List<YuyuePhone> getMobileNumber(Map<String, Object> map);
}