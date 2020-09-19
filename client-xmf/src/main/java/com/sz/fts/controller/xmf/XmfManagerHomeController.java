//package com.sz.fts.controller.xmf;
//
//import com.sz.fts.utils.CommonUtil;
//import com.sz.fts.utils.ShareUtils;
//import com.sz.fts.service.xmf.XmfService;
//import com.sz.fts.utils.BaseRquestAndResponse;
//import net.sf.json.JSONObject;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
///**
// * @author 征华兴
// * @date 下午 2:34  2018/4/13 0013
// * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
// */
//@Controller
//@RequestMapping(value = "/com/plaftorm/xmfManager")
//public class XmfManagerHomeController extends BaseRquestAndResponse {
//
//    @Autowired
//    private XmfService xmfService;
//
//    @RequestMapping(value = "/unifyEntrance.do")
//    public void unifyEntrance(@RequestParam String method) throws Exception {
//        // 解析数据
//        JSONObject params = CommonUtil.analysisRequestMsg(getRequest());
//        JSONObject outcome = new JSONObject();
//        try {
//            // 根据标识把用户从内存数据库中取出
//            JSONObject authorData = JSONObject.fromObject(getSession().getAttribute(params.getString("authorizeKey")).toString());
//
//            // 验证签名是否正确
//            if (ShareUtils.getPlatformSign(OwnPlatformTool.jsonToMap(params), params.getString("sign"),
//                    authorData.getString("authorizeCode"))) {
//                switch (method) {
//                    case "list":
//                        outcome = this.xmfService.managerList(params.toString());
//                        break;
////                    case "update":
////                        outcome = this.xmfService.updateOrder(params.toString());
////                        break;
////                    case "delete":
////                        outcome = this.xmfService.delOrder(params.toString());
////                        break;
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
