package com.sz.fts.impl.wxpay.utils;


import com.alibaba.fastjson.JSONObject;
import com.sz.fts.impl.wxpay.wechatpay.WxPayConstants;
import com.sz.fts.utils.DateUtils;
import com.sz.fts.utils.StringUtils;
import com.sz.fts.utils.sms.MD5;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Random;

/**
 * @author 征华兴
 * @date 上午 11:00  2019/1/14 0014
 * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
 */
public class WechatPay {


    private static final Logger logger = LogManager.getLogger(WechatPay.class);
    //微信公众号ID     wx220ef99f7d84059b  流量宝 wx1575a1a993950056
    public static final String APPID = "wx220ef99f7d84059b";
    //第三方用户唯一凭证密码
    public static final String APP_SECRET = "6304d7a4f4381fe67ad63fd41589ddd2";
    //微信公众号商户号ID 1218097601  流量宝 1323521601
    public static final String MCH_ID = "1323521601";
    //微信商户平台-账户设置-安全设置-api安全,配置32位key
    public static final String KEY = "HX123456WEIXIN65432120160328WEI8";
    //交易类型
    public static final String TRADE_TYPE_JS = "JSAPI";
    //微信支付回调url
    public static final String NOTIFY_URL = "http://www.118114sz.com.cn/xmf/wxPay/reUrl.do";


    /**
     * 统一下单
     *
     * @param openid
     * @return
     * @throws IOException
     */
    public static JSONObject WxUnifiedOrder(String openid, String total_fee, HttpServletRequest request) throws IOException {
        JSONObject map = new JSONObject();
        //设置访问路径
        HttpPost httppost = new HttpPost("https://api.mch.weixin.qq.com/pay/unifiedorder");
        String body = "苏州电信微信统一支付";
        String out_trade_no = DateUtils.getCurrentTime17();   // 商户订单号（订单id + 时间戳）
        String IP = "58.211.5.59";
//        String IP = IpFindConstant.getIpAddress(request);
//        logger.info("微信支付用户IP地址" + IP);
        String nonce_str = getNonceStr().toUpperCase();//32位以内的随机字符串
        //组装请求参数,按照ASCII排序
        String sign = "appid=" + APPID +
                "&body=" + body +
                "&mch_id=" + MCH_ID +
                "&nonce_str=" + nonce_str +
                "&notify_url=" + NOTIFY_URL +
                "&openid=" + openid +
                "&out_trade_no=" + out_trade_no +
                "&spbill_create_ip=" + IP +
                "&total_fee=" + total_fee +
                "&trade_type=" + TRADE_TYPE_JS +
                "&key=" + KEY;//这个字段是用于之后MD5加密的，字段要按照ascii码顺序排序
        sign = MD5.GetMD5Code(sign).toUpperCase();
        //组装包含openid用于请求统一下单返回结果的XML
        StringBuilder sb = new StringBuilder("");
        sb.append("<xml>");
        setXmlKV(sb, "appid", WxPayConstants.APPID);  // 1
        setXmlKV(sb, "body", body);   // 5 商品描述
        setXmlKV(sb, "mch_id", WxPayConstants.MCH_ID);  // 2
        setXmlKV(sb, "nonce_str", nonce_str);    // 3
        setXmlKV(sb, "notify_url", WxPayConstants.NOTIFY_URL);  // 9
        setXmlKV(sb, "openid", openid);  // 11
        setXmlKV(sb, "out_trade_no", out_trade_no);  //  6 商户订单号
        setXmlKV(sb, "spbill_create_ip", IP);   // 8 终端IP
        setXmlKV(sb, "total_fee", total_fee);    // 7 金额
        setXmlKV(sb, "trade_type", WxPayConstants.TRADE_TYPE_JS);  // 10 交易类型
        setXmlKV(sb, "sign", sign);  // 4
        sb.append("</xml>");
        logger.info("统一下单请求：" + sb);

        StringEntity reqEntity = new StringEntity(new String(sb.toString().getBytes("UTF-8"), "ISO8859-1"));//这个处理是为了防止传中文的时候出现签名错误
        httppost.setEntity(reqEntity);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = httpclient.execute(httppost);
        String prePayInfoXml = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));
        logger.info("---返回的支付参数---" + prePayInfoXml);
        // 对返回的参数进行处理
//        String return_code = getXmlPara(prePayInfoXml, "return_code");
//        String return_msg = getXmlPara(prePayInfoXml, "return_msg");
//        map.put("return_code", return_code);
//        map.put("return_msg", return_msg);
//        if ("FAIL".equals(return_code)) {
//            // 失败
//            return map;
//        }
        String prepay_id = getXmlPara(prePayInfoXml, "prepay_id");//统一下单返回xml中prepay_id
        String timeStamp = String.valueOf((System.currentTimeMillis() / 1000));//1970年到现在的秒数
        String nonceStr = getNonceStr().toUpperCase();//随机数据字符串
        String packageStr = "prepay_id=" + prepay_id;
        String signType = "MD5";
        String paySign =
                "appId=" + APPID +
                        "&nonceStr=" + nonceStr +
                        "&package=prepay_id=" + prepay_id +
                        "&signType=" + signType +
                        "&timeStamp=" + timeStamp +
                        "&key=" + KEY;//注意这里的参数要根据ASCII码 排序
        paySign = MD5.GetMD5Code(paySign).toUpperCase();//将数据MD5加密
        map.put("appId", APPID);
        map.put("timeStamp", timeStamp);
        map.put("nonceStr", nonceStr);
        map.put("packageStr", packageStr);
        map.put("signType", signType);
        map.put("paySign", paySign);
        map.put("prepay_id", prepay_id);
        return map;
    }

    /**
     * 统一下单
     *
     * @param openid
     * @return
     * @throws IOException
     */
    public static JSONObject WxUnifiedOrderNew(String appid,String mchid ,String openid,String outTradeNo,String sign, String total_fee, HttpServletRequest request) throws IOException {
        JSONObject map = new JSONObject();
        //设置访问路径
        HttpPost httppost = new HttpPost("https://api.mch.weixin.qq.com/pay/unifiedorder");
        String body = "苏州电信微信统一支付";
      //  String out_trade_no = DateUtils.getCurrentTime17();   // 商户订单号（订单id + 时间戳）
        String IP = "58.211.5.59";
//        String IP = IpFindConstant.getIpAddress(request);
//        logger.info("微信支付用户IP地址" + IP);
        String nonce_str = getNonceStr().toUpperCase();//32位以内的随机字符串
        //组装请求参数,按照ASCII排序
//        String sign = "appid=" + appid +
//                "&body=" + body +
//                "&mch_id=" + mchid +
//                "&nonce_str=" + nonce_str +
//                "&notify_url=" + NOTIFY_URL +
//                "&openid=" + openid +
//                "&out_trade_no=" + outTradeNo +
//                "&spbill_create_ip=" + IP +
//                "&total_fee=" + total_fee +
//                "&trade_type=" + TRADE_TYPE_JS +
//                "&key=" + KEY;//这个字段是用于之后MD5加密的，字段要按照ascii码顺序排序
//        sign = MD5.GetMD5Code(sign).toUpperCase();
        //组装包含openid用于请求统一下单返回结果的XML
        StringBuilder sb = new StringBuilder("");
        sb.append("<xml>");
        setXmlKV(sb, "appid", appid);  // 1
        setXmlKV(sb, "body", body);   // 5 商品描述
        setXmlKV(sb, "mch_id", mchid);  // 2
        setXmlKV(sb, "nonce_str", nonce_str);    // 3
        setXmlKV(sb, "notify_url", WxPayConstants.NOTIFY_URL);  // 9
        setXmlKV(sb, "openid", openid);  // 11
        setXmlKV(sb, "out_trade_no", outTradeNo);  //  6 商户订单号
        setXmlKV(sb, "spbill_create_ip", IP);   // 8 终端IP
        setXmlKV(sb, "total_fee", total_fee);    // 7 金额
        setXmlKV(sb, "trade_type", WxPayConstants.TRADE_TYPE_JS);  // 10 交易类型
        setXmlKV(sb, "sign", sign);  // 4
        sb.append("</xml>");
        logger.info("统一下单请求：" + sb);

        StringEntity reqEntity = new StringEntity(new String(sb.toString().getBytes("UTF-8"), "ISO8859-1"));//这个处理是为了防止传中文的时候出现签名错误
        httppost.setEntity(reqEntity);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = httpclient.execute(httppost);
        String prePayInfoXml = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));
        logger.info("---返回的支付参数---" + prePayInfoXml);
        // 对返回的参数进行处理
//        String return_code = getXmlPara(prePayInfoXml, "return_code");
//        String return_msg = getXmlPara(prePayInfoXml, "return_msg");
//        map.put("return_code", return_code);
//        map.put("return_msg", return_msg);
//        if ("FAIL".equals(return_code)) {
//            // 失败
//            return map;
//        }
        String prepay_id = getXmlPara(prePayInfoXml, "prepay_id");//统一下单返回xml中prepay_id
        String timeStamp = String.valueOf((System.currentTimeMillis() / 1000));//1970年到现在的秒数
        String nonceStr = getNonceStr().toUpperCase();//随机数据字符串
        String packageStr = "prepay_id=" + prepay_id;
        String signType = "MD5";
        String paySign =
                "appId=" + APPID +
                        "&nonceStr=" + nonceStr +
                        "&package=prepay_id=" + prepay_id +
                        "&signType=" + signType +
                        "&timeStamp=" + timeStamp +
                        "&key=" + KEY;//注意这里的参数要根据ASCII码 排序
        paySign = MD5.GetMD5Code(paySign).toUpperCase();//将数据MD5加密
        map.put("appId", APPID);
        map.put("timeStamp", timeStamp);
        map.put("nonceStr", nonceStr);
        map.put("packageStr", packageStr);
        map.put("signType", signType);
        map.put("paySign", paySign);
        map.put("prepay_id", prepay_id);
        return map;
    }

    /**
     * 插入XML标签
     *
     * @param sb
     * @param Key
     * @param value
     * @return
     */
    public static StringBuilder setXmlKV(StringBuilder sb, String Key, String value) {
        sb.append("<");
        sb.append(Key);
        sb.append(">");

        sb.append(value);

        sb.append("</");
        sb.append(Key);
        sb.append(">");

        return sb;
    }

    public static JSONObject getPayMap(String prePayInfoXml) {
        JSONObject map = new JSONObject();
        logger.info("---返回的支付参数---" + prePayInfoXml);
        String prepay_id = getXmlPara(prePayInfoXml, "prepay_id");//统一下单返回xml中prepay_id
        String timeStamp = String.valueOf((System.currentTimeMillis() / 1000));//1970年到现在的秒数
        String nonceStr = getNonceStr().toUpperCase();//随机数据字符串
        String packageStr = "prepay_id=" + prepay_id;
        String signType = "MD5";
        String paySign =
                "appId=" + APPID +
                        "&nonceStr=" + nonceStr +
                        "&package=prepay_id=" + prepay_id +
                        "&signType=" + signType +
                        "&timeStamp=" + timeStamp +
                        "&key=" + KEY;//注意这里的参数要根据ASCII码 排序
        paySign = MD5.GetMD5Code(paySign).toUpperCase();//将数据MD5加密
        map.put("appId", APPID);
        map.put("timeStamp", timeStamp);
        map.put("nonceStr", nonceStr);
        map.put("packageStr", packageStr);
        map.put("signType", signType);
        map.put("paySign", paySign);
        map.put("prepay_id", prepay_id);
        return map;
    }

    /**
     * 解析XML 获得名称为para的参数值
     *
     * @param xml
     * @param para
     * @return
     */
    public static String getXmlPara(String xml, String para) {

        int start = xml.indexOf("<" + para + ">");
        int end = xml.indexOf("</" + para + ">");
        if (start < 0 && end < 0) {
            return null;
        }
        return xml.substring(start + ("<" + para + ">").length(), end).replace("<![CDATA[", "").replace("]]>", "");
    }

    /**
     * 修改订单状态，获取微信回调结果
     *
     * @param request
     * @return
     */
    public static String getNotifyResult(HttpServletRequest request) {
        String inputLine;
        String notifyXml = "";
        String resXml = "";
        try {
            while ((inputLine = request.getReader().readLine()) != null) {
                notifyXml += inputLine;
            }
            request.getReader().close();
        } catch (Exception e) {
            logger.info("xml获取失败：" + e);
            e.printStackTrace();
        }
        logger.info("接收到的xml：" + notifyXml);
        logger.info("收到微信异步回调：");
        if (StringUtils.isEmpty(notifyXml)) {
            logger.info("xml为空：");
        }
        String return_code = getXmlPara(notifyXml, "return_code");  // 以下字段在return_code为SUCCESS的时候有返回
        String appid = getXmlPara(notifyXml, "appid");  // 1
        String mch_id = getXmlPara(notifyXml, "mch_id"); // 2
        String nonce_str = getXmlPara(notifyXml, "nonce_str"); // 3
        String sign = getXmlPara(notifyXml, "sign"); //4
        String result_code = getXmlPara(notifyXml, "result_code");// 5 业务结果
        String openid = getXmlPara(notifyXml, "openid"); //6
        String is_subscribe = getXmlPara(notifyXml, "is_subscribe"); //7
        String trade_type = getXmlPara(notifyXml, "trade_type"); //8
        String bank_type = getXmlPara(notifyXml, "bank_type");  // 9 付款银行
        String total_fee = getXmlPara(notifyXml, "total_fee"); //10 订单总金额，单位为分
        String cash_fee = getXmlPara(notifyXml, "cash_fee");  // 11 现金支付金额
        String transaction_id = getXmlPara(notifyXml, "transaction_id"); // 12 微信支付订单号
        String out_trade_no = getXmlPara(notifyXml, "out_trade_no"); // 13 商户订单号
        String time_end = getXmlPara(notifyXml, "time_end"); // 14 订单完成时间
//        String fee_type = getXmlPara(notifyXml, "fee_type");   货币种类
        //TODO  进行加锁控制，判断该订单是否已经通知过


        //根据返回xml计算本地签名
        String localSign =
                "appid=" + appid +
                        "&bank_type=" + bank_type +
                        "&cash_fee=" + cash_fee +
//                        "&fee_type=" + fee_type +
                        "&is_subscribe=" + is_subscribe +
                        "&mch_id=" + mch_id +
                        "&nonce_str=" + nonce_str +
                        "&openid=" + openid +
                        "&out_trade_no=" + out_trade_no +
                        "&result_code=" + result_code +
                        "&return_code=" + return_code +
                        "&time_end=" + time_end +
                        "&total_fee=" + total_fee +
                        "&trade_type=" + trade_type +
                        "&transaction_id=" + transaction_id +
                        "&key=" + WxPayConstants.KEY;//注意这里的参数要根据ASCII码 排序
        localSign = MD5.GetMD5Code(localSign).toUpperCase();//将数据MD5加密

        System.out.println("本地签名是：" + localSign);
        System.out.println("微信支付签名是：" + sign);

        //本地计算签名与微信返回签名不同||返回结果为不成功
        if (!sign.equals(localSign) || !"SUCCESS".equals(result_code) || !"SUCCESS".equals(return_code)) {
            System.out.println("验证签名失败或返回错误结果码");
            resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" + "<return_msg><![CDATA[FAIL]]></return_msg>" + "</xml> ";
        } else {
            System.out.println("公众号支付成功，out_trade_no(订单号)为：" + out_trade_no);
            resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>" + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
        }
        return resXml;
    }

    /**
     * 获取32位随机字符串
     *
     * @return
     */
    public static String getNonceStr() {
        String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random rd = new Random();
        for (int i = 0; i < 32; i++) {
            sb.append(str.charAt(rd.nextInt(str.length())));
        }
        return sb.toString();
    }

    public static void main(String[] args) {

//        try { // oFYchs0SQU5dVH0WhvaXPNq8gXWE   oeSlKxGK-_NfZOCPnOFTGQhY0gJs
//            JSONObject result = WxUnifiedOrder("oFYchs0SQU5dVH0WhvaXPNq8gXWE", "1", null);
//            logger.info("-result-" + result);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        Random r = new Random();
        for (int i = 0; i < 20; i++) {
            //   logger.info(new Random().nextInt(50));
            System.out.println(r.nextInt(50));
        }
    }
}
