package com.sz.fts.service.xjggl;

import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse; /**
 * @author 征华兴
 * @date 下午 2:14  2018/11/8 0008
 * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
 */
public interface XjDfsService {
    JSONObject holdLog(JSONObject jsonObject);

    void downloadLog(HttpServletRequest request, HttpServletResponse response);
}
