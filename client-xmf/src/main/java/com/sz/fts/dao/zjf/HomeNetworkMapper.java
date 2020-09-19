package com.sz.fts.dao.zjf;

import com.sz.fts.bean.zjf.ActivityOrder;
import com.sz.fts.bean.zjf.AllActivityUserInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * @NAME HomeNetworkMapper
 * @AUTHOR 朱建峰
 * @DATE 2019/5/6 0006 下午 3:43
 * @DESCRIPTION 家庭网活动
 */
public interface HomeNetworkMapper {
    void updateForEvaluation(Map<String, Object> map);

    AllActivityUserInfo selecUsertById(String id);

    ActivityOrder selectOrderByActivityKeyAndId(@Param("activityKey") String activityKey, @Param("id") String id);
}
