package com.sz.fts.service.xmf;

import net.sf.json.JSONObject;

/**
 * @author 征华兴
 * @date 上午 11:19  2018/4/11 0011
 * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
 */
public interface XmfService {

    /**
     * 获取用户信息
     *
     * @param param
     * @return
     */
    JSONObject getUserInfo(String param) throws Exception;

    /**
     * 查询套餐列表信息s
     *
     * @param
     * @return
     */
    JSONObject queryTaocanList(String param);

    /**
     * 用户分享接口
     *
     * @param param
     * @return
     */
    JSONObject shareInfo(String param);

    /**
     * 通过openid,status
     * 查询用户进行中的订单信息
     */
    JSONObject getProcessOrder(String param);


    /**
     * 查询客户审核完成信息
     */
    JSONObject querySuccessOrderList(String param);


    /**
     * 提现 接口
     *
     * @param param
     * @return
     */
    JSONObject payMoney(String param) throws Exception;

    /**
     * 提现记录表
     *
     * @param param
     * @return
     */
    JSONObject queryTxInfo(String param);


    /*###############################################################后台管理系统#############################################################*/

    /**
     * 套餐列表信息
     *
     * @param s
     * @return
     */
    JSONObject taoCanList(String s) throws Exception;

    /**
     * 更改套餐
     *
     * @param s
     * @return
     */
    JSONObject updateTaoCan(String s) throws Exception;

    /**
     * 用户列表信息
     *
     * @param s
     * @return
     */
    JSONObject managerList(String s) throws Exception;

    /**
     * 推荐订单列表信息
     *
     * @param s
     * @return
     */
    JSONObject orderList(String s) throws Exception;

    /**
     * 审核成功
     *
     * @param s
     * @return
     */
    JSONObject successOrder(String s) throws Exception;

    /**
     * 取消
     *
     * @param s
     * @return
     */
    JSONObject delOrder(String s) throws Exception;


    /**
     * 查看套餐详情
     *
     * @param param
     * @return
     */
    JSONObject taocanInfo(String param);

    /**
     * 下单接口
     *
     * @param param
     * @return
     */
    JSONObject holdInfoByTaocan(String param);
}
