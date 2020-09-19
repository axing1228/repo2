package com.sz.fts.service.common;


import net.sf.json.JSONObject;

/**
 * @author 征华兴
 * @date 下午 3:10  2018/7/17 0017
 * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
 */
public interface CommonService {
//    /**
//     * 发送验证码
//     * @param param
//     * @param request
//     * @return
//     */
//    JSONObject sendCode(String param, HttpServletRequest request);
//
//    /**
//     *  通过手机号查询用户星级
//     * @param param
//     * @return
//     */
//    JSONObject getUserInfoByMobile(String param);

 //  JSONObject payMoney(String openid, String uuid,String userOpenid);

   JSONObject payMoney(JSONObject json);
}
