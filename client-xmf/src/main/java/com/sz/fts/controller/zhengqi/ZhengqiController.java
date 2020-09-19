package com.sz.fts.controller.zhengqi;


import com.sz.fts.service.zhengqi.ZhengqiService;
import com.sz.fts.utils.BaseRquestAndResponse;
import com.sz.fts.utils.CommonUtil;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author 征华兴
 * @date 上午 9:36  2018/6/9 0009
 * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
 */
@Controller
@RequestMapping("zhengqi")
public class ZhengqiController extends BaseRquestAndResponse {

    @Autowired
    private ZhengqiService zhengqiService;

    private static final Logger logger = LogManager.getLogger(ZhengqiController.class);

    /**
     * 入口，通过openid获取信息
     */
    @RequestMapping("getUserInfo.do")
    public void getUserInfo(@RequestBody String json) throws Exception {
        getResponse().setHeader("Access-Control-Allow-Origin", "*");
        String param = this.getParam(json);
        logger.info("--------------政企OpenId-----------：" + param);
        JSONObject out = this.zhengqiService.getUserInfo(param);
        CommonUtil.printOutMsg(getRequest(), getResponse(), out);
    }


    /**
     *  解析json数据
     */
    private String getParam(String param) throws UnsupportedEncodingException {
        String out = URLDecoder.decode(param, "UTF-8");
        out = out.replace("=", "\":\"");
        out = out.replace("&", "\",\"");
        out = "{\"" + out + "\"}";
        return out;
    }
}


