package com.sz.fts.controller.gxxmf;

import com.sz.fts.service.gxxmf.GxXmfService;
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

import java.net.URLEncoder;

/**
 * @author 征华兴
 * @date 下午 1:49  2018/6/20 0020
 * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
 */
@Controller
@RequestMapping(value = "/gxXmf")
public class GxXmfController extends BaseRquestAndResponse {

    @Autowired
    private GxXmfService gxXmfService;

    private static final Logger logger = LogManager.getLogger(GxXmfController.class);

    private static final String SECRET = "5e01223cc793593a9111a274b2ea8a26";
    private static final String APPID = "wx0b8dfe36ecf03148";

    /**
     * 获取openId
     *
     * @return
     */
    @RequestMapping(value = "/getOpenId.do")
    public ModelAndView getOpenid() {
        String code = getRequest().getParameter("code");
        try {
            String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + APPID + "&secret=" + SECRET + "&code=" + code + "&grant_type=authorization_code";
            String result = HttpUtil.postUrl(url, "wechat");
            System.out.println("-------------------------result-------------------" + result);
            JSONObject out = JSONObject.fromObject(result);
            String openid = out.getString("openid");
            String URL = "http://app1.118114sz.com/active/schoolLink/pages/tixian.html?llbOpenId=" + openid;
            String jumpURL = "redirect:https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx220ef99f7d84059b&redirect_uri=http%3a%2f%2fquanzi.118114sz.com%2fwechat%2ffindOpenid%3ft%3d7%26u%3d" +
                    URLEncoder.encode(URL) + "&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";
            System.out.println("===========" + jumpURL);
            //跳转路径
            return new ModelAndView(jumpURL);
        } catch (Exception e) {
            e.printStackTrace();
            return new ModelAndView("404");
        }
    }

    /**
     * 入口，通过openid获取用户信息
     */
    @RequestMapping("getUserInfo.do")
    public void getUserInfo() throws Exception {
        getResponse().setHeader("Access-Control-Allow-Origin", "*");
        JSONObject jsonObject = CommonUtil.outDecryptMsg(getRequest());
        logger.info("--------------高校小蜜蜂OpenId-----------：" + jsonObject);
        JSONObject out = this.gxXmfService.getUserInfo(jsonObject.toString());
        CommonUtil.printEncryptMsg(getRequest(), getResponse(), out);
    }

    /**
     * 高校小蜜蜂套餐信息
     *
     * @param
     * @return
     * @throws
     */
    @RequestMapping("queryTaocan.do")
    public void queryTaocanList() throws Exception {
        getResponse().setHeader("Access-Control-Allow-Origin", "*");
        JSONObject jsonObject = CommonUtil.outDecryptMsg(getRequest());
        JSONObject out = gxXmfService.queryTaocanList(jsonObject.toString());
        CommonUtil.printEncryptMsg(getRequest(), getResponse(), out);
    }

    /**
     * 通过openid,status
     * 查询用户进行中，全部的订单信息
     */
    @RequestMapping("queryProcessOrder.do")
    public void queryProcessOrder() throws Exception {
        getResponse().setHeader("Access-Control-Allow-Origin", "*");
        JSONObject jsonObject = CommonUtil.outDecryptMsg(getRequest());
        JSONObject out = this.gxXmfService.getProcessOrder(jsonObject.toString());
        CommonUtil.printEncryptMsg(getRequest(), getResponse(), out);
    }

    /**
     * 通过openid  查询订单审核完成信息
     */
    @RequestMapping("querySuccessOrderList.do")
    public void queryOrderList() throws Exception {
        getResponse().setHeader("Access-Control-Allow-Origin", "*");
        JSONObject jsonObject = CommonUtil.outDecryptMsg(getRequest());
        JSONObject out = this.gxXmfService.querySuccessOrderList(jsonObject.toString());
        CommonUtil.printEncryptMsg(getRequest(), getResponse(), out);
    }

    /**
     * 查询用户提现信息接口
     *
     * @param
     * @throws Exception
     */
    @RequestMapping("queryTxInfo.do")
    public void queryTxInfo() throws Exception {
        getResponse().setHeader("Access-Control-Allow-Origin", "*");
        JSONObject jsonObject = CommonUtil.outDecryptMsg(getRequest());
        JSONObject out = this.gxXmfService.queryTxInfo(jsonObject.toString());
        CommonUtil.printEncryptMsg(getRequest(), getResponse(), out);
    }

    /**
     * 通过套餐名称提交订单
     *
     * @param
     */
    @RequestMapping(value = "/save.do")
    public void saveInfoByTaocan() throws Exception {
        getResponse().setHeader("Access-Control-Allow-Origin", "*");
        JSONObject jsonObject = CommonUtil.outDecryptMsg(getRequest());
        logger.info("--------------新校园linker传入参数-----------：" + jsonObject);
        JSONObject out = this.gxXmfService.save(jsonObject.toString());
        CommonUtil.printEncryptMsg(getRequest(), getResponse(), out);
    }

    /**
     * 小蜜蜂提现
     */
    @RequestMapping("payMoney.do")
    public void payMoney() throws Exception {
        getResponse().setHeader("Access-Control-Allow-Origin", "*");
        JSONObject jsonObject = CommonUtil.outDecryptMsg(getRequest());
        logger.info("********提现*******" + jsonObject);
        JSONObject out = this.gxXmfService.payMoney(jsonObject.toString());
        CommonUtil.printEncryptMsg(getRequest(), getResponse(), out);
    }

    /**
     * 通过套餐名称提交订单
     *
     * @param
     */
    @RequestMapping(value = "/saveQuanyi.do")
    public void saveQuanyi() throws Exception {
        getResponse().setHeader("Access-Control-Allow-Origin", "*");
        JSONObject jsonObject = CommonUtil.outDecryptMsg(getRequest());
        JSONObject out = this.gxXmfService.saveQuanyi(jsonObject.toString());
        CommonUtil.printEncryptMsg(getRequest(), getResponse(), out);
    }

    /**
     * 导出订单表信息
     */

    @RequestMapping(value = "/downloadLog.do")
    public void downloadLog(@RequestParam String extend9) throws Exception {
        getResponse().setHeader("Access-Control-Allow-Origin", "*");
        this.gxXmfService.downloadLog(extend9, getRequest(), getResponse());
    }
}

