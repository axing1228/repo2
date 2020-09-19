package com.sz.fts.impl.wxpay;


import com.sz.fts.bean.hhact.AActLog;
import com.sz.fts.bean.wxpay.CActivityOrder;
import com.sz.fts.bean.wxpay.WxCallbackOrder;
import com.sz.fts.bean.wxpay.WxGoodsOrder;
import com.sz.fts.bean.yuyue.YuyuePhone;
import com.sz.fts.bean.yuyuekunsan.YYOrderKS;
import com.sz.fts.bean.zjf.MobileNumber;
import com.sz.fts.bean.zjf.PersonalConstants;
import com.sz.fts.dao.hhact.AActLogMapper;
import com.sz.fts.dao.wxpay.CActivityOrderMapper;
import com.sz.fts.dao.wxpay.WxCallbackOrderMapper;
import com.sz.fts.dao.wxpay.WxGoodsOrderMapper;
import com.sz.fts.dao.yuyue.YuyuePhoneMapper;
import com.sz.fts.dao.yuyuekunsan.YYOrderKSMapperTwo;
import com.sz.fts.dao.zjf.MobileNumberMapper;
import com.sz.fts.impl.wxpay.payment.PaymentManageServiceServiceLocator;
import com.sz.fts.impl.wxpay.payment.PaymentManageServiceSoapBindingStub;
import com.sz.fts.impl.wxpay.utils.UrlEncodeForPay;
import com.sz.fts.redis.repository.RedisAction;
import com.sz.fts.service.wxpay.WxPayService;
import com.sz.fts.service.yuyuekunsan.YuYueLHService;
import com.sz.fts.utils.DateUtils;
import com.sz.fts.utils.HttpUtil;
import com.sz.fts.utils.IpFindConstant;
import com.sz.fts.utils.StringUtils;
import com.sz.fts.utils.sms.MD5;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import static com.sz.fts.utils.SerectUtil.checkTX;

/**
 * @author 征华兴
 * @date 下午 3:46  2019/1/16 0016
 * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
 */
@Service
public class WxPayServiceImpl implements WxPayService {


    private static final Logger logger = LogManager.getLogger(WxPayServiceImpl.class);
    private static final String ReturnURL = "http://132.232.3.6:9001/szdxnw/wxPay/testNotify.do";
    private static final String ReturnURLCOMMON = "http://132.232.3.6:9001/szdxnw/wxPay/commonNotify.do";
    private static final String ReturnURLThr = "http://132.232.3.6:9001/szdxnw/wxPay/testNotifyThr.do";
    private static final String ReturnURLFour = "http://132.232.3.6:9001/szdxnw/wxPay/testNotifyFour.do";
    private static final String ReturnURLQuanyi = "http://132.232.3.6:9001/szdxnw/wxPay/testNotifyQuanyi.do";
    private static final String ReturnURLWap = "http://132.232.3.6:9001/szdxnw/wxPay/wapByNotify.do";
    private static final String SpId = "1033";  // SP身份标识
    private static final String wsdlUrl = "http://221.228.43.126:9001/webservice/services/PaymentManageService?wsdl";
    private static final String transationType = "Pay";
    private static final String payInterfaceType = "wxpay";  // 微信支付
    private static final String orderType = "12";   //消费
    private static final String ReturnURL_THIRDPART = "http://132.232.3.6:9001/szdxnw/wxPay/thirdPart.do";

    @Autowired
    private RedisAction redisAction;
    @Autowired
    private WxGoodsOrderMapper wxGoodsOrderMapper;
    @Autowired
    private WxCallbackOrderMapper wxCallbackOrderMapper;

    @Autowired
    private YYOrderKSMapperTwo yyOrderKSMapper;

    @Autowired
    private YuYueLHService yuYueLHService;

    @Override
    public JSONObject testPay(JSONObject input) {
        JSONObject map = new JSONObject();
        try {
            // 通过订单号 获取用户订单信息
            String TimeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String TransactionID = TimeStamp + getRandom();
            String orderSeq = SpId + TransactionID;
                StringBuffer inParameter = new StringBuffer();
                String riskControlInfo = "测试活动";
                // 价格
                String yuan = input.getString("fee");
                BigDecimal v1 = new BigDecimal(yuan);
                BigDecimal v2 = new BigDecimal("100");
                int fee1 = v1.multiply(v2).intValue();
                inParameter.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
                inParameter.append("<Payment>");
                inParameter.append("<TransactionID>" + TransactionID + "</TransactionID>");
                inParameter.append("<TransactionType>" + transationType + "</TransactionType>");
                inParameter.append("<TimeStamp>" + TimeStamp + "</TimeStamp>");
                inParameter.append("<PayInterfaceType>" + payInterfaceType + "</PayInterfaceType>");//
                inParameter.append("<SPID>" + SpId + "</SPID>");
                inParameter.append("<OrderType>" + orderType + "</OrderType>"); //12:
                inParameter.append("<CurrencyType>01</CurrencyType>");//01: 人民币
                inParameter.append("<Fee>" + fee1 + "</Fee>"); //  金额 分
                inParameter.append("<ReturnURL>" + ReturnURLWap + "</ReturnURL>"); //  后台接收返回地址
                inParameter.append("<ClientIP>58.211.5.59</ClientIP>");
                inParameter.append("<OrderSeq>" + orderSeq + "</OrderSeq>");
                inParameter.append("<tradeType>JSAPI</tradeType>");//
                inParameter.append("<openid>" + input.getString("openid") + "</openid>");//
                inParameter.append("<appidTag>1</appidTag>");
                inParameter.append("<PayGoodsType>" + riskControlInfo + "</PayGoodsType>");
                inParameter.append("</Payment>");
                logger.info("JS_UNI_PAY入参:[" + inParameter + "]");
                PaymentManageServiceServiceLocator locator = new PaymentManageServiceServiceLocator();
                locator.setPaymentManageServiceEndpointAddress(wsdlUrl);
                PaymentManageServiceSoapBindingStub binding = null;
                binding = (PaymentManageServiceSoapBindingStub) locator.getPaymentManageService();
                binding.setTimeout(60000);
                String xx = binding.doKeyPayment(inParameter.toString());
                logger.info("JS_UNI_PAY回参:[" + xx + "]");
                WxGoodsOrder order = new WxGoodsOrder();
                order.setTransactionId(TransactionID);
                order.setOrderSeq(orderSeq);
                order.setFee(fee1 + "");
                order.setOpenid(input.getString("openid"));
                order.setGoodsName(riskControlInfo);
                order.setCreateTime(DateUtils.dateFormat.format(new Date()));
                order.setStatus(1);
                String[] strs = xx.split("\\|");
                if ("E".equals(strs[2])) {
                    JSONObject resjson = JSONObject.fromObject(strs[3]);
                    String resultMsg = resjson.getString("resultMsg");
                    String resultCode = resjson.getString("resultCode");
                    // 如果OK 则调用
                    if ("OK".equals(resultMsg) && "SUCCESS".equals(resultCode)) {
                        order.setExtend1("true");
                        String appId = resjson.getString("appId");
                        String timeStamp = resjson.getString("timeStamp");
                        String signType = resjson.getString("signType");
                        String packageStr = resjson.getString("package");
                        String prepay_id = resjson.getString("prepay_id");
                        String nonceStr = resjson.getString("nonceStr");
                        String sign = resjson.getString("sign");  // 签名
                        map.put("flag", "0");
                        map.put("msg", "成功");
                        map.put("appId", appId);
                        map.put("timeStamp", timeStamp);
                        map.put("nonceStr", nonceStr);
                        map.put("package", packageStr);  //  prepay_id=123456789
                        map.put("signType", signType);   // MD5
                        map.put("paySign", sign);
                        map.put("prepay_id", prepay_id);
                    } else {
                        order.setExtend1("false");
                        map.put("flag", "1");
                        map.put("msg", resultMsg);
                    }
                    wxGoodsOrderMapper.insertSelective(order);
                } else {
                    map.put("flag", "4");
                    map.put("msg", "类型错误");
                }

            return map;
        } catch (Exception ex) {
            map.put("flag", "4");
            map.put("msg", "活动火爆");
            return map;
        }
    }

    @Override
    public String thirdPart(HttpServletRequest request) {
        String inputLine;
        String notifyXml = "";
        WxCallbackOrder order;
        try {
            while ((inputLine = request.getReader().readLine()) != null) {
                notifyXml += inputLine;
            }
            request.getReader().close();
            order = getWxCallbackOrder(notifyXml);
            wxCallbackOrderMapper.insertSelective(order);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"result\":\"4\",\"resultMsg\":\"fail\"}";
        }
        WxGoodsOrder wxGoodsOrder = wxGoodsOrderMapper.selectByOrderSeq(order.getOrderSeq());
        // 发送通知
        String returnUrl = wxGoodsOrder.getExtend4();
        JSONObject json = new JSONObject();
        json.put("fee", order.getFee());
        json.put("flag", "0");
        json.put("msg", "成功");
        json.put("orderSeq", order.getOrderSeq());
        json.put("payTime", order.getPayTime());
        json.put("timestamp",System.currentTimeMillis()/1000L);
        json.put("uptranSeq", order.getUptranSeq());
        String value = "fee="+json.getString("fee")+
                "&flag="+json.getString("flag")+
                "&msg="+json.getString("msg")+
                "&orderSeq="+json.getString("orderSeq")+
                "&payTime="+json.getString("payTime")+
                "&timestamp="+json.getString("timestamp")+
                "&uptranSeq="+json.getString("uptranSeq")+
                "&key=RGSlkg012_KGFqwe_a";
        String sign = MD5.GetMD5Code(value);
        json.put("sign",sign);
        String result = null;
        try {
            result = HttpUtil.doPost(returnUrl, json.toString());
            System.out.println("==用户返回结果===" + result);
            JSONObject jsonObject = JSONObject.fromObject(result);
            if (jsonObject.get("result") != null && jsonObject.get("resultMsg") != null) {
                String backResult = jsonObject.getString("result");
                if ("0".equals(backResult)) {
                    wxGoodsOrder.setSource(2);
                    // 更新时间
                    wxGoodsOrder.setExtend3(DateUtils.dateFormat.format(new Date()));
                    this.wxGoodsOrderMapper.updateByPrimaryKey(wxGoodsOrder);
                }else{
                    return "{\"result\":\"4\",\"resultMsg\":\"fail\"}";
                }
            }else{
                return "{\"result\":\"4\",\"resultMsg\":\"fail\"}";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"result\":\"4\",\"resultMsg\":\"fail\"}";
        }
        return "{\"result\":\"0\",\"resultMsg\":\"ok\"}";
    }

    @Override
    public JSONObject prepayOrderjsapi(JSONObject json, HttpServletRequest request) {
        JSONObject map = new JSONObject();
        try {
            JSONObject jsonInput = checkParam(json, "money","openid","orderSeq","sign","timestamp","user");
            if (jsonInput != null) {
                return jsonInput;
            }
            if (!checkTX(json)) {
                return out("9", "签名错误。");
            }
            String TimeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            // 订单号
            String TransactionID = TimeStamp + getRandom();
//            String orderSeq = "1033" + TransactionID;
            String orderSeq = json.getString("orderSeq");
            WxGoodsOrder existOrder = this.wxGoodsOrderMapper.selectByOrderSeq(orderSeq);
            if (existOrder != null) {
                return out("9", "已有相同订单号");
            }
            StringBuffer inParameter = new StringBuffer();

            String riskControlInfo = "第三方业务";
            double money = json.getDouble("money");
            if (money < 0.01) {
                return out("9", "金额异常");
            }
            BigDecimal v1 = new BigDecimal(money);
            BigDecimal v2 = new BigDecimal("100");
            int fee1 = v1.multiply(v2).intValue();
            String clientIp = IpFindConstant.getIpAddress(request);
            inParameter.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            inParameter.append("<Payment>");
            inParameter.append("<TransactionID>" + TransactionID + "</TransactionID>");
            inParameter.append("<TransactionType>" + transationType + "</TransactionType>");
            inParameter.append("<TimeStamp>" + TimeStamp + "</TimeStamp>");
            inParameter.append("<PayInterfaceType>" + payInterfaceType + "</PayInterfaceType>");//
            inParameter.append("<SPID>" + SpId + "</SPID>");
            inParameter.append("<OrderType>" + orderType + "</OrderType>"); //12:
            inParameter.append("<CurrencyType>01</CurrencyType>");//01: 人民币
            inParameter.append("<Fee>" + fee1 + "</Fee>"); //  金额 分
            inParameter.append("<ReturnURL>" + ReturnURL_THIRDPART + "</ReturnURL>"); //  后台接收返回地址
            inParameter.append("<ClientIP>" + clientIp + "</ClientIP>");
            inParameter.append("<OrderSeq>" + orderSeq + "</OrderSeq>");
            inParameter.append("<tradeType>JSAPI</tradeType>");//
            inParameter.append("<openid>" + json.getString("openid") + "</openid>");//
            inParameter.append("<appidTag>2</appidTag>");
            inParameter.append("<PayGoodsType>" + riskControlInfo + "</PayGoodsType>");
            inParameter.append("</Payment>");
            logger.info("JS_UNI_PAY入参:[" + inParameter + "]");
            PaymentManageServiceServiceLocator locator = new PaymentManageServiceServiceLocator();
            locator.setPaymentManageServiceEndpointAddress(wsdlUrl);
            PaymentManageServiceSoapBindingStub binding = null;
            binding = (PaymentManageServiceSoapBindingStub) locator.getPaymentManageService();
            binding.setTimeout(60000);
            String xx = binding.doKeyPayment(inParameter.toString());
            logger.info("JS_UNI_PAY回参:[" + xx + "]");
            WxGoodsOrder order = new WxGoodsOrder();
            order.setOrderSeq(orderSeq);
            order.setTransactionId(clientIp);
            order.setFee(fee1 + "");
            order.setOpenid(json.getString("openid"));
            order.setGoodsName(riskControlInfo);
            order.setCreateTime(DateUtils.dateFormat.format(new Date()));
            order.setStatus(1);
            if (json.has("user")) {
                order.setGoodsMobile(json.getString("user"));
            }
            // 未更新
            order.setSource(1);
            if (json.has("returnUrl")) {
                order.setExtend4(json.getString("returnUrl"));
            }
            String[] strs = xx.split("\\|");
            if ("E".equals(strs[2])) {
                JSONObject resjson = JSONObject.fromObject(strs[3]);
                String resultMsg = resjson.getString("resultMsg");
                String resultCode = resjson.getString("resultCode");
                // 如果OK 则调用
                if ("OK".equals(resultMsg) && "SUCCESS".equals(resultCode)) {
                    order.setExtend1("true");
                    String appId = resjson.getString("appId");
                    String timeStamp = resjson.getString("timeStamp");
                    String signType = resjson.getString("signType");
                    String packageStr = resjson.getString("package");
                    String prepay_id = resjson.getString("prepay_id");
                    String nonceStr = resjson.getString("nonceStr");
                    String paySign = resjson.getString("sign");  // 签名错误
                    map.put("flag", "0");
                    map.put("msg", "成功");
                    map.put("appId", appId);
                    map.put("timestamp",timeStamp);
                    map.put("nonceStr", nonceStr);
                    map.put("package", packageStr);  //  prepay_id=123456789
                    map.put("signType", signType);
                    map.put("paySign", paySign);
                    String value = "appId="+appId+
                            "&flag="+map.getString("flag")+
                            "&msg="+map.getString("msg")+
                            "&nonceStr="+map.getString("nonceStr")+
                            "&package="+map.getString("package")+
                            "&paySign="+map.getString("paySign")+
                            "&signType="+map.getString("signType")+
                            "&timestamp="+map.getString("timestamp")+
                            "&key=RGSlkg012_KGFqwe_a";
                    System.out.println(value);
                    String sign = MD5.GetMD5Code(value);
                    map.put("sign",sign);
                } else {
                    order.setExtend1("false");
                    map.put("flag", "9");
                    map.put("msg", resultMsg);
                    String value = "flag="+map.getString("flag")+
                            "&msg="+map.getString("msg")+
                            "&key=RGSlkg012_KGFqwe_a";
                    String sign = MD5.GetMD5Code(value);
                    map.put("sign",sign);
                }
                wxGoodsOrderMapper.insertSelective(order);
            } else {
                map.put("flag", "9");
                map.put("msg", "类型错误");
                String value = "flag="+map.getString("flag")+
                        "&msg="+map.getString("msg")+
                        "&key=RGSlkg012_KGFqwe_a";
                String sign = MD5.GetMD5Code(value);
                map.put("sign",sign);
            }
            return map;
        } catch (Exception ex) {
            ex.printStackTrace();
            map.put("flag", "9");
            map.put("msg", "活动火爆");
            String value = "flag="+map.getString("flag")+
                    "&msg="+map.getString("msg")+
                    "&key=RGSlkg012_KGFqwe_a";
            String sign = MD5.GetMD5Code(value);
            map.put("sign",sign);
            return map;
        }

    }



    @Override
    public JSONObject preOrderOne(JSONObject json) {
        JSONObject map = new JSONObject();
        try {
            String TimeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            StringBuffer inParameter = new StringBuffer();
            String TransactionID = TimeStamp + getRandom();
            int fee = 1;
            if (json.has("money")) {
                String money = json.getString("money");
                BigDecimal v1 = new BigDecimal(money);
                BigDecimal v2 = new BigDecimal("100");
                fee = v1.multiply(v2).intValue();
            }
            String orderSeq = SpId + TransactionID; //SPID+YYYYMMDDHHMISS+6
            String riskControlInfo = "{\"service_identify\":\"" + "10000000" + "\",\"subject\":\"" + "测试的钢笔" +
                    "\",\"product_type\":\"" + "1" + "\",\"body\":\"" + "很好用的钢笔，英雄" + "\",\"goods_count\":\"" + "2" + "\",\"service_cardno\":\"" + "111" + "\"}";
            riskControlInfo = "苏州电信测试办卡充值0.01元";
            inParameter.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            inParameter.append("<Payment>");
            inParameter.append("<TransactionID>" + TransactionID + "</TransactionID>");
            inParameter.append("<TransactionType>" + transationType + "</TransactionType>");
            inParameter.append("<TimeStamp>" + TimeStamp + "</TimeStamp>");
            inParameter.append("<PayInterfaceType>" + payInterfaceType + "</PayInterfaceType>");//
            inParameter.append("<SPID>" + SpId + "</SPID>");
            inParameter.append("<OrderType>" + orderType + "</OrderType>"); //12:
            inParameter.append("<CurrencyType>01</CurrencyType>");//01: 人民币
            inParameter.append("<Fee>" + fee + "</Fee>"); //  金额 分
            inParameter.append("<ReturnURL>" + ReturnURLWap + "</ReturnURL>"); //  后台接收返回地址
            inParameter.append("<ClientIP>58.211.5.59</ClientIP>");
            inParameter.append("<OrderSeq>" + orderSeq + "</OrderSeq>");
            inParameter.append("<tradeType>NATIVE</tradeType>");//交易类型  取值如下： NATIVE,APP,WAP,JSAPI
            inParameter.append("<appidTag>1</appidTag>");
            //  inParameter.append("<PayGoodsType>" + riskControlInfo + "</PayGoodsType>");
            inParameter.append("</Payment>");
            logger.info("JS_UNI_PAY入参:[" + inParameter + "]");
            PaymentManageServiceServiceLocator locator = new PaymentManageServiceServiceLocator();
            locator.setPaymentManageServiceEndpointAddress(wsdlUrl);
            PaymentManageServiceSoapBindingStub binding = null;
            binding = (PaymentManageServiceSoapBindingStub) locator.getPaymentManageService();
            binding.setTimeout(60000);
            String result = binding.doKeyPayment(inParameter.toString());
            logger.info("JS_UNI_PAY回参:[" + result + "]");
            String[] strs = result.split("\\|");

            WxGoodsOrder order = new WxGoodsOrder();
            order.setTransactionId(TransactionID);
            order.setOrderSeq(orderSeq);
            order.setFee(fee + "");
            order.setGoodsName(riskControlInfo);
            order.setCreateTime(DateUtils.dateFormat.format(new Date()));
            order.setStatus(1);

            if ("E".equals(strs[2])) {
                JSONObject resjson = JSONObject.fromObject(strs[3]);
                String resultMsg = resjson.getString("resultMsg");
                String resultCode = resjson.getString("resultCode");
                // 如果OK 则调用
                if ("OK".equals(resultMsg) && "SUCCESS".equals(resultCode)) {
                    order.setExtend1("true");
                    String codeUrl = resjson.getString("codeUrl");
                    order.setExtend2(codeUrl);
                    map.put("flag", "0");
                    map.put("msg", "成功");
                    map.put("codeUrl", codeUrl);
                } else {
                    order.setExtend1("false");
                    map.put("flag", "1");
                    map.put("msg", resultMsg);
                }
                wxGoodsOrderMapper.insertSelective(order);
            } else {
                map.put("flag", "4");
                map.put("msg", "类型错误");
            }
            return map;
        } catch (Exception ex) {
            System.out.println(ex);
            return out("4", "稍后再试");
        }

    }

    @Override
    public JSONObject preOrderTwo(String json) {
        JSONObject map = new JSONObject();
        try {
            JSONObject input = JSONObject.fromObject(json);
            String TimeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            // 订单号
            String orderSeq = input.getString("orderSeq");
            // 通过订单号 获取用户订单信息
            YYOrderKS orderKS = this.yyOrderKSMapper.selectByJuBen(orderSeq);
            if (orderKS != null && "0".equals(orderKS.getExtend1())) {
                StringBuffer inParameter = new StringBuffer();
                String TransactionID = TimeStamp + getRandom();
                String riskControlInfo = "苏州电渠微信业务";
                if (input.get("goodsName") != null && !"".equals(input.getString("goodsName"))) {
                    riskControlInfo = input.getString("goodsName");
                }
                String yuan = orderKS.getExtend3();
                BigDecimal v1 = new BigDecimal(yuan);
                BigDecimal v2 = new BigDecimal("100");
                int fee1 = v1.multiply(v2).intValue();
                inParameter.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
                inParameter.append("<Payment>");
                inParameter.append("<TransactionID>" + TransactionID + "</TransactionID>");
                inParameter.append("<TransactionType>" + transationType + "</TransactionType>");
                inParameter.append("<TimeStamp>" + TimeStamp + "</TimeStamp>");
                inParameter.append("<PayInterfaceType>" + payInterfaceType + "</PayInterfaceType>");//
                inParameter.append("<SPID>" + SpId + "</SPID>");
                inParameter.append("<OrderType>" + orderType + "</OrderType>"); //12:
                inParameter.append("<CurrencyType>01</CurrencyType>");//01: 人民币
                inParameter.append("<Fee>" + fee1 + "</Fee>"); //  金额 分
                inParameter.append("<ReturnURL>" + ReturnURL + "</ReturnURL>"); //  后台接收返回地址
                inParameter.append("<ClientIP>58.211.5.59</ClientIP>");
                inParameter.append("<OrderSeq>" + orderSeq + "</OrderSeq>");
                inParameter.append("<tradeType>JSAPI</tradeType>");//
                inParameter.append("<openid>" + input.getString("openid") + "</openid>");//
                inParameter.append("<appidTag>1</appidTag>");
                inParameter.append("<PayGoodsType>" + riskControlInfo + "</PayGoodsType>");
                inParameter.append("</Payment>");
                logger.info("JS_UNI_PAY入参:[" + inParameter + "]");
                PaymentManageServiceServiceLocator locator = new PaymentManageServiceServiceLocator();
                locator.setPaymentManageServiceEndpointAddress(wsdlUrl);
                PaymentManageServiceSoapBindingStub binding = null;
                binding = (PaymentManageServiceSoapBindingStub) locator.getPaymentManageService();
                binding.setTimeout(60000);
                String xx = binding.doKeyPayment(inParameter.toString());
                logger.info("JS_UNI_PAY回参:[" + xx + "]");
                WxGoodsOrder order = new WxGoodsOrder();
                order.setTransactionId(TransactionID);
                order.setOrderSeq(orderSeq);
                order.setFee(fee1 + "");
                order.setOpenid(input.getString("openid"));
                order.setGoodsName(riskControlInfo);
                order.setCreateTime(DateUtils.dateFormat.format(new Date()));
                order.setStatus(1);
                String[] strs = xx.split("\\|");
                if ("E".equals(strs[2])) {
                    JSONObject resjson = JSONObject.fromObject(strs[3]);
                    String resultMsg = resjson.getString("resultMsg");
                    String resultCode = resjson.getString("resultCode");
                    // 如果OK 则调用
                    if ("OK".equals(resultMsg) && "SUCCESS".equals(resultCode)) {
                        order.setExtend1("true");
                        String appId = resjson.getString("appId");
                        String timeStamp = resjson.getString("timeStamp");
                        String signType = resjson.getString("signType");
                        String packageStr = resjson.getString("package");
                        String prepay_id = resjson.getString("prepay_id");
                        String nonceStr = resjson.getString("nonceStr");
                        String sign = resjson.getString("sign");  // 签名
                        map.put("flag", "0");
                        map.put("msg", "成功");
                        map.put("appId", appId);
                        map.put("timeStamp", timeStamp);
                        map.put("nonceStr", nonceStr);
                        map.put("package", packageStr);  //  prepay_id=123456789
                        map.put("signType", signType);   // MD5
                        map.put("paySign", sign);
                        map.put("prepay_id", prepay_id);
                    } else {
                        order.setExtend1("false");
                        map.put("flag", "1");
                        map.put("msg", resultMsg);
                    }
                    wxGoodsOrderMapper.insertSelective(order);
                } else {
                    map.put("flag", "4");
                    map.put("msg", "类型错误");
                }
            } else {
                return out("2", "订单异常");
            }
            return map;
        } catch (Exception ex) {
            map.put("flag", "4");
            map.put("msg", "活动火爆");
            return map;
        }
    }

    @Override
    public String notifyResult(HttpServletRequest request) {
        String inputLine;
        String notifyXml = "";
        try {
            while ((inputLine = request.getReader().readLine()) != null) {
                notifyXml += inputLine;
            }
            logger.info("请求参数：" + notifyXml);
            request.getReader().close();
            String ResponseValue = notifyXml.split("ResponseValue=")[1];
            UrlEncodeForPay encryptPay = new UrlEncodeForPay();
            String deResValue = encryptPay.deCodingForPay(ResponseValue,
                    "123456123456123456123456", "utf-8");
            logger.info("-----解密后---deResValue-----" + deResValue);
            String result = deResValue.substring(0, deResValue.lastIndexOf("$"));
            String[] values = result.split("\\$");
            String TransactionID = values[0];
            String ORDERSEQ = values[1];
            String UPTRANSEQ = values[2];
            String Fee = values[3];
            String SPID = values[4];
            String CMPDate = values[5];
            String TimeStamp = values[6];
            String PayTime = values[7];
            String RespCode = values[8];
            String RespDesc = "";
            String ExtData1 = "";
            String ExtData2 = "";
            if (values.length == 10) {
                RespDesc = values[9];
            } else if (values.length == 11) {
                RespDesc = values[9];
                ExtData1 = values[10];
            } else if (values.length == 12) {
                RespDesc = values[9];
                ExtData1 = values[10];
                ExtData2 = values[11];
            }
            WxCallbackOrder order = new WxCallbackOrder();
            order.setTransactionId(TransactionID);
            order.setOrderSeq(ORDERSEQ);
            order.setUptranSeq(UPTRANSEQ);
            order.setFee(Fee);
            order.setSpId(SPID);
            order.setCmpDate(CMPDate);
            order.setTimeStamp(TimeStamp);
            order.setPayTime(PayTime);
            order.setRespCode(RespCode);
            order.setRespDesc(RespDesc);
            order.setExtData1(ExtData1);
            order.setExtData2(ExtData2);
            order.setExtend1(DateUtils.dateFormat.format(new Date()));
            order.setExtend2("1");  // 1 工单未处理
            JSONObject out = new JSONObject();
            out.put("orderId", ORDERSEQ);
            if ("Y".equalsIgnoreCase(RespCode)) {
                out.put("status", "1");
            } else {
                out.put("status", "0");
            }
            JSONObject result1 = yuYueLHService.xiuOrder(out);
            logger.info("--------------------------更新状态-------------------" + result1);
            if ("0".equals(result1.getString("flag"))) {
                wxCallbackOrderMapper.insertSelective(order);
            } else if ("4".equals(result1.getString("flag"))) {
                return "{\"result\":\"4\",\"resultMsg\":\"fail\"}";
            }
        } catch (Exception e) {
            logger.info("xml获取失败：" + e);
            e.printStackTrace();
            return "{\"result\":\"4\",\"resultMsg\":\"fail\"}";
        }
        return "{\"result\":\"0\",\"resultMsg\":\"ok\"}";
    }

    @Override
    public JSONObject preOrderThr(String json) {
        JSONObject map = new JSONObject();
        try {
            JSONObject input = JSONObject.fromObject(json);
            String TimeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            // 订单号
            String orderSeq = input.getString("orderSeq");
            // 通过订单号 获取用户订单信息
            StringBuffer inParameter = new StringBuffer();
            String TransactionID = TimeStamp + getRandom();
            String riskControlInfo = "wifi家庭网";
//            if (input.get("goodsName") != null && !"".equals(input.getString("goodsName"))) {
//                riskControlInfo = input.getString("goodsName");
//            }
            int fee1 = 1;
            inParameter.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            inParameter.append("<Payment>");
            inParameter.append("<TransactionID>" + TransactionID + "</TransactionID>");
            inParameter.append("<TransactionType>" + transationType + "</TransactionType>");
            inParameter.append("<TimeStamp>" + TimeStamp + "</TimeStamp>");
            inParameter.append("<PayInterfaceType>" + payInterfaceType + "</PayInterfaceType>");//
            inParameter.append("<SPID>" + SpId + "</SPID>");
            inParameter.append("<OrderType>" + orderType + "</OrderType>"); //12:
            inParameter.append("<CurrencyType>01</CurrencyType>");//01: 人民币
            inParameter.append("<Fee>" + fee1 + "</Fee>"); //  金额 分
            inParameter.append("<ReturnURL>" + ReturnURLThr + "</ReturnURL>"); //  后台接收返回地址
            inParameter.append("<ClientIP>58.211.5.59</ClientIP>");
            inParameter.append("<OrderSeq>" + orderSeq + "</OrderSeq>");
            inParameter.append("<tradeType>JSAPI</tradeType>");//
            inParameter.append("<openid>" + input.getString("openid") + "</openid>");//
            inParameter.append("<appidTag>1</appidTag>");
            inParameter.append("<PayGoodsType>" + riskControlInfo + "</PayGoodsType>");
            inParameter.append("</Payment>");
            logger.info("JS_UNI_PAY入参:[" + inParameter + "]");
            PaymentManageServiceServiceLocator locator = new PaymentManageServiceServiceLocator();
            locator.setPaymentManageServiceEndpointAddress(wsdlUrl);
            PaymentManageServiceSoapBindingStub binding = null;
            binding = (PaymentManageServiceSoapBindingStub) locator.getPaymentManageService();
            binding.setTimeout(60000);
            String xx = binding.doKeyPayment(inParameter.toString());
            logger.info("JS_UNI_PAY回参:[" + xx + "]");
            WxGoodsOrder order = new WxGoodsOrder();
            order.setTransactionId(TransactionID);
            order.setOrderSeq(orderSeq);
            order.setFee(fee1 + "");
            order.setOpenid(input.getString("openid"));
            order.setGoodsName(riskControlInfo);
            order.setCreateTime(DateUtils.dateFormat.format(new Date()));
            order.setStatus(1);
            String[] strs = xx.split("\\|");
            if ("E".equals(strs[2])) {
                JSONObject resjson = JSONObject.fromObject(strs[3]);
                String resultMsg = resjson.getString("resultMsg");
                String resultCode = resjson.getString("resultCode");
                // 如果OK 则调用
                if ("OK".equals(resultMsg) && "SUCCESS".equals(resultCode)) {
                    order.setExtend1("true");
                    String appId = resjson.getString("appId");
                    String timeStamp = resjson.getString("timeStamp");
                    String signType = resjson.getString("signType");
                    String packageStr = resjson.getString("package");
                    String prepay_id = resjson.getString("prepay_id");
                    String nonceStr = resjson.getString("nonceStr");
                    String sign = resjson.getString("sign");  // 签名
                    map.put("flag", "0");
                    map.put("msg", "成功");
                    map.put("appId", appId);
                    map.put("timeStamp", timeStamp);
                    map.put("nonceStr", nonceStr);
                    map.put("package", packageStr);  //  prepay_id=123456789
                    map.put("signType", signType);   // MD5
                    map.put("paySign", sign);
                    map.put("prepay_id", prepay_id);
                } else {
                    order.setExtend1("false");
                    map.put("flag", "1");
                    map.put("msg", resultMsg);
                }
                wxGoodsOrderMapper.insertSelective(order);
            } else {
                map.put("flag", "4");
                map.put("msg", "类型错误");
            }
            return map;
        } catch (Exception ex) {
            map.put("flag", "4");
            map.put("msg", "活动火爆");
            return map;
        }
    }

    @Override
    public String notifyResultThr(HttpServletRequest request) {
        String inputLine;
        String notifyXml = "";
        try {
            while ((inputLine = request.getReader().readLine()) != null) {
                notifyXml += inputLine;
            }
            logger.info("请求参数：" + notifyXml);
            request.getReader().close();
            String ResponseValue = notifyXml.split("ResponseValue=")[1];
            UrlEncodeForPay encryptPay = new UrlEncodeForPay();
            String deResValue = encryptPay.deCodingForPay(ResponseValue,
                    "123456123456123456123456", "utf-8");
            logger.info("-----解密后---deResValue-----" + deResValue);
            String result = deResValue.substring(0, deResValue.lastIndexOf("$"));
            String[] values = result.split("\\$");
            String TransactionID = values[0];
            String ORDERSEQ = values[1];
            String UPTRANSEQ = values[2];
            String Fee = values[3];
            String SPID = values[4];
            String CMPDate = values[5];
            String TimeStamp = values[6];
            String PayTime = values[7];
            String RespCode = values[8];
            String RespDesc = "";
            String ExtData1 = "";
            String ExtData2 = "";
            if (values.length == 10) {
                RespDesc = values[9];
            } else if (values.length == 11) {
                RespDesc = values[9];
                ExtData1 = values[10];
            } else if (values.length == 12) {
                RespDesc = values[9];
                ExtData1 = values[10];
                ExtData2 = values[11];
            }
            WxCallbackOrder order = new WxCallbackOrder();
            order.setTransactionId(TransactionID);
            order.setOrderSeq(ORDERSEQ);
            order.setUptranSeq(UPTRANSEQ);
            order.setFee(Fee);
            order.setSpId(SPID);
            order.setCmpDate(CMPDate);
            order.setTimeStamp(TimeStamp);
            order.setPayTime(PayTime);
            order.setRespCode(RespCode);
            order.setRespDesc(RespDesc);
            order.setExtData1(ExtData1);
            order.setExtData2(ExtData2);
            order.setExtend1(DateUtils.dateFormat.format(new Date()));
            order.setExtend2("1");  // 1 工单未处理
            order.setExtend3("2");//WiFi家庭网
            JSONObject out = new JSONObject();
            out.put("orderNumber", ORDERSEQ);
            // 0 失败，1成功
            if ("Y".equalsIgnoreCase(RespCode)) {
                out.put("orderStatus", "1");
            } else {
                out.put("orderStatus", "0");
            }
            String url = "http://app1.118114sz.com/szfts/homeNetwork/completePay.do";
            String result1 = HttpUtil.postUrl(url, out.toString());
            JSONObject json2 = JSONObject.fromObject(result1);
            logger.info("--------------------------更新状态-------------------" + result1);
            if ("1".equals(json2.getString("flag"))) {
                wxCallbackOrderMapper.insertSelective(order);
            } else if ("0".equals(json2.getString("flag"))) {
                return "{\"result\":\"4\",\"resultMsg\":\"fail\"}";
            }
        } catch (Exception e) {
            logger.info("xml获取失败：" + e);
            e.printStackTrace();
            return "{\"result\":\"4\",\"resultMsg\":\"fail\"}";
        }
        return "{\"result\":\"0\",\"resultMsg\":\"ok\"}";
    }

    @Autowired
    private CActivityOrderMapper cActivityOrderMapper;

    @Override
    public JSONObject preOrderFour(JSONObject input) {
        JSONObject map = new JSONObject();
        try {
            String TimeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            // 订单号
            String orderSeq = input.getString("orderSeq");

            // 通过订单号 获取用户订单信息
            StringBuffer inParameter = new StringBuffer();
            String TransactionID = TimeStamp + getRandom();
            String riskControlInfo = "社区宽带";
            int fee1 = 0;
            CActivityOrder userOrder = this.cActivityOrderMapper.selectByPrimaryKey(orderSeq);
            if (null == userOrder) {
                return out("4", "该订单不存在");
            } else {
                if ("0".equals(userOrder.getOrderStatus())) {
                    String yuan = userOrder.getExtend1();
                    BigDecimal v1 = new BigDecimal(yuan);
                    BigDecimal v2 = new BigDecimal("100");
                    fee1 = v1.multiply(v2).intValue();
                } else {
                    return out("4", "订单已支付");
                }
            }
            inParameter.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            inParameter.append("<Payment>");
            inParameter.append("<TransactionID>" + TransactionID + "</TransactionID>");
            inParameter.append("<TransactionType>" + transationType + "</TransactionType>");
            inParameter.append("<TimeStamp>" + TimeStamp + "</TimeStamp>");
            inParameter.append("<PayInterfaceType>" + payInterfaceType + "</PayInterfaceType>");//
            inParameter.append("<SPID>" + SpId + "</SPID>");
            inParameter.append("<OrderType>" + orderType + "</OrderType>"); //12:
            inParameter.append("<CurrencyType>01</CurrencyType>");//01: 人民币
            inParameter.append("<Fee>" + fee1 + "</Fee>"); //  金额 分
            inParameter.append("<ReturnURL>" + ReturnURLFour + "</ReturnURL>"); //  后台接收返回地址
            inParameter.append("<ClientIP>58.211.5.59</ClientIP>");
            inParameter.append("<OrderSeq>" + orderSeq + "</OrderSeq>");
            inParameter.append("<tradeType>JSAPI</tradeType>");//
            inParameter.append("<openid>" + input.getString("openid") + "</openid>");//
            inParameter.append("<appidTag>1</appidTag>");
            inParameter.append("<PayGoodsType>" + riskControlInfo + "</PayGoodsType>");
            inParameter.append("</Payment>");
            logger.info("JS_UNI_PAY入参:[" + inParameter + "]");
            PaymentManageServiceServiceLocator locator = new PaymentManageServiceServiceLocator();
            locator.setPaymentManageServiceEndpointAddress(wsdlUrl);
            PaymentManageServiceSoapBindingStub binding = null;
            binding = (PaymentManageServiceSoapBindingStub) locator.getPaymentManageService();
            binding.setTimeout(60000);
            String xx = binding.doKeyPayment(inParameter.toString());
            logger.info("JS_UNI_PAY回参:[" + xx + "]");
            WxGoodsOrder order = new WxGoodsOrder();
            order.setTransactionId(TransactionID);
            order.setOrderSeq(orderSeq);
            order.setFee(fee1 + "");
            order.setOpenid(input.getString("openid"));
            order.setGoodsName(riskControlInfo);
            order.setCreateTime(DateUtils.dateFormat.format(new Date()));
            order.setStatus(1);
            String[] strs = xx.split("\\|");
            if ("E".equals(strs[2])) {
                JSONObject resjson = JSONObject.fromObject(strs[3]);
                String resultMsg = resjson.getString("resultMsg");
                String resultCode = resjson.getString("resultCode");
                // 如果OK 则调用
                if ("OK".equals(resultMsg) && "SUCCESS".equals(resultCode)) {
                    order.setExtend1("true");
                    String appId = resjson.getString("appId");
                    String timeStamp = resjson.getString("timeStamp");
                    String signType = resjson.getString("signType");
                    String packageStr = resjson.getString("package");
                    String prepay_id = resjson.getString("prepay_id");
                    String nonceStr = resjson.getString("nonceStr");
                    String sign = resjson.getString("sign");  // 签名
                    map.put("flag", "0");
                    map.put("msg", "成功");
                    map.put("appId", appId);
                    map.put("timeStamp", timeStamp);
                    map.put("nonceStr", nonceStr);
                    map.put("package", packageStr);  //  prepay_id=123456789
                    map.put("signType", signType);   // MD5
                    map.put("paySign", sign);
                    map.put("prepay_id", prepay_id);
                } else {
                    order.setExtend1("false");
                    map.put("flag", "1");
                    map.put("msg", resultMsg);
                }
                wxGoodsOrderMapper.insertSelective(order);
            } else {
                map.put("flag", "4");
                map.put("msg", "类型错误");
            }
            return map;
        } catch (Exception ex) {
            map.put("flag", "4");
            map.put("msg", "活动火爆");
            return map;
        }
    }

    @Override
    public String notifyResultFour(HttpServletRequest request) {
        String inputLine;
        String notifyXml = "";
        try {
            while ((inputLine = request.getReader().readLine()) != null) {
                notifyXml += inputLine;
            }
            logger.info("请求参数：" + notifyXml);
            request.getReader().close();
            String ResponseValue = notifyXml.split("ResponseValue=")[1];
            UrlEncodeForPay encryptPay = new UrlEncodeForPay();
            String deResValue = encryptPay.deCodingForPay(ResponseValue,
                    "123456123456123456123456", "utf-8");
            logger.info("-----解密后---deResValue-----" + deResValue);
            String result = deResValue.substring(0, deResValue.lastIndexOf("$"));
            String[] values = result.split("\\$");
            String TransactionID = values[0];
            String ORDERSEQ = values[1];
            String UPTRANSEQ = values[2];
            String Fee = values[3];
            String SPID = values[4];
            String CMPDate = values[5];
            String TimeStamp = values[6];
            String PayTime = values[7];
            String RespCode = values[8];
            String RespDesc = "";
            String ExtData1 = "";
            String ExtData2 = "";
            if (values.length == 10) {
                RespDesc = values[9];
            } else if (values.length == 11) {
                RespDesc = values[9];
                ExtData1 = values[10];
            } else if (values.length == 12) {
                RespDesc = values[9];
                ExtData1 = values[10];
                ExtData2 = values[11];
            }
            WxCallbackOrder order = new WxCallbackOrder();
            order.setTransactionId(TransactionID);
            order.setOrderSeq(ORDERSEQ);
            order.setUptranSeq(UPTRANSEQ);
            order.setFee(Fee);
            order.setSpId(SPID);
            order.setCmpDate(CMPDate);
            order.setTimeStamp(TimeStamp);
            order.setPayTime(PayTime);
            order.setRespCode(RespCode);
            order.setRespDesc(RespDesc);
            order.setExtData1(ExtData1);
            order.setExtData2(ExtData2);
            order.setExtend1(DateUtils.dateFormat.format(new Date()));
            order.setExtend2("1");  // 1 工单未处理
            order.setExtend3("3");//社区宽带
            // 0 失败，1成功
            String flag = "3";
            if ("Y".equalsIgnoreCase(RespCode)) {
                flag = "2";
            }
            wxCallbackOrderMapper.insertSelective(order);
            String url = "http://app1.118114sz.com/szfts/rollCharge/completePay.do?payFlag=" + flag + "&orderNumber=" + ORDERSEQ;
            String result1 = HttpUtil.sendUrl(url);
//            JSONObject json2 = JSONObject.fromObject(result1);
            logger.info("--------------------------更新状态-------------------" + result1);
//            if ("1".equals(json2.getString("flag"))) {

//            } else {
//                return "{\"result\":\"4\",\"resultMsg\":\"fail\"}";
//            }
        } catch (Exception e) {
            logger.info("xml获取失败：" + e);
            e.printStackTrace();
            return "{\"result\":\"4\",\"resultMsg\":\"fail\"}";
        }
        return "{\"result\":\"0\",\"resultMsg\":\"ok\"}";
    }

    @Override
    public JSONObject preOrderWap(JSONObject json) {
        JSONObject map = new JSONObject();
        try {
            String TimeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String TransactionID = TimeStamp + getRandom();
            String orderSeq = SpId + TransactionID;
            StringBuffer inParameter = new StringBuffer();
            String riskControlInfo = "wap支付";
            int fee1 = 1;
            if (json.has("money")) {
                double money = json.getDouble("money");
                if (money < 0.01) {
                    return out("4", "金额有误");
                }
                BigDecimal v1 = new BigDecimal(money);
                BigDecimal v2 = new BigDecimal("100");
                fee1 = v1.multiply(v2).intValue();
            }
            inParameter.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            inParameter.append("<Payment>");
            inParameter.append("<TransactionID>" + TransactionID + "</TransactionID>");
            inParameter.append("<TransactionType>" + transationType + "</TransactionType>");
            inParameter.append("<TimeStamp>" + TimeStamp + "</TimeStamp>");
            inParameter.append("<PayInterfaceType>" + payInterfaceType + "</PayInterfaceType>");//
            inParameter.append("<SPID>" + SpId + "</SPID>");
            inParameter.append("<OrderType>" + orderType + "</OrderType>"); //12:
            inParameter.append("<CurrencyType>01</CurrencyType>");//01: 人民币
            inParameter.append("<Fee>" + fee1 + "</Fee>"); //  金额 分
            inParameter.append("<ReturnURL>" + ReturnURLWap + "</ReturnURL>"); //  后台接收返回地址
            inParameter.append("<ClientIP>58.211.5.59</ClientIP>");
            inParameter.append("<OrderSeq>" + orderSeq + "</OrderSeq>");
            inParameter.append("<tradeType>WAP</tradeType>");//
            inParameter.append("<appidTag>1</appidTag>");
            inParameter.append("<PayGoodsType>" + riskControlInfo + "</PayGoodsType>");
            inParameter.append("</Payment>");
            logger.info("JS_UNI_PAY入参:[" + inParameter + "]");
            PaymentManageServiceServiceLocator locator = new PaymentManageServiceServiceLocator();
            locator.setPaymentManageServiceEndpointAddress(wsdlUrl);
            PaymentManageServiceSoapBindingStub binding = null;
            binding = (PaymentManageServiceSoapBindingStub) locator.getPaymentManageService();
            binding.setTimeout(60000);
            String xx = binding.doKeyPayment(inParameter.toString());
            logger.info("wap回参:[" + xx + "]");
            WxGoodsOrder order = new WxGoodsOrder();
            order.setTransactionId(TransactionID);
            order.setOrderSeq(orderSeq);
            order.setFee(fee1 + "");
            order.setGoodsName(riskControlInfo);
            order.setCreateTime(DateUtils.dateFormat.format(new Date()));
            order.setStatus(1);
            String[] strs = xx.split("\\|");
            if ("E".equals(strs[2])) {
                JSONObject resjson = JSONObject.fromObject(strs[3]);
                String resultMsg = resjson.getString("resultMsg");
                String resultCode = resjson.getString("resultCode");
                // 如果OK 则调用
                if ("OK".equals(resultMsg) && "SUCCESS".equals(resultCode)) {
                    order.setExtend1("true");
                    String appId = resjson.getString("appid");
                    String timeStamp = resjson.getString("timestamp");
                    // String signType = resjson.getString("signType");
                    String packageStr = resjson.getString("package");
                    String prepay_id = resjson.getString("prepay_id");
                    String nonceStr = resjson.getString("noncestr");
                    String sign = resjson.getString("sign");  // 签名
                    map.put("flag", "0");
                    map.put("msg", "成功");
                    map.put("appId", appId);
                    map.put("timeStamp", timeStamp);
                    map.put("nonceStr", nonceStr);
                    map.put("package", packageStr);  //  WAP
                    map.put("signType", "MD5");   // MD5
                    map.put("paySign", sign);
                    map.put("prepay_id", prepay_id);
                } else {
                    order.setExtend1("false");
                    map.put("flag", "1");
                    map.put("msg", resultMsg);
                }
                wxGoodsOrderMapper.insertSelective(order);
            } else {
                map.put("flag", "4");
                map.put("msg", "类型错误");
            }
            return map;
        } catch (Exception ex) {
            ex.printStackTrace();
            map.put("flag", "4");
            map.put("msg", "活动火爆");
            return map;
        }
    }

    @Autowired
    private AActLogMapper aActLogMapper;

    @Override
    public JSONObject preOrderQuanyi(JSONObject input) {
        JSONObject map = new JSONObject();
        try {
            // 订单号
            String orderSeq = input.getString("orderSeq");
            // 通过订单号 获取用户订单信息
            AActLog log = this.aActLogMapper.selectByOrderSeq(orderSeq);
            if (log != null && 1 == log.getExtend2()) {
                if (!input.getString("openid").equals(log.getOpenId())) {
                    return out("4", "参数异常");
                }
                String TimeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                StringBuffer inParameter = new StringBuffer();
                String TransactionID = TimeStamp + getRandom();
                String riskControlInfo = "权益专区充值活动";
                if ("22".equals(log.getActType())) {
                    riskControlInfo = "59元回城卡";
                }
                // 价格
                String yuan = log.getExtend3();
                BigDecimal v1 = new BigDecimal(yuan);
                BigDecimal v2 = new BigDecimal("100");
                int fee1 = v1.multiply(v2).intValue();
                inParameter.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
                inParameter.append("<Payment>");
                inParameter.append("<TransactionID>" + TransactionID + "</TransactionID>");
                inParameter.append("<TransactionType>" + transationType + "</TransactionType>");
                inParameter.append("<TimeStamp>" + TimeStamp + "</TimeStamp>");
                inParameter.append("<PayInterfaceType>" + payInterfaceType + "</PayInterfaceType>");//
                inParameter.append("<SPID>" + SpId + "</SPID>");
                inParameter.append("<OrderType>" + orderType + "</OrderType>"); //12:
                inParameter.append("<CurrencyType>01</CurrencyType>");//01: 人民币
                inParameter.append("<Fee>" + fee1 + "</Fee>"); //  金额 分
                inParameter.append("<ReturnURL>" + ReturnURLQuanyi + "</ReturnURL>"); //  后台接收返回地址
                inParameter.append("<ClientIP>58.211.5.59</ClientIP>");
                inParameter.append("<OrderSeq>" + orderSeq + "</OrderSeq>");
                inParameter.append("<tradeType>JSAPI</tradeType>");//
                inParameter.append("<openid>" + input.getString("openid") + "</openid>");//
                inParameter.append("<appidTag>1</appidTag>");
                inParameter.append("<PayGoodsType>" + riskControlInfo + "</PayGoodsType>");
                inParameter.append("</Payment>");
                logger.info("JS_UNI_PAY入参:[" + inParameter + "]");
                PaymentManageServiceServiceLocator locator = new PaymentManageServiceServiceLocator();
                locator.setPaymentManageServiceEndpointAddress(wsdlUrl);
                PaymentManageServiceSoapBindingStub binding = null;
                binding = (PaymentManageServiceSoapBindingStub) locator.getPaymentManageService();
                binding.setTimeout(60000);
                String xx = binding.doKeyPayment(inParameter.toString());
                logger.info("JS_UNI_PAY回参:[" + xx + "]");
                WxGoodsOrder order = new WxGoodsOrder();
                order.setTransactionId(TransactionID);
                order.setOrderSeq(orderSeq);
                order.setFee(fee1 + "");
                order.setOpenid(input.getString("openid"));
                order.setGoodsName(riskControlInfo);
                order.setCreateTime(DateUtils.dateFormat.format(new Date()));
                order.setStatus(1);
                String[] strs = xx.split("\\|");
                if ("E".equals(strs[2])) {
                    JSONObject resjson = JSONObject.fromObject(strs[3]);
                    String resultMsg = resjson.getString("resultMsg");
                    String resultCode = resjson.getString("resultCode");
                    // 如果OK 则调用
                    if ("OK".equals(resultMsg) && "SUCCESS".equals(resultCode)) {
                        order.setExtend1("true");
                        String appId = resjson.getString("appId");
                        String timeStamp = resjson.getString("timeStamp");
                        String signType = resjson.getString("signType");
                        String packageStr = resjson.getString("package");
                        String prepay_id = resjson.getString("prepay_id");
                        String nonceStr = resjson.getString("nonceStr");
                        String sign = resjson.getString("sign");  // 签名
                        map.put("flag", "0");
                        map.put("msg", "成功");
                        map.put("appId", appId);
                        map.put("timeStamp", timeStamp);
                        map.put("nonceStr", nonceStr);
                        map.put("package", packageStr);  //  prepay_id=123456789
                        map.put("signType", signType);   // MD5
                        map.put("paySign", sign);
                        map.put("prepay_id", prepay_id);
                    } else {
                        order.setExtend1("false");
                        map.put("flag", "1");
                        map.put("msg", resultMsg);
                    }
                    wxGoodsOrderMapper.insertSelective(order);
                } else {
                    map.put("flag", "4");
                    map.put("msg", "类型错误");
                }
            } else {
                return out("2", "订单异常");
            }
            return map;
        } catch (Exception ex) {
            map.put("flag", "4");
            map.put("msg", "活动火爆");
            return map;
        }
    }

    @Override
    public String notifyResultQuanyi(HttpServletRequest request) {
        String inputLine;
        String notifyXml = "";
        try {
            while ((inputLine = request.getReader().readLine()) != null) {
                notifyXml += inputLine;
            }
            logger.info("请求参数：" + notifyXml);
            request.getReader().close();
            String ResponseValue = notifyXml.split("ResponseValue=")[1];
            UrlEncodeForPay encryptPay = new UrlEncodeForPay();
            String deResValue = encryptPay.deCodingForPay(ResponseValue,
                    "123456123456123456123456", "utf-8");
            logger.info("-----解密后---deResValue-----" + deResValue);
            String result = deResValue.substring(0, deResValue.lastIndexOf("$"));
            String[] values = result.split("\\$");
            String TransactionID = values[0];
            String ORDERSEQ = values[1];
            String UPTRANSEQ = values[2];
            String Fee = values[3];
            String SPID = values[4];
            String CMPDate = values[5];
            String TimeStamp = values[6];
            String PayTime = values[7];
            String RespCode = values[8];
            String RespDesc = "";
            String ExtData1 = "";
            String ExtData2 = "";
            if (values.length == 10) {
                RespDesc = values[9];
            } else if (values.length == 11) {
                RespDesc = values[9];
                ExtData1 = values[10];
            } else if (values.length == 12) {
                RespDesc = values[9];
                ExtData1 = values[10];
                ExtData2 = values[11];
            }
            WxCallbackOrder order = new WxCallbackOrder();
            order.setTransactionId(TransactionID);
            order.setOrderSeq(ORDERSEQ);
            order.setUptranSeq(UPTRANSEQ);
            order.setFee(Fee);
            order.setSpId(SPID);
            order.setCmpDate(CMPDate);
            order.setTimeStamp(TimeStamp);
            order.setPayTime(PayTime);
            order.setRespCode(RespCode);
            order.setRespDesc(RespDesc);
            order.setExtData1(ExtData1);
            order.setExtData2(ExtData2);
            order.setExtend1(DateUtils.dateFormat.format(new Date()));
            order.setExtend2("1");  // 1 工单未处理
            order.setExtend3("4");// 权益专区充值
            // 0 失败，1成功
//            String flag = "3";
//            if ("Y".equalsIgnoreCase(RespCode)) {
//                flag = "2";
//            }
//            String url = "http://app1.118114sz.com/szfts/rollCharge/completePay.do?payFlag=" + flag + "&orderNumber=" + ORDERSEQ;
//            String result1 = HttpUtil.sendUrl(url);
//            JSONObject json2 = JSONObject.fromObject(result1);
//            logger.info("--------------------------更新状态-------------------" + result1);
            AActLog log = this.aActLogMapper.selectByOrderSeq(ORDERSEQ);
            if ("Y".equalsIgnoreCase(RespCode)) {
                // 成功
                log.setExtend2(2);
                //   log.setUserName("成功");
            } else {
                // 失败
                log.setExtend2(3);
                // log.setUserName("失败");
            }
            this.aActLogMapper.updateByPrimaryKeySelective(log);
            wxCallbackOrderMapper.insertSelective(order);
        } catch (Exception e) {
            logger.info("xml获取失败：" + e);
            e.printStackTrace();
            return "{\"result\":\"4\",\"resultMsg\":\"fail\"}";
        }
        return "{\"result\":\"0\",\"resultMsg\":\"ok\"}";
    }

    @Autowired
    YuyuePhoneMapper yuyuePhoneMapper;

    @Override
    public JSONObject preOrderJsapiCommon(JSONObject input) {
        JSONObject map = new JSONObject();
        try {

            String TimeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            // 订单号
            String orderSeq = input.getString("orderSeq");
            // 通过订单号 获取用户订单信息
            YYOrderKS orderKS = this.yyOrderKSMapper.selectByJuBen(orderSeq);
            if (orderKS != null && "0".equals(orderKS.getExtend1())) {
                StringBuffer inParameter = new StringBuffer();
                String TransactionID = TimeStamp + getRandom();
                String riskControlInfo = "苏州电渠微信业务";
                if (input.get("goodsName") != null && !"".equals(input.getString("goodsName"))) {
                    riskControlInfo = input.getString("goodsName");
                }
                String first = orderKS.getPhoneNumber();
                String second = orderKS.getYyNo();
                int source = orderKS.getSource();
                synchronized (first) {
                    if (1228 == source) {
                        MobileNumber firstNumber = mobileNumberMapper.getByMobileNumber(first, "zjf005");
                        if (firstNumber == null || firstNumber.getStatus() != 0) {
                            return out("2", "该号码已售出，请重新选择号码");
                        }
                        if (second.startsWith("1")) {
                            MobileNumber secondNumber = mobileNumberMapper.getByMobileNumber(second, "zjf015");
                            if (secondNumber == null || secondNumber.getStatus() != 0) {
                                return out("2", "该号码已售出，请重新选择号码");
                            }
                        }
                    } else if (1229 == source) {
                        YuyuePhone firstNumber = yuyuePhoneMapper.selectByMobile(first);
                        if (firstNumber == null || firstNumber.getStatus() == 1) {
                            return out("2", "该号码已售出，请重新选择号码");
                        }
                        if (second.startsWith("1")) {
                            YuyuePhone secondNumber = yuyuePhoneMapper.selectByMobile(second);
                            if (secondNumber == null || secondNumber.getStatus() == 1) {
                                return out("2", "该号码已售出，请重新选择号码");
                            }
                        }
                    }
                    String yuan = orderKS.getExtend3();
                    BigDecimal v1 = new BigDecimal(yuan);
                    BigDecimal v2 = new BigDecimal("100");
                    int fee1 = v1.multiply(v2).intValue();
                    inParameter.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
                    inParameter.append("<Payment>");
                    inParameter.append("<TransactionID>" + TransactionID + "</TransactionID>");
                    inParameter.append("<TransactionType>" + transationType + "</TransactionType>");
                    inParameter.append("<TimeStamp>" + TimeStamp + "</TimeStamp>");
                    inParameter.append("<PayInterfaceType>" + payInterfaceType + "</PayInterfaceType>");//
                    inParameter.append("<SPID>" + SpId + "</SPID>");
                    inParameter.append("<OrderType>" + orderType + "</OrderType>"); //12:
                    inParameter.append("<CurrencyType>01</CurrencyType>");//01: 人民币
                    inParameter.append("<Fee>" + fee1 + "</Fee>"); //  金额 分
                    inParameter.append("<ReturnURL>" + ReturnURLCOMMON + "</ReturnURL>"); //  后台接收返回地址
                    inParameter.append("<ClientIP>58.211.5.59</ClientIP>");
                    inParameter.append("<OrderSeq>" + orderSeq + "</OrderSeq>");
                    inParameter.append("<tradeType>JSAPI</tradeType>");//
                    inParameter.append("<openid>" + input.getString("openid") + "</openid>");//
                    inParameter.append("<appidTag>1</appidTag>");
                    inParameter.append("<PayGoodsType>" + riskControlInfo + "</PayGoodsType>");
                    inParameter.append("</Payment>");
                    logger.info("JS_UNI_PAY入参:[" + inParameter + "]");
                    PaymentManageServiceServiceLocator locator = new PaymentManageServiceServiceLocator();
                    locator.setPaymentManageServiceEndpointAddress(wsdlUrl);
                    PaymentManageServiceSoapBindingStub binding = null;
                    binding = (PaymentManageServiceSoapBindingStub) locator.getPaymentManageService();
                    binding.setTimeout(60000);
                    String xx = binding.doKeyPayment(inParameter.toString());
                    logger.info("JS_UNI_PAY回参:[" + xx + "]");
                    WxGoodsOrder order = new WxGoodsOrder();
                    order.setTransactionId(TransactionID);
                    order.setOrderSeq(orderSeq);
                    order.setFee(fee1 + "");
                    order.setOpenid(input.getString("openid"));
                    order.setGoodsName(riskControlInfo);
                    order.setCreateTime(DateUtils.dateFormat.format(new Date()));
                    order.setStatus(1);
                    String[] strs = xx.split("\\|");
                    if ("E".equals(strs[2])) {
                        JSONObject resjson = JSONObject.fromObject(strs[3]);
                        String resultMsg = resjson.getString("resultMsg");
                        String resultCode = resjson.getString("resultCode");
                        // 如果OK 则调用
                        if ("OK".equals(resultMsg) && "SUCCESS".equals(resultCode)) {
                            order.setExtend1("true");
                            String appId = resjson.getString("appId");
                            String timeStamp = resjson.getString("timeStamp");
                            String signType = resjson.getString("signType");
                            String packageStr = resjson.getString("package");
                            String prepay_id = resjson.getString("prepay_id");
                            String nonceStr = resjson.getString("nonceStr");
                            String sign = resjson.getString("sign");  // 签名
                            map.put("flag", "0");
                            map.put("msg", "成功");
                            map.put("appId", appId);
                            map.put("timeStamp", timeStamp);
                            map.put("nonceStr", nonceStr);
                            map.put("package", packageStr);  //  prepay_id=123456789
                            map.put("signType", signType);   // MD5
                            map.put("paySign", sign);
                            map.put("prepay_id", prepay_id);
                        } else {
                            order.setExtend1("false");
                            map.put("flag", "1");
                            map.put("msg", resultMsg);
                        }
                        wxGoodsOrderMapper.insertSelective(order);
                    } else {
                        map.put("flag", "4");
                        map.put("msg", "类型错误");
                    }
                }
            } else {
                return out("2", "订单异常");
            }
            return map;
        } catch (Exception ex) {
            map.put("flag", "4");
            map.put("msg", "活动火爆");
            return map;
        }
    }

    @Autowired
    private MobileNumberMapper mobileNumberMapper;

    @Override
    public String wapByNotify(HttpServletRequest request) {
        String inputLine;
        String notifyXml = "";
        try {

            while ((inputLine = request.getReader().readLine()) != null) {
                notifyXml += inputLine;
            }

            request.getReader().close();
            WxCallbackOrder order = getWxCallbackOrder(notifyXml);
            wxCallbackOrderMapper.insertSelective(order);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"result\":\"4\",\"resultMsg\":\"fail\"}";
        }
        return "{\"result\":\"0\",\"resultMsg\":\"ok\"}";
    }

    private WxCallbackOrder getWxCallbackOrder(String notifyXml) {
        logger.info("请求参数：" + notifyXml);
        String ResponseValue = notifyXml.split("ResponseValue=")[1];
        UrlEncodeForPay encryptPay = new UrlEncodeForPay();
        String deResValue = encryptPay.deCodingForPay(ResponseValue,
                "123456123456123456123456", "utf-8");
        logger.info("-----解密后--------" + deResValue);
        String result = deResValue.substring(0, deResValue.lastIndexOf("$"));
        String[] values = result.split("\\$");
        String TransactionID = values[0];
        String ORDERSEQ = values[1];
        String UPTRANSEQ = values[2];
        String Fee = values[3];
        String SPID = values[4];
        String CMPDate = values[5];
        String TimeStamp = values[6];
        String PayTime = values[7];
        String RespCode = values[8];
        String RespDesc = "";
        String ExtData1 = "";
        String ExtData2 = "";
        if (values.length == 10) {
            RespDesc = values[9];
        } else if (values.length == 11) {
            RespDesc = values[9];
            ExtData1 = values[10];
        } else if (values.length == 12) {
            RespDesc = values[9];
            ExtData1 = values[10];
            ExtData2 = values[11];
        }
        WxCallbackOrder order = new WxCallbackOrder();
        order.setTransactionId(TransactionID);
        order.setOrderSeq(ORDERSEQ);
        order.setUptranSeq(UPTRANSEQ);
        order.setFee(Fee);
        order.setSpId(SPID);
        order.setCmpDate(CMPDate);
        order.setTimeStamp(TimeStamp);
        order.setPayTime(PayTime);
        order.setRespCode(RespCode);
        order.setRespDesc(RespDesc);
        order.setExtData1(ExtData1);
        order.setExtData2(ExtData2);
        order.setExtend1(DateUtils.dateFormat.format(new Date()));
        order.setExtend2("1");  // 1 工单未处理
        return order;
    }

    @Override
    public String commonNotify(HttpServletRequest request) {
        String inputLine;
        String notifyXml = "";
        try {
            while ((inputLine = request.getReader().readLine()) != null) {
                notifyXml += inputLine;
            }
            logger.info("请求参数：" + notifyXml);
            request.getReader().close();
            String ResponseValue = notifyXml.split("ResponseValue=")[1];
            UrlEncodeForPay encryptPay = new UrlEncodeForPay();
            String deResValue = encryptPay.deCodingForPay(ResponseValue,
                    "123456123456123456123456", "utf-8");
            logger.info("-----解密后---deResValue-----" + deResValue);
            String result = deResValue.substring(0, deResValue.lastIndexOf("$"));
            String[] values = result.split("\\$");
            String TransactionID = values[0];
            String ORDERSEQ = values[1];
            String UPTRANSEQ = values[2];
            String Fee = values[3];
            String SPID = values[4];
            String CMPDate = values[5];
            String TimeStamp = values[6];
            String PayTime = values[7];
            String RespCode = values[8];
            String RespDesc = "";
            String ExtData1 = "";
            String ExtData2 = "";
            if (values.length == 10) {
                RespDesc = values[9];
            } else if (values.length == 11) {
                RespDesc = values[9];
                ExtData1 = values[10];
            } else if (values.length == 12) {
                RespDesc = values[9];
                ExtData1 = values[10];
                ExtData2 = values[11];
            }
            WxCallbackOrder order = new WxCallbackOrder();
            order.setTransactionId(TransactionID);
            order.setOrderSeq(ORDERSEQ);
            order.setUptranSeq(UPTRANSEQ);
            order.setFee(Fee);
            order.setSpId(SPID);
            order.setCmpDate(CMPDate);
            order.setTimeStamp(TimeStamp);
            order.setPayTime(PayTime);
            order.setRespCode(RespCode);
            order.setRespDesc(RespDesc);
            order.setExtData1(ExtData1);
            order.setExtData2(ExtData2);
            order.setExtend1(DateUtils.dateFormat.format(new Date()));
            order.setExtend2("1");  // 1 工单未处理
            YYOrderKS existOrder = yyOrderKSMapper.selectByJuBen(ORDERSEQ);
            String firstMobile = existOrder.getPhoneNumber();
            String secondPhone = existOrder.getYyNo();
            int source = existOrder.getSource();
            if (1228 == source) {
                MobileNumber first = mobileNumberMapper.getByMobileNumber(firstMobile, "zjf005");
                MobileNumber second = null;
                if (secondPhone.startsWith("1")) {
                    second = mobileNumberMapper.getByMobileNumber(secondPhone, "zjf015");
                }
                order.setExtend3("5");//5G
                if ("Y".equalsIgnoreCase(RespCode)) {
                    // 已支付
                    existOrder.setExtend1("1");
                    first.setStatus(1);
                    if (second != null) {
                        second.setStatus(1);
                    }
                } else {
                    // 回调支付失败
                    existOrder.setExtend1("2");
                    first.setStatus(0);
                    if (second != null) {
                        second.setStatus(0);
                    }
                }
                mobileNumberMapper.update(first);
                if (second != null) {
                    mobileNumberMapper.update(second);
                }
            } else if (1229 == source) {
                YuyuePhone first = yuyuePhoneMapper.selectByMobile(firstMobile);
                YuyuePhone second = null;
                if (secondPhone.startsWith("1")) {
                    second = yuyuePhoneMapper.selectByMobile(secondPhone);
                }
                order.setExtend3("6");//星卡
                if ("Y".equalsIgnoreCase(RespCode)) {
                    // 已支付
                    existOrder.setExtend1("1");
                    if (first != null) {
                        first.setStatus(1);
                    }
                    if (second != null) {
                        second.setStatus(1);
                    }
                } else {
                    // 回调支付失败
                    existOrder.setExtend1("2");
                    if (first != null) {
                        first.setStatus(0);
                    }
                    if (second != null) {
                        second.setStatus(0);
                    }
                }
                if (first != null) {
                    yuyuePhoneMapper.updateByPrimaryKey(first);
                }
                if (second != null) {
                    yuyuePhoneMapper.updateByPrimaryKey(second);
                }
            }
            yyOrderKSMapper.updateByPrimaryKeySelective(existOrder);
            wxCallbackOrderMapper.insertSelective(order);
        } catch (Exception e) {
            logger.info("xml获取失败：" + e);
            e.printStackTrace();
            return "{\"result\":\"4\",\"resultMsg\":\"fail\"}";
        }
        return "{\"result\":\"0\",\"resultMsg\":\"ok\"}";
    }


    private JSONObject out(String flag, String msg) {
        JSONObject out = new JSONObject();
        out.put("flag", flag);
        out.put("msg", msg);
        return out;
    }

    public static void main(String[] args) {
        String order = "103320190404100312500040";
        String result = order.substring(18, 19);
        System.out.println(result);
    }

    private String getRandom() {
        Random random = new Random();
        String result = "";
        for (int i = 0; i < 6; i++) {
            result += random.nextInt(10);
        }
        return result;
    }


    public static JSONObject checkParam(JSONObject params, String... keys) {
        String key = "";
        if (params != null) {
            for (int i = 0; i < keys.length; i++) {
                if (params.has(keys[i])) {
                    if (StringUtils.isNotEmpty(params.getString(keys[i]))) {
                        if (i == keys.length - 1) {
                            if ("".equals(key)) {
                                return null;
                            }
                        }
                    } else {
                        key += keys[i];
                        key += ",";
                    }
                } else {
                    key += keys[i];
                    key += ",";
                }
            }
        }
        key = key.substring(0, key.length() - 1);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(PersonalConstants.FLAG, "9");
        jsonObject.put(PersonalConstants.MSG, "未传入值:" + key);
        return jsonObject;
    }


}
