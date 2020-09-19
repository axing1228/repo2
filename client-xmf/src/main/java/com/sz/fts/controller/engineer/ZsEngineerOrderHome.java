//package com.sz.fts.controller.engineer;
//
//import com.sz.fts.utils.CommonUtil;
//import com.sz.fts.utils.OwnPlatformTool;
//import com.sz.fts.utils.ShareUtils;
//import com.sz.fts.redis.repository.RedisAction;
//import com.sz.fts.service.engineer.ZsEngineerService;
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
// * @date 下午 3:27  2018/11/19 0019
// * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
// */
//@Controller
//@RequestMapping(value = "/com/plaftorm/zsEngineerOrder")
//public class ZsEngineerOrderHome extends BaseRquestAndResponse {
//
//    @Autowired
//    private ZsEngineerService zsEngineerService;
//    @Autowired
//    private RedisAction redisAction;
//    @RequestMapping(value = "/unifyEntrance.do")
//    public void unifyEntrance(@RequestParam String method) throws Exception {
//        getResponse().setHeader("Access-Control-Allow-Origin", "*");
//        // 解析数据
//        JSONObject params = CommonUtil.analysisRequestMsg(getRequest());
//        assert params != null;
//        String appKey = params.has("authorizeKey") ? params.getString("authorizeKey") : "";
//        JSONObject in = JSONObject.fromObject(redisAction.getString(appKey));
//        JSONObject outcome = new JSONObject();
//        try {
//            if (in != null && (in.getString("roleArray").contains("4") || in.getString("roleArray").contains("161"))) {
//                switch (method) {
//                    case "list":
//                        outcome = this.zsEngineerService.orderList(params.toString());
//                        break;
//                    case "disable":
//                        outcome = this.zsEngineerService.saveOrder(params.toString());
//                        break;
//                    case "update":
//                        outcome = this.zsEngineerService.updateOrder(params.toString());
//                        break;
//                    case "delete":
//                        outcome = this.zsEngineerService.delOrder(params.toString());
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
//
//
