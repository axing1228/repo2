//package com.sz.fts.controller.xjggl;
//
//import com.sz.fts.service.xjggl.XjGglService;
//import com.sz.fts.utils.BaseRquestAndResponse;
//import com.sz.fts.utils.CommonUtil;
//import net.sf.json.JSONObject;
//import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import java.io.UnsupportedEncodingException;
//import java.net.URLDecoder;
//
///**
// * @author 征华兴
// * @date 上午 9:03  2018/7/17 0017 翼支付代金券
// * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
// */
//@Controller
//@RequestMapping("xjggl")
//public class XjGglController extends BaseRquestAndResponse {
//
//    @Autowired
//    private XjGglService xjGglService;
//
//    private static Logger logger = LogManager.getLogger(XjGglController.class);
//
//    /**
//     * *通过手机号码查询刮刮乐接口
//     */
//    @RequestMapping(value = "/getGgl.do")
//    public void getHuafei() throws Exception {
//        System.out.println("---------0-----------" + getRequest().getParameter("para"));
//        if (!getRequest().getParameter("para").contains("AA71C86813A85A94DFB")) {
//            JSONObject jsonObject = CommonUtil.outDecryptMsg(getRequest());
//            logger.info("------------星级刮刮乐0------------" + jsonObject);
//            //输入参数
//            JSONObject out = xjGglService.getGgl(jsonObject);
//            //返回参数
//            CommonUtil.printEncryptMsg(getRequest(), getResponse(), out);
//        }
//    }
//
//    /**
//     * 领取接口
//     */
//    @RequestMapping(value = "saveLog.do")
//    public void saveLog() throws Exception {
//        if (!getRequest().getParameter("para").contains("AA71C86813A85A94DFB")) {
//            JSONObject jsonObject = CommonUtil.outDecryptMsg(getRequest());
//            logger.info("------------星级刮刮乐1------------" + jsonObject);
//            //输入参数
//            JSONObject out = xjGglService.holdLog(jsonObject);
//            //返回参数
//            CommonUtil.printEncryptMsg(getRequest(), getResponse(), out);
//        }
//    }
//
//    /**
//     * 下载接口
//     */
//    @RequestMapping("downloadLog.do")
//    public void downloadLog() {
//        getResponse().setHeader("Access-Control-Allow-Origin", "*");
//        this.xjGglService.downloadLog(getRequest(), getResponse());
//    }
//
//    /**
//     * 解析json数据
//     */
//    private String getParam(String param) throws UnsupportedEncodingException {
//        String out = URLDecoder.decode(param, "UTF-8");
//        out = out.replace("=", "\":\"");
//        out = out.replace("&", "\",\"");
//        out = "{\"" + out + "\"}";
//        System.out.println(out);
//        return out;
//    }
//}
