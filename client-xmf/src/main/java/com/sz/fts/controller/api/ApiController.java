//package com.sz.fts.controller.api;
//
//import com.sz.fts.utils.BaseRquestAndResponse;
//import com.sz.fts.utils.CommonUtil;
//import net.sf.json.JSONObject;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
///**
// * @author 征华兴
// * @date 下午 2:04  2018/7/16 0016
// * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
// */
//@Controller
//@RequestMapping("api")
//public class ApiController extends BaseRquestAndResponse {
//
//    /**
//     * 通过手机号码获取奖品信息
//     */
//    @RequestMapping(value = "/phoneInfo.do")
//    public void getUserInfo(@RequestBody String json) {
//        JSONObject out = new JSONObject();
//        try {
//            JSONObject in = JSONObject.fromObject(json);
//            try {
//                CommonUtil.printOutMsg(getRequest(), getResponse(), out);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }
//    }
//}