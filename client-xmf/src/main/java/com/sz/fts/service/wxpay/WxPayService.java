package com.sz.fts.service.wxpay;

import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 征华兴
 * @date 下午 3:45  2019/1/16 0016
 * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
 */
public interface WxPayService {


    JSONObject preOrderOne(JSONObject json);

    JSONObject preOrderTwo(String json);


    String notifyResult(HttpServletRequest request);


    JSONObject preOrderThr(String s);

    String notifyResultThr(HttpServletRequest request);

    JSONObject preOrderFour(JSONObject s);

    String notifyResultFour(HttpServletRequest request);

    JSONObject preOrderWap(JSONObject json);

    JSONObject preOrderQuanyi(JSONObject jsonObject);

    String notifyResultQuanyi(HttpServletRequest request);

    JSONObject preOrderJsapiCommon(JSONObject jsonObject);

    String commonNotify(HttpServletRequest request);

    String wapByNotify(HttpServletRequest request);

    /**
     * 给第三方公司支付
     *
     * @param json
     * @return
     */
    JSONObject prepayOrderjsapi(JSONObject json,HttpServletRequest request);

    String thirdPart(HttpServletRequest request);

    JSONObject testPay(JSONObject jsonObject);
}
