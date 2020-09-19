package com.sz.fts.service.xmftg;

import net.sf.json.JSONObject;

/**
 * @author 征华兴
 * @date 下午 2:38  2018/5/29 0029
 * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
 */
public interface XmfTgService {
    /**
     * 开团接口
     */
    JSONObject kaiTuan(String param);

    /**
     * 参团接口
     *
     * @param param
     * @return
     */
    JSONObject canTuan(String param);

    /**
     *  展示所有团成员头像
     * @param param
     * @return
     */
    JSONObject showAllMembers(String param) throws Exception;

    /**
     *  下单接口
     * @param param
     * @return
     */
    JSONObject holdInfo(String param);

    /**
     * 用户通过openid，查询自己的订单信息
     * @param param
     * @return
     */
    JSONObject queryOrderByOpenId(String param);
}
