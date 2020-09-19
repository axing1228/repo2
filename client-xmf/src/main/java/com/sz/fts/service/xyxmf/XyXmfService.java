package com.sz.fts.service.xyxmf;

import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 征华兴
 * @date 上午 11:11  2018/5/11 0011
 * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
 */
public interface XyXmfService {
    /**
     * 获取用户信息
     *
     * @param param
     * @return
     */
    JSONObject getUserInfo(String param) ;

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

    /**
     * 通过手机号码，查询该号码权限
     */

    JSONObject queryTaocanByMobile(String param);

    /**
     * 发送验证码
     *
     * @param param
     * @param request
     * @return
     */
    JSONObject sendCode(String param, HttpServletRequest request);
    /**
     * 通过用户手机号，
     * 查询用户订单情况
     */
    JSONObject queryOrderByMobile(String param);


}
