package com.sz.fts.controller.wxController;


import com.sz.fts.service.wxpay.WxPayService;
import com.sz.fts.utils.BaseRquestAndResponse;
import com.sz.fts.utils.CommonUtil;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Enumeration;


/**
 * @author
 * @version [版本号, 2018/4/12]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
@RestController
@RequestMapping("/wxPay")
public class WxController extends BaseRquestAndResponse {


    @Autowired
    private WxPayService wxPayService;


    /**
     * 调用省公司接口native
     */
    @RequestMapping("preOrderNative.do")
    public void preOrderOne() throws Exception {
        getResponse().setHeader("Access-Control-Allow-Origin", "*");
        JSONObject jsonObject = CommonUtil.outDecryptMsg(getRequest());
        JSONObject out = wxPayService.preOrderOne(jsonObject);
        CommonUtil.printEncryptMsg(getRequest(), getResponse(), out);
    }

    /**
     * 调用省公司接口jsapi
     */
    @RequestMapping("preOrderJsapi.do")
    public void preOrderTwo() throws Exception {
        getResponse().setHeader("Access-Control-Allow-Origin", "*");
        JSONObject jsonObject = CommonUtil.outDecryptMsg(getRequest());
        JSONObject out = wxPayService.preOrderTwo(jsonObject.toString());
        CommonUtil.printEncryptMsg(getRequest(), getResponse(), out);
    }

    /**
     * 调用省公司接口jsapi
     */
    @RequestMapping("preOrderJsapiThr.do")
    public void preOrderThr() throws Exception {
        getResponse().setHeader("Access-Control-Allow-Origin", "*");
        JSONObject jsonObject = CommonUtil.outDecryptMsg(getRequest());
        JSONObject out = wxPayService.preOrderThr(jsonObject.toString());
        CommonUtil.printEncryptMsg(getRequest(), getResponse(), out);
    }

    /**
     * 社区宽带支付
     */
    @RequestMapping("preOrderJsapiFour.do")
    public void preOrderFour() throws Exception {
        getResponse().setHeader("Access-Control-Allow-Origin", "*");
        JSONObject jsonObject = CommonUtil.outDecryptMsg(getRequest());
        JSONObject out = wxPayService.preOrderFour(jsonObject);
        CommonUtil.printEncryptMsg(getRequest(), getResponse(), out);
    }

    /**
     * 权益充值支付
     */
    @RequestMapping("preOrderJsapiQuanyi.do")
    public void preOrderQuanyi() throws Exception {
        getResponse().setHeader("Access-Control-Allow-Origin", "*");
        JSONObject jsonObject = CommonUtil.outDecryptMsg(getRequest());
        JSONObject out = wxPayService.preOrderQuanyi(jsonObject);
        CommonUtil.printEncryptMsg(getRequest(), getResponse(), out);
    }

    /**
     * common支付
     */
    @RequestMapping("preOrderJsapiCommon.do")
    public void preOrderJsapiCommon() {
        getResponse().setHeader("Access-Control-Allow-Origin", "*");
        JSONObject jsonObject = CommonUtil.outDecryptMsg(getRequest());
        JSONObject out = wxPayService.preOrderJsapiCommon(jsonObject);
        CommonUtil.printEncryptMsg(getRequest(), getResponse(), out);
    }

    /**
     * wap支付
     */
    @RequestMapping("preOrderWap.do")
    public void preOrderWap() {
        JSONObject jsonObject = CommonUtil.outDecryptMsg(getRequest());
        JSONObject out = wxPayService.preOrderWap(jsonObject);
        CommonUtil.printEncryptMsg(getRequest(), getResponse(), out);
    }
    /**
     * wap支付
     */
    @RequestMapping("testPay.do")
    public void testPay() {
        JSONObject jsonObject = CommonUtil.outDecryptMsg(getRequest());
        JSONObject out = wxPayService.testPay(jsonObject);
        CommonUtil.printEncryptMsg(getRequest(), getResponse(), out);
    }


    /**
     * 省公司结果通知
     */
    @RequestMapping("testNotify.do")
    public void getNotify() {
        try {
            String result = wxPayService.notifyResult(getRequest());
            BufferedOutputStream out = new BufferedOutputStream(getResponse().getOutputStream());
            out.write(result.getBytes());
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 省公司结果通知
     */
    @RequestMapping("testNotifyThr.do")
    public void getNotifyThr() {
        try {
            String result = wxPayService.notifyResultThr(getRequest());
            BufferedOutputStream out = new BufferedOutputStream(getResponse().getOutputStream());
            System.out.println("==testNotifyThr===" + result);
            out.write(result.getBytes());
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 省公司结果通知
     */
    @RequestMapping("testNotifyFour.do")
    public void getNotifyFour() {
        try {
            String result = wxPayService.notifyResultFour(getRequest());
            BufferedOutputStream out = new BufferedOutputStream(getResponse().getOutputStream());
            out.write(result.getBytes());
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 省公司结果通知
     */
    @RequestMapping("testNotifyQuanyi.do")
    public void getNotifyQuanyi() {
        try {
            String result = wxPayService.notifyResultQuanyi(getRequest());
            BufferedOutputStream out = new BufferedOutputStream(getResponse().getOutputStream());
            System.out.println("==testNotifyQuanyi===" + result);
            out.write(result.getBytes());
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 省公司结果通知
     */
    @RequestMapping("commonNotify.do")
    public void commonNotify() {
        try {
            String result = wxPayService.commonNotify(getRequest());
            BufferedOutputStream out = new BufferedOutputStream(getResponse().getOutputStream());
            System.out.println("==commonNotify===" + result);
            out.write(result.getBytes());
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 省公司结果通知
     */
    @RequestMapping("wapByNotify.do")
    public void wapByNotify() {
        try {
            String result = wxPayService.wapByNotify(getRequest());
            BufferedOutputStream out = new BufferedOutputStream(getResponse().getOutputStream());
            System.out.println("==wapByNotify结果===" + result);
            out.write(result.getBytes());
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 提供给第三方支付
     */
    @RequestMapping("preOrder")
    public void preOrder(@RequestBody String json) throws UnsupportedEncodingException {
        System.out.println("==第三方pay==" + json);
        System.out.println("ip:" + getRequest().getHeader("X-Forwarded-For"));
        JSONObject out = wxPayService.prepayOrderjsapi(JSONObject.fromObject(json), getRequest());
        CommonUtil.printOutMsg(getRequest(), getResponse(), out);
    }

    /**
     * 省公司结果通知
     */
    @RequestMapping("thirdPart.do")
    public void thirdPart() {
        try {
            String result = wxPayService.thirdPart(getRequest());
            BufferedOutputStream out = new BufferedOutputStream(getResponse().getOutputStream());
            System.out.println("==pay结果===" + result);
            out.write(result.getBytes());
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getParam(String param) throws UnsupportedEncodingException {
        String result = URLDecoder.decode(param, "UTF-8");
        result = result.replace("&", "\",\"");
        result = result.replace("=", "\":\"");
        result = "{\"" + result + "\"}";
        System.out.println(result);
        return result;
    }
}



