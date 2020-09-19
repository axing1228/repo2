package com.sz.fts.controller.ksxmf;

import com.sz.fts.service.ksxmf.KsXmfService;
import com.sz.fts.utils.BaseRquestAndResponse;
import com.sz.fts.utils.CommonUtil;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author 征华兴
 * @date 下午 3:17  2018/8/9 0009
 * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
 */
@Controller
@RequestMapping("ksxmf")
public class KsXmfController extends BaseRquestAndResponse {

    @Autowired
    private KsXmfService ksXmfService;

    /**
     * 提交订单
     */
    @RequestMapping(value = "/saveInfo.do")
    public void saveInfoNew() throws Exception {
        getResponse().setHeader("Access-Control-Allow-Origin", "*");
          JSONObject json = CommonUtil.outDecryptMsg(getRequest());
        JSONObject out = this.ksXmfService.saveInfo(json.toString());
        CommonUtil.printEncryptMsg(getRequest(), getResponse(), out);
    }

    private String getParam(String param) throws UnsupportedEncodingException {
        String result = URLDecoder.decode(param, "UTF-8");
        result = result.replace("&", "\",\"");
        result = result.replace("=", "\":\"");
        result = "{\"" + result + "\"}";
        System.out.println(result);
        return result;
    }
}
