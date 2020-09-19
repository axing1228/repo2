package com.sz.fts.service.tengxun;

import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 征华兴
 * @date 上午 10:29  2018/11/7 0007
 * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
 */
public interface TengxunService {
    /**
     * 开通并绑定
     *
     * @param json
     * @return
     */
    JSONObject openAndBindTengxun(String json);

    /**
     * 关闭
     *
     * @param json
     * @return
     */
    JSONObject closeTengxun(String json);

    void download(HttpServletRequest request, HttpServletResponse response);

    /**
     *  群发短信
     * @return
     */
    JSONObject sendCodeAll();
}
