//package com.sz.fts.controller.engineer;
//
//import com.sz.fts.controller.gxxmf.GxXmfController;
//import com.sz.fts.service.engineer.ZsEngineerService;
//import com.sz.fts.utils.BaseRquestAndResponse;
//import com.sz.fts.utils.CommonUtil;
//import com.sz.fts.utils.WechatUpload;
//import net.sf.json.JSONObject;
//import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.servlet.ModelAndView;
//
//import java.io.IOException;
//import java.net.URLEncoder;
//
///**
// * @author 征华兴
// * @date 下午 1:54  2018/11/15 0015
// * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
// */
//@Controller
//@RequestMapping("zs/engineer")
//public class ZsEngineerController extends BaseRquestAndResponse{
//
//    @Autowired
//    private ZsEngineerService zsEngineerService;
//    private static final Logger logger = LogManager.getLogger(ZsEngineerController.class);
//    /**
//     * 入口，通过openid获取用户信息
//     */
//    @RequestMapping("getUserInfo.do")
//    public void getUserInfo() throws Exception {
//        getResponse().setHeader("Access-Control-Allow-Origin", "*");
//        JSONObject jsonObject = CommonUtil.outDecryptMsg(getRequest());
//        logger.info("--------------专属工程师OpenId-----------：" + jsonObject);
//        JSONObject out = this.zsEngineerService.getUserInfo(jsonObject.toString());
//        CommonUtil.printEncryptMsg(getRequest(), getResponse(), out);
//    }
//    /**
//     * 工程师套餐信息
//     *
//     * @param
//     * @return
//     * @throws
//     */
//    @RequestMapping("queryTaocan.do")
//    public void queryTaocanList() throws Exception {
//        getResponse().setHeader("Access-Control-Allow-Origin", "*");
//        JSONObject json = CommonUtil.outDecryptMsg(getRequest());
//        JSONObject out = zsEngineerService.queryTaocanList(json.toString());
//        logger.info("*********专属工程师***********" + out);
//        CommonUtil.printEncryptMsg(getRequest(), getResponse(), out);
//    }
//
//    /**
//     * 提交订单
//     *
//     * @param
//     */
//    @RequestMapping(value = "/saveByTaocan.do")
//    public void saveInfoByTaocan() throws Exception {
//        getResponse().setHeader("Access-Control-Allow-Origin", "*");
//        JSONObject json = CommonUtil.outDecryptMsg(getRequest());
//        logger.info("--------------专属工程师下单参数-----------：" + json);
//        JSONObject out = this.zsEngineerService.holdInfoByTaocan(json.toString());
//        CommonUtil.printEncryptMsg(getRequest(), getResponse(), out);
//    }
//
//    /**
//     * 点击套餐 查询套餐内容
//     */
//    @RequestMapping(value = "/description.do")
//    public void queryTaocanContent() throws Exception {
//        getResponse().setHeader("Access-Control-Allow-Origin", "*");
//        JSONObject json = CommonUtil.outDecryptMsg(getRequest());
//        JSONObject out = this.zsEngineerService.taocanInfo(json.toString());
//        CommonUtil.printEncryptMsg(getRequest(), getResponse(), out);
//    }
//
//    /**
//     * 通过openid,status
//     * 查询用户进行中，全部的订单信息
//     */
//    @RequestMapping("queryProcessOrder.do")
//    public void queryProcessOrder() throws Exception {
//        getResponse().setHeader("Access-Control-Allow-Origin", "*");
//        JSONObject json = CommonUtil.outDecryptMsg(getRequest());
//        JSONObject out = this.zsEngineerService.getProcessOrder(json.toString());
//        CommonUtil.printEncryptMsg(getRequest(), getResponse(), out);
//    }
//
////    /**
////     * 通过openid  查询订单审核完成信息
////     *
////     * @param
////     * @return
////     * @throws
////     */
////    @RequestMapping("querySuccessOrderList.do")
////    public void queryOrderList() throws Exception {
////        getResponse().setHeader("Access-Control-Allow-Origin", "*");
////        JSONObject json = CommonUtil.outDecryptMsg(getRequest());
////        JSONObject out = this.zsEngineerService.querySuccessOrderList(json.toString());
////        CommonUtil.printEncryptMsg(getRequest(), getResponse(), out);
////    }
//
//    /**
//     * 获取openId
//     *
//     * @return
//     */
//    @RequestMapping(value = "/getOpenId.do")
//    public ModelAndView getOpenid(@RequestParam String openId) {
//        String URL = "http://app1.118114sz.com/active/excuBroad/pages/tixian/tixian.html?llbOpenId=" + openId;
//        System.out.println("===========" + URL);
//        String jumpURL = "redirect:https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx220ef99f7d84059b&redirect_uri=http%3a%2f%2fquanzi.118114sz.com%2fwechat%2ffindOpenid%3ft%3d7%26u%3d" +
//                URLEncoder.encode(URL) + "&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";
//        System.out.println("===========" + jumpURL);
//        //跳转路径
//        return new ModelAndView(jumpURL);
//    }
//
//    /**
//     * 小蜜蜂提现
//     */
//    @RequestMapping("payMoney.do")
//    public void payMoney() throws Exception {
//        getResponse().setHeader("Access-Control-Allow-Origin", "*");
//        JSONObject jsonObject = CommonUtil.outDecryptMsg(getRequest());
//        logger.info("********传入参数*******" + jsonObject);
//        JSONObject out = this.zsEngineerService.payMoney(jsonObject.toString());
//        CommonUtil.printEncryptMsg(getRequest(), getResponse(), out);
//    }
//    /**
//     * 提现记录
//     */
//    @RequestMapping("queryTxInfo.do")
//    public void queryTxInfo() throws Exception {
//        getResponse().setHeader("Access-Control-Allow-Origin", "*");
//        JSONObject jsonObject = CommonUtil.outDecryptMsg(getRequest());
//        JSONObject out = this.zsEngineerService.queryTxInfo(jsonObject.toString());
//        CommonUtil.printEncryptMsg(getRequest(), getResponse(), out);
//    }
//    /**
//     * 导出订单审核
//     */
//    @RequestMapping(value = "/downloadOrder.do")
//    public void downloadOrder(@RequestParam String extend9) throws Exception {
//        getResponse().setHeader("Access-Control-Allow-Origin", "*");
//        this.zsEngineerService.downloadOrder(extend9, getRequest(), getResponse());
//    }
//
//}
