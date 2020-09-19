//package com.sz.fts.controller.gxxmf;
//
//import com.sz.fts.utils.CommonUtil;
//import com.sz.fts.redis.repository.RedisAction;
//import com.sz.fts.service.gxxmf.GxXmfService;
//import com.sz.fts.utils.BaseRquestAndResponse;
//import net.sf.json.JSONObject;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
///**
// * @author 征华兴
// * @date 下午 12:01  2019/5/31 0031
// * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
// */
//@Controller
//@RequestMapping(value = "/com/plaftorm/xyLinkLog")
//public class GxLinkerHomeController extends BaseRquestAndResponse {
//
//    @Autowired
//    private GxXmfService gxXmfService;
//    @Autowired
//    private RedisAction redisAction;
//
//    @RequestMapping(value = "/unifyEntrance.do")
//    public void unifyEntrance(@RequestParam String method) throws Exception {
//        getResponse().setHeader("Access-Control-Allow-Origin", "*");
//        // 解析数据
//        JSONObject params = CommonUtil.analysisRequestMsg(getRequest());
//        String appKey = params.has("authorizeKey") ? params.getString("authorizeKey") : "";
//        JSONObject in = JSONObject.fromObject(redisAction.getString(appKey));
//        JSONObject outcome = new JSONObject();
//        try {
//            if (in != null && (in.getString("roleArray").contains("4") || in.getString("roleArray").contains("193"))) {
//                switch (method) {
//                    case "list":
//                        outcome = this.gxXmfService.logList(params.toString());
//                        break;
//                    case "disable":
//                        outcome = this.gxXmfService.saveLog(params.toString());
//                        break;
//                    case "update":
//                        outcome = this.gxXmfService.updateLog(params.toString());
//                        break;
//                    case "delete":
//                        outcome = this.gxXmfService.delLog(params.toString());
//                        break;
//                    default:
//                }
//            } else {
//                outcome = CommonUtil.printPlatform(40002);
//            }
//        } catch (Exception e) {
//            System.out.println(e);
//            e.printStackTrace();
//            outcome = CommonUtil.printPlatform(1);
//        }
//        // 返回信息
//        CommonUtil.printPlatformMsg(true, getResponse(), outcome);
//    }
//}
