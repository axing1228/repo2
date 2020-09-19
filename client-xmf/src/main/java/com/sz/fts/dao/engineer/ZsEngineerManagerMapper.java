package com.sz.fts.dao.engineer;

import com.sz.fts.bean.engineer.ZsEngineerManager;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ZsEngineerManagerMapper {
    int deleteByPrimaryKey(Integer managerId);

    int insert(ZsEngineerManager record);

    int insertSelective(ZsEngineerManager record);

    ZsEngineerManager selectByPrimaryKey(Integer managerId);

    int updateByPrimaryKeySelective(ZsEngineerManager record);

    int updateByPrimaryKey(ZsEngineerManager record);

    ZsEngineerManager selectManagerByMobileAndSource(@Param("mobile") String mobile, @Param("source") int source);

    ZsEngineerManager selectManagerByOpenIdAndSource(@Param("openid") String openid, @Param("source") int source);

    List<ZsEngineerManager> selectList(ZsEngineerManager xyNewManager);

    int selectCount(ZsEngineerManager xyNewManager);

    ZsEngineerManager selectManagerBySource(int source);

    ZsEngineerManager selectManagerByOpenId(String openId);

    ZsEngineerManager selectManagerByMobile(String mobile);
}