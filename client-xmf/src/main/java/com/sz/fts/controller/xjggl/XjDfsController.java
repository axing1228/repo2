//package com.sz.fts.controller.xjggl;
//
//import com.sz.fts.service.xjggl.XjDfsService;
//import com.sz.fts.service.xjggl.XjGglService;
//import com.sz.fts.utils.BaseRquestAndResponse;
//import com.sz.fts.utils.CommonUtil;
//import net.sf.json.JSONObject;
//import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import java.io.UnsupportedEncodingException;
//import java.net.URLDecoder;
//
///**
// * @author 征华兴
// * @date 下午 2:09  2018/11/8 0008
// * @Copyright 江苏鸿信系统集成有限公司 All rights reserved 星级用户
// */
//@Controller
//@RequestMapping("xjdfs")
//public class XjDfsController extends BaseRquestAndResponse {
//
//    @Autowired
//    private XjDfsService xjDfsService;
//
//    private static Logger logger = LogManager.getLogger(XjDfsController.class);
//
//    /**
//     * 领取接口
//     */
//    @RequestMapping(value = "saveLog.do")
//    public void saveLog() throws Exception {
//        JSONObject jsonObject = CommonUtil.outDecryptMsg(getRequest());
//        //输入参数
//        JSONObject out = xjDfsService.holdLog(jsonObject);
//        //返回参数
//        CommonUtil.printEncryptMsg(getRequest(), getResponse(), out);
//    }
//
//    /**
//     * 下载接口
//     */
//    @RequestMapping("downloadLog.do")
//    public void downloadLog() {
//        getResponse().setHeader("Access-Control-Allow-Origin", "*");
//        this.xjDfsService.downloadLog(getRequest(), getResponse());
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
//
