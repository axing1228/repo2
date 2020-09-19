package com.sz.fts.service.yuyuekunsan;


import net.sf.json.JSONObject;

/**
 * 靓号0元抢
 * @author 耿怀志
 * @version [版本号, 2019/3/20]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public interface YuYueLHService {

//    /**
//     * 搜索号码
//     * @param json
//     * @return
//     */
//    JSONObject searchPhones(JSONObject json);
//
//    /**
//     * 保存订单信息
//     * @param json
//     * @return
//     */
//    JSONObject saveLHInfo(JSONObject json) throws Exception;
//
//    /**
//     * 查询列表
//     * @param json
//     * @return
//     */
//    JSONObject selectList(JSONObject json) throws Exception;
//
//    /**
//     *  修改订单
//     */
//    JSONObject updateOrder(JSONObject json) throws Exception;
//
//
//    /**
//     *  取消订单
//     */
//    JSONObject delOrder(JSONObject json) throws Exception;
//
//    /**
//     *  确认订单
//     */
//    JSONObject successOrder(JSONObject json) throws Exception;
//
//
//    /**
//     *  导出订单
//     */
//    void downloadOrder(JSONObject json, HttpServletResponse response) throws Exception;

    /**
     *  回调订单 - 修改订单
     */
    JSONObject xiuOrder(JSONObject json) throws Exception;
}
