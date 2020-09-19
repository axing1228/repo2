package com.sz.fts.service.xjggl;

import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 征华兴
 * @date 上午 9:06  2018/7/17 0017
 * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
 */
public interface XjGglService {

    /**
     * 通过手机号查询ggl活动
     *
     * @param json
     * @return
     */
    JSONObject getGgl(JSONObject json);

    /**
     * 领取接口
     *
     * @param json
     * @return
     */
    JSONObject holdLog(JSONObject json);

    /**
     * 导出领取记录
     *
     * @param request
     * @param response
     */
    void downloadLog(HttpServletRequest request, HttpServletResponse response);
}
