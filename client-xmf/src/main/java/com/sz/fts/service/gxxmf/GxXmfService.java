package com.sz.fts.service.gxxmf;

import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 征华兴
 * @date 下午 1:51  2018/6/20 0020
 * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
 */
public interface GxXmfService {
    /**
     *  获取用户信息
     * @param param
     * @return
     */
    JSONObject getUserInfo(String param);

    /**
     *  查询套餐信息
     * @param param
     * @return
     */
    JSONObject queryTaocanList(String param);

    /**
     *   进行中，全部
     * @param param
     * @return
     */
    JSONObject getProcessOrder(String param);

    /**
     *  查询已完成订单
     * @param param
     * @return
     */
    JSONObject querySuccessOrderList(String param);



    JSONObject queryTxInfo(String param);

    JSONObject save(String s);

    JSONObject payMoney(String s);

    JSONObject saveQuanyi(String s);

    JSONObject logList(String s) throws Exception;

    JSONObject saveLog(String s) throws Exception;

    JSONObject updateLog(String s) throws Exception;

    JSONObject delLog(String s) throws Exception;

    JSONObject downloadLog(String extend9, HttpServletRequest request, HttpServletResponse response) throws Exception;
}
