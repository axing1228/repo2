package com.sz.fts.dao.tengxun;

import com.sz.fts.bean.tengxun.TengxunWhite;

import java.util.List;

public interface TengxunWhiteMapper {
    int deleteByPrimaryKey(Integer whiteId);

    int insert(TengxunWhite record);

    int insertSelective(TengxunWhite record);

    TengxunWhite selectByPrimaryKey(Integer whiteId);

    int updateByPrimaryKeySelective(TengxunWhite record);

    int updateByPrimaryKey(TengxunWhite record);

    int selectByMobile(String phone);

    List<TengxunWhite> selectAll();

    List<TengxunWhite> selectByExtend4(String s);
}