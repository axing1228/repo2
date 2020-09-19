package com.sz.fts.service.baoming;

import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 征华兴
 * @date 下午 3:53  2018/8/6 0006
 * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
 */
public interface CommonBaomingService {
    JSONObject hold(String s);

    JSONObject downloadLog(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
