//package com.sz.fts.controller.baoming;
//
//import com.sz.fts.service.baoming.CommonBaomingService;
//import com.sz.fts.utils.BaseRquestAndResponse;
//import com.sz.fts.utils.CommonUtil;
//import net.sf.json.JSONObject;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import java.io.UnsupportedEncodingException;
//import java.net.URLDecoder;
//
///**
// * @author 征华兴  潜水报名
// * @date 下午 3:49  2018/8/6 0006
// * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
// */
//@Controller
//@RequestMapping(value = "/common/baoming")
//public class CommonBaomingController extends BaseRquestAndResponse {
//
//    @Autowired
//    private CommonBaomingService baomingService;
//
//
//    /**
//     * 提交信息
//     */
//
//    @RequestMapping(value = "save.do")
//    public void save() throws Exception {
//        getResponse().setHeader("Access-Control-Allow-Origin", "*");
//        JSONObject jsonObject = CommonUtil.outDecryptMsg(getRequest());
//        //   JSONObject out = baomingService.hold(jsonObject.toString());
//        JSONObject out = baomingService.hold(jsonObject.toString());
//        // System.out.println("-报名-" + out);
//        CommonUtil.printEncryptMsg(getRequest(), getResponse(), out);
//    }
//
//    /**
//     * 导出领取记录
//     *
//     * @param
//     * @return
//     * @throws UnsupportedEncodingException
//     */
//    @RequestMapping(value = "/downloadLog.do")
//    public void downloadGift() throws Exception {
//        getResponse().setHeader("Access-Control-Allow-Origin", "*");
//        this.baomingService.downloadLog(getRequest(), getResponse());
//    }
//
//    private String getParam(String param) throws UnsupportedEncodingException {
//        String result = URLDecoder.decode(param, "UTF-8");
//        result = result.replace("&", "\",\"");
//        result = result.replace("=", "\":\"");
//        result = "{\"" + result + "\"}";
//        System.out.println(result);
//        return result;
//    }
//}
//
