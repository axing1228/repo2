package com.sz.fts.dao.gxxmf;

import com.sz.fts.bean.gxxmf.XyNewAdmin;
import org.apache.ibatis.annotations.Param;

public interface XyNewAdminMapper {
    int deleteByPrimaryKey(Integer adminId);

    int insert(XyNewAdmin record);

    int insertSelective(XyNewAdmin record);

    XyNewAdmin selectByPrimaryKey(Integer adminId);

    int updateByPrimaryKeySelective(XyNewAdmin record);

    int updateByPrimaryKey(XyNewAdmin record);

    XyNewAdmin selectAdminByMobileAndType(@Param("mobile") String mobile, @Param("type") int type);

    XyNewAdmin selectAdminByOpenIdAndType(@Param("openid") String mobile, @Param("type") int type);
}