package com.sz.fts.controller.common;


import com.sz.fts.redis.repository.RedisAction;
import com.sz.fts.service.common.CommonService;
import com.sz.fts.utils.BaseRquestAndResponse;
import com.sz.fts.utils.CommonUtil;
import com.sz.fts.utils.HttpUtil;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @author 征华兴
 * @date 下午 2:48  2018/7/17 0017
 * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
 */
@Controller
@RequestMapping("common")
public class CommonController extends BaseRquestAndResponse {
    @Autowired
    private CommonService commonService;

    private static Logger logger = LogManager.getLogger(CommonController.class);
    // 威得尔appid secret
    private static final String SECRET = "066f30ff6ad1b4148e4a6f8b8262ca26";
    private static final String APPID = "wxe5930e19f587de32";


    /**
     * 获取威得尔 openId
     *
     * @return
     */
    @RequestMapping(value = "/getCode.do")
    public ModelAndView getCode(@RequestParam String uuid) {
        getResponse().setHeader("Access-Control-Allow-Origin", "*");
        try {
            System.out.println("------------uuid111111------" + uuid);
            String URL = "http://www.118114sz.com.cn/xmf/common/getOpenId.do?uuid=" + uuid;
            String jumpURL = "redirect:https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxe5930e19f587de32&redirect_uri=" + URLEncoder.encode(URL) + "&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";
            //跳转路径
            System.out.println("===========" + jumpURL);
            return new ModelAndView(jumpURL);
        } catch (Exception e) {
            e.printStackTrace();
            return new ModelAndView("404");
        }
    }

    @Autowired
    private RedisAction redisAction;

    /**
     * 获取威得尔 openId
     *
     * @return
     */
    @RequestMapping(value = "/getOpenId.do")
    public ModelAndView getOpenid(@RequestParam(required = false) String uuid, @RequestParam String openid) {
        getResponse().setHeader("Access-Control-Allow-Origin", "*");
        String URL = "http://www.118114sz.com.cn/xmf/common/getOpenIdThr.do?szdianxinOpenid=" + openid;
        String jumpURL = "redirect:https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + APPID + "&redirect_uri=" +
                URLEncoder.encode(URL) + "&response_type=code&scope=snsapi_base&state=" + uuid + "#wechat_redirect";
        return new ModelAndView(jumpURL);
    }

//    /**
//     * 获取openId
//     *
//     * @return
//     */
//    @RequestMapping(value = "/getOpenIdTwo.do")
//    public ModelAndView getOpenidTwo(@RequestParam(required = false) String szdianxinOpenid, @RequestParam(required = false) String openid) {
//        Enumeration enu = getRequest().getParameterNames();
//        while (enu.hasMoreElements()) {
//            String paraName = (String) enu.nextElement();
//            System.out.println(paraName + ": " + getRequest().getParameter(paraName));
//        }
//        System.out.println("============威德尔openid=============" + szdianxinOpenid);
//        String uuid = getRequest().getParameter("state");
//        System.out.println("============uuid=============" + uuid);
//        // 苏州电信openid
//        System.out.println("=============苏州电信openid=============" + openid);
//        return new ModelAndView("redirect:http://app1.118114sz.com/active/Rollhuafei/linker.html");
//    }

    /**
     * 获取openId
     *
     * @return
     */
    @RequestMapping(value = "/getOpenIdThr.do")
    public ModelAndView getOpenidThr(@RequestParam String szdianxinOpenid) {
        getResponse().setHeader("Access-Control-Allow-Origin", "*");
        String result = "";
        try {
            String code = getRequest().getParameter("code");
            String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + APPID + "&secret=" + SECRET + "&code=" + code + "&grant_type=authorization_code";
            result = HttpUtil.postUrl(url, "wechat");
            JSONObject resOut1 = JSONObject.fromObject(result);
            String wdrOpenid = resOut1.getString("openid");
            redisAction.setString("huafei" + szdianxinOpenid, wdrOpenid, -1);
            return new ModelAndView("redirect:http://app1.118114sz.com/active/Rollhuafei/index.html?openid=" + szdianxinOpenid);
        } catch (Exception e) {
            e.printStackTrace();
            // 出现异常 返回 3
            return new ModelAndView("redirect:http://app1.118114sz.com/active/Rollhuafei/index.html?openid=" + szdianxinOpenid);
        }
    }

    /**
     * 获取openId
     *
     * @return
     */
    @RequestMapping(value = "/tixian.do")
    public void tiXian() throws Exception {
        getResponse().setHeader("Access-Control-Allow-Origin", "*");
        JSONObject jsonObject = CommonUtil.outDecryptMsg(getRequest());
        logger.info("********传入参数*******" + jsonObject);
        JSONObject out = this.commonService.payMoney(jsonObject);
        CommonUtil.printEncryptMsg(getRequest(), getResponse(), out);
    }


    private String getParam(String param) throws UnsupportedEncodingException {
        String out = URLDecoder.decode(param, "UTF-8");
        out = out.replace("=", "\":\"");
        out = out.replace("&", "\",\"");
        out = "{\"" + out + "\"}";
        System.out.println(out);
        return out;
    }

    public static void printOutMsg(HttpServletRequest request, HttpServletResponse response, JSONObject msg)
            throws Exception {
        try {
            PrintWriter pw = response.getWriter();
            pw.write(msg.toString());
            pw.flush();
            pw.close();
        } catch (IOException e) {
            logger.error("response wirter fail!", e);
        }
    }


}
