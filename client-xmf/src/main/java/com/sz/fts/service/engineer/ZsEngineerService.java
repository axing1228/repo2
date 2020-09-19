package com.sz.fts.service.engineer;

import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 征华兴
 * @date 下午 1:57  2018/11/15 0015
 * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
 */
public interface ZsEngineerService {
    /**
     *  获取用户信息
     * @param s
     * @return
     */
    JSONObject getUserInfo(String s);

    /**
     *  查询套餐列表
     * @param s
     * @return
     */
    JSONObject queryTaocanList(String s);

    /**
     *  下单接口
     * @param s
     * @return
     */
    JSONObject holdInfoByTaocan(String s);

    /**
     * 查询套餐
     * @param s
     * @return
     */
    JSONObject taocanInfo(String s);

    /**
     *  查询用户进行中，全部的订单信息
     * @param s
     * @return
     */
    JSONObject getProcessOrder(String s);

//    /**
//     *  已审核完成订单
//     * @param s
//     * @return
//     */
//    JSONObject querySuccessOrderList(String s);

    JSONObject payMoney(String s) throws Exception;

    JSONObject managerList(String s) throws Exception;

    JSONObject orderList(String s) throws Exception;

    JSONObject saveOrder(String s) throws Exception;

    JSONObject updateOrder(String s) throws Exception;

    JSONObject delOrder(String s) throws Exception;

    JSONObject addManager(String s) throws Exception;

    JSONObject updateManager(String s) throws Exception;

    JSONObject queryTxInfo(String s);

    JSONObject downloadOrder(String extend9,HttpServletRequest request, HttpServletResponse response) throws Exception;

//    JSONObject updateManager(String s);
}
