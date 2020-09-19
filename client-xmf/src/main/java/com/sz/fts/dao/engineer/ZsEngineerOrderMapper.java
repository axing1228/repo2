package com.sz.fts.dao.engineer;

import com.sz.fts.bean.engineer.ZsEngineerOrder;

import java.util.List;

public interface ZsEngineerOrderMapper {
    int deleteByPrimaryKey(Integer orderId);

    int insert(ZsEngineerOrder record);

    int insertSelective(ZsEngineerOrder record);

    ZsEngineerOrder selectByPrimaryKey(Integer orderId);

    int updateByPrimaryKeySelective(ZsEngineerOrder record);

    int updateByPrimaryKey(ZsEngineerOrder record);

   List<ZsEngineerOrder> selectList(ZsEngineerOrder zsEngineerOrder);

    int selectCount(ZsEngineerOrder xmfOrder);
}