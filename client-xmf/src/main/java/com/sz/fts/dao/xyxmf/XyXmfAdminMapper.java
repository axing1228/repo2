package com.sz.fts.dao.xyxmf;

import com.sz.fts.bean.xyxmf.XyXmfAdmin;

public interface XyXmfAdminMapper {
    int deleteByPrimaryKey(Integer adminId);

    int insert(XyXmfAdmin record);

    int insertSelective(XyXmfAdmin record);

    XyXmfAdmin selectByPrimaryKey(Integer adminId);

    int updateByPrimaryKeySelective(XyXmfAdmin record);

    int updateByPrimaryKey(XyXmfAdmin record);

    XyXmfAdmin selectAdminByMobile(String mobile);

    XyXmfAdmin selectAdminByOpenId(String openId);
}