package com.sz.fts.dao.zjf;

import com.sz.fts.bean.zjf.MobileNumber;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @NAME MobileNumberMapper
 * @AUTHOR 朱建峰
 * @DATE 2019/6/24 0024 下午 2:23
 * @DESCRIPTION
 */
public interface MobileNumberMapper {

    void update(MobileNumber mobileNumber);

    MobileNumber get(Integer id);

    List<MobileNumber> getMobileNumber(Map<String, Object> map);

    MobileNumber getByMobileNumber(@Param("mobileNumber") String mobileNumber, @Param("activityKey") String activityKey);

    List<MobileNumber> selectByMobileNumber(MobileNumber mobileNumber);

}
