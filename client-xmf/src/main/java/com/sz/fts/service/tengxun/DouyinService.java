package com.sz.fts.service.tengxun;

import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 征华兴
 * @date 下午 1:32  2018/12/24 0024
 * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
 */
public interface DouyinService {
    JSONObject hold(String s);

    void downloadLog(HttpServletRequest request, HttpServletResponse response);
}
