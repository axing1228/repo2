package com.sz.fts.impl.wxpay.qiyepay;

import com.alibaba.fastjson.JSONObject;
import com.lowagie.text.DocumentException;
import com.sz.fts.impl.wxpay.sendred.MD5;
import com.sz.fts.utils.DateUtils;
import com.sz.fts.utils.MessageUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.*;

/**
 * @author 耿怀志
 * @version [版本号, 2018/4/13]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class QiyePay {


    /**
     * 签名字符串
     *
     * @param text          需要签名的字符串
     * @param key           密钥
     * @param input_charset 编码格式
     * @return 签名结果
     */
    public static String sign(String text, String key, String input_charset) {
        text = text + key;
        return DigestUtils.md5Hex(getContentBytes(text, input_charset));
    }

    /**
     * 签名字符串
     *
     * @param text          需要签名的字符串
     * @param sign          签名结果
     * @param key           密钥
     * @param input_charset 编码格式
     * @return 签名结果
     */
    public static boolean verify(String text, String sign, String key, String input_charset) {
        text = text + key;
        String mysign = DigestUtils.md5Hex(getContentBytes(text, input_charset));
        if (mysign.equals(sign)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * @param content
     * @param charset
     * @return
     * @throws SignatureException
     * @throws UnsupportedEncodingException
     */
    private static byte[] getContentBytes(String content, String charset) {
        if (charset == null || "".equals(charset)) {
            return content.getBytes();
        }
        try {
            return content.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
        }
    }


    /**
     * 生成6位或10位随机数
     * param codeLength(多少位)
     *
     * @return
     */
    private static String createCode(int codeLength) {
        String code = "";
        for (int i = 0; i < codeLength; i++) {
            code += (int) (Math.random() * 9);
        }
        return code;
    }

    private static boolean isValidChar(char ch) {
        if ((ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z'))
            return true;
        if ((ch >= 0x4e00 && ch <= 0x7fff) || (ch >= 0x8000 && ch <= 0x952f))
            return true;// 简体中文汉字编码
        return false;
    }


    /**
     * 除去数组中的空值和签名参数
     *
     * @param sArray 签名参数组
     * @return 去掉空值与签名参数后的新签名参数组
     */
    public static Map<String, String> paraFilter(Map<String, String> sArray) {


        Map<String, String> result = new HashMap<String, String>();


        if (sArray == null || sArray.size() <= 0) {
            return result;
        }


        for (String key : sArray.keySet()) {
            String value = sArray.get(key);
            if (value == null || value.equals("") || key.equalsIgnoreCase("sign")
                    || key.equalsIgnoreCase("sign_type")) {
                continue;
            }
            result.put(key, value);
        }


        return result;
    }


    /**
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     *
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    public static String createLinkString(Map<String, String> params) {


        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);


        String prestr = "";


        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);


            if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }


        return prestr;
    }


    /**
     * 企业付款到余额
     *
     * @throws KeyStoreException
     * @throws IOException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws UnrecoverableKeyException
     * @throws KeyManagementException
     * @throws DocumentException
     */
    public static JSONObject qiYePay(String openid, String amount) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, KeyManagementException, UnrecoverableKeyException, DocumentException {
        // 获取uuid作为随机字符串
        String nonceStr = UUIDHexGenerator.generate();
        //TODO 商户信息
        String mch_id = "1538209481";//商户号
        String appid = "wx0b8dfe36ecf03148";
        QiyeCas qiyeCas = new QiyeCas();
        qiyeCas.setAmount(amount);
        qiyeCas.setCheck_name("NO_CHECK");
        qiyeCas.setDesc("苏州电信业务分享奖励金");
        qiyeCas.setMch_appid(appid);
        qiyeCas.setMchid(mch_id);
        qiyeCas.setNonce_str(nonceStr);
        qiyeCas.setOpenid(openid);
        String tradeNo = "XMF" + DateUtils.getCurrentTime17();
        qiyeCas.setPartner_trade_no(tradeNo);

        qiyeCas.setSpbill_create_ip("58.211.5.59");


        //把请求参数打包成数组
        Map<String, String> sParaTemp = new HashMap<String, String>();
        sParaTemp.put("mch_appid", appid);
        sParaTemp.put("mchid", mch_id);
        sParaTemp.put("nonce_str", nonceStr);
        sParaTemp.put("partner_trade_no", tradeNo);
        sParaTemp.put("openid", openid);
        sParaTemp.put("check_name", "NO_CHECK");
        sParaTemp.put("amount", amount);
        sParaTemp.put("desc", "苏州电信业务分享奖励金");
        sParaTemp.put("spbill_create_ip", "58.211.5.59");


        //除去数组中的空值和签名参数
        Map<String, String> sPara = paraFilter(sParaTemp);
        String prestr = createLinkString(sPara); //把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
        //TODO 商户支付密钥
        String key = "&key=eGm0DMAlaJGBq1Qs3EYmcEEXlq3fBEYn"; //商户支付密钥
        String mysign = MD5.sign(prestr, key, "utf-8").toUpperCase();
        sParaTemp.put("sign", mysign);
        qiyeCas.setSign(mysign);

        String respXml = MessageUtil.qYToXml(qiyeCas);

        //打印respXml发现，得到的xml中有“__”不对，应该替换成“_”
        respXml = respXml.replace("__", "_");


        // 将解析结果存储在HashMap中
        Map<String, String> map = new HashMap<String, String>();


        KeyStore keyStore = KeyStore.getInstance("PKCS12");

        //TODO 证书位置
        FileInputStream instream = new FileInputStream(new File("/home/hongxin/apache-client-xmf/webapps/xmf/WEB-INF/file/apiclient_cert.p12")); //此处为证书所放的绝对路径

        try {
            keyStore.load(instream, mch_id.toCharArray());
        } finally {
            instream.close();
        }


        // Trust own CA and all self-signed certs
        SSLContext sslcontext = SSLContexts.custom()
                .loadKeyMaterial(keyStore, mch_id.toCharArray())
                .build();
        // Allow TLSv1 protocol only
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslcontext,
                new String[]{"TLSv1"},
                null,
                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        CloseableHttpClient httpclient = HttpClients.custom()
                .setSSLSocketFactory(sslsf)
                .build();

        try {


            HttpPost httpPost = new HttpPost("https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers");

            StringEntity reqEntity = new StringEntity(respXml, "utf-8");

            // 设置类型
            reqEntity.setContentType("application/x-www-form-urlencoded");

            httpPost.setEntity(reqEntity);

            System.out.println("executing request" + httpPost.getRequestLine());


            CloseableHttpResponse response = httpclient.execute(httpPost);
            try {
                HttpEntity entity = response.getEntity();
                System.out.println("=======" + response.getStatusLine());
                if (entity != null) {
                    // 从request中取得输入流
                    InputStream inputStream = entity.getContent();
                    // 读取输入流
                    SAXReader reader = new SAXReader();
                    Document document = reader.read(inputStream);
                    // 得到xml根元素
                    Element root = document.getRootElement();
                    // 得到根元素的所有子节点
                    List<Element> elementList = root.elements();
                    // 遍历所有子节点
                    for (Element e : elementList)
                        map.put(e.getName(), e.getText());
                    // 释放资源
                    inputStream.close();

                }
                EntityUtils.consume(entity);
            } catch (org.dom4j.DocumentException e) {
                e.printStackTrace();
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
        JSONObject out = new JSONObject();
        out.put("tradeNo", tradeNo);
        // 返回状态码
        String return_code = map.get("return_code");
        System.out.println("=return_code=" + return_code);
        if (return_code.equals("FAIL")) {
            // 返回信息
            String return_msg = map.get("return_msg");
            System.out.println("=return_msg=" + return_msg);

            // 错误代码
            String err_code = map.get("err_code");
            System.out.println("=err_code=" + err_code);
            if (err_code.equals("SYSTEMERROR")) {
                out.put("code", "2");
                return out;
            }
            // 错误代码描述
            String err_code_des = map.get("err_code_des");
            System.out.println("=err_code_des=" + err_code_des);
            out.put("code", "1");
            out.put("errCode", err_code);
            return out;
        }
        // 业务结果
        String result_code = map.get("result_code");
        System.out.println("=result_code=" + result_code);
        if (result_code.equals("FAIL")) {
            // 错误代码
            String err_code = map.get("err_code");
            System.out.println("=err_code=" + err_code);
            if (err_code.equals("SYSTEMERROR")) {
                out.put("code", "2");
                return out;
            }
            out.put("code", "1");
            out.put("errCode", err_code);
            return out;
        }
        out.put("code", "0");
        return out;

    }

    /**
     * 企业付款到余额
     *
     * @throws KeyStoreException
     * @throws IOException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws UnrecoverableKeyException
     * @throws KeyManagementException
     * @throws DocumentException
     */
    public static net.sf.json.JSONObject qiYePayTwo(String openid, String amount) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, KeyManagementException, UnrecoverableKeyException, DocumentException {
        // 获取uuid作为随机字符串
        String nonceStr = UUIDHexGenerator.generate();
        //TODO 商户信息
        String mch_id = "1542973171";//商户号
        String appid = "wxe5930e19f587de32";
        QiyeCas qiyeCas = new QiyeCas();
        qiyeCas.setAmount(amount);
        qiyeCas.setCheck_name("NO_CHECK");
        qiyeCas.setDesc("苏州电信业务分享奖励金");
        qiyeCas.setMch_appid(appid);
        qiyeCas.setMchid(mch_id);
        qiyeCas.setNonce_str(nonceStr);
        qiyeCas.setOpenid(openid);
        String tradeNo = "suzhouDianxin" + DateUtils.getCurrentTime17();
        qiyeCas.setPartner_trade_no(tradeNo);

        qiyeCas.setSpbill_create_ip("58.211.5.59");


        //把请求参数打包成数组
        Map<String, String> sParaTemp = new HashMap<String, String>();
        sParaTemp.put("mch_appid", appid);
        sParaTemp.put("mchid", mch_id);
        sParaTemp.put("nonce_str", nonceStr);
        sParaTemp.put("partner_trade_no", tradeNo);
        sParaTemp.put("openid", openid);
        sParaTemp.put("check_name", "NO_CHECK");
        sParaTemp.put("amount", amount);
        sParaTemp.put("desc", "苏州电信业务分享奖励金");
        sParaTemp.put("spbill_create_ip", "58.211.5.59");


        //除去数组中的空值和签名参数
        Map<String, String> sPara = paraFilter(sParaTemp);
        String prestr = createLinkString(sPara); //把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
        //TODO 商户支付密钥
        String key = "&key=Frtxhjy842369nfdyjFsrjbcddZfh856"; //商户支付密钥
        String mysign = MD5.sign(prestr, key, "utf-8").toUpperCase();
        sParaTemp.put("sign", mysign);
        qiyeCas.setSign(mysign);

        String respXml = MessageUtil.qYToXml(qiyeCas);

        //打印respXml发现，得到的xml中有“__”不对，应该替换成“_”
        respXml = respXml.replace("__", "_");


        // 将解析结果存储在HashMap中
        Map<String, String> map = new HashMap<String, String>();


        KeyStore keyStore = KeyStore.getInstance("PKCS12");

        //TODO 证书位置
        FileInputStream instream = new FileInputStream(new File("/home/hongxin/apache-client-xmf/webapps/xmf/WEB-INF/file/apiclient_cert1.p12")); //此处为证书所放的绝对路径

        try {
            keyStore.load(instream, mch_id.toCharArray());
        } finally {
            instream.close();
        }


        // Trust own CA and all self-signed certs
        SSLContext sslcontext = SSLContexts.custom()
                .loadKeyMaterial(keyStore, mch_id.toCharArray())
                .build();
        // Allow TLSv1 protocol only
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslcontext,
                new String[]{"TLSv1"},
                null,
                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        CloseableHttpClient httpclient = HttpClients.custom()
                .setSSLSocketFactory(sslsf)
                .build();

        try {


            HttpPost httpPost = new HttpPost("https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers");

            StringEntity reqEntity = new StringEntity(respXml, "utf-8");

            // 设置类型
            reqEntity.setContentType("application/x-www-form-urlencoded");

            httpPost.setEntity(reqEntity);

            System.out.println("executing request" + httpPost.getRequestLine());


            CloseableHttpResponse response = httpclient.execute(httpPost);
            try {
                HttpEntity entity = response.getEntity();
                System.out.println("=======" + response.getStatusLine());
                if (entity != null) {
                    // 从request中取得输入流
                    InputStream inputStream = entity.getContent();
                    // 读取输入流
                    SAXReader reader = new SAXReader();
                    Document document = reader.read(inputStream);
                    // 得到xml根元素
                    Element root = document.getRootElement();
                    // 得到根元素的所有子节点
                    List<Element> elementList = root.elements();
                    // 遍历所有子节点
                    for (Element e : elementList)
                        map.put(e.getName(), e.getText());
                    // 释放资源
                    inputStream.close();

                }
                EntityUtils.consume(entity);
            } catch (org.dom4j.DocumentException e) {
                e.printStackTrace();
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
        net.sf.json.JSONObject out = new net.sf.json.JSONObject();
        out.put("tradeNo", tradeNo);
        // 返回状态码
        String return_code = map.get("return_code");
        System.out.println("=return_code=" + return_code);
        if (return_code.equals("FAIL")) {
            // 返回信息
            String return_msg = map.get("return_msg");
            System.out.println("=return_msg=" + return_msg);

            // 错误代码
            String err_code = map.get("err_code");
            System.out.println("=err_code=" + err_code);
            if (err_code.equals("SYSTEMERROR")) {
                out.put("code", "2");
                return out;
            }
            // 错误代码描述
            String err_code_des = map.get("err_code_des");
            System.out.println("=err_code_des=" + err_code_des);
            out.put("code", "1");
            out.put("errCode", err_code);
            return out;
        }
        // 业务结果
        String result_code = map.get("result_code");
        System.out.println("=result_code=" + result_code);
        if (result_code.equals("FAIL")) {
            // 错误代码
            String err_code = map.get("err_code");
            System.out.println("=err_code=" + err_code);
            if (err_code.equals("SYSTEMERROR")) {
                out.put("code", "2");
                return out;
            }
            out.put("code", "1");
            out.put("errCode", err_code);
            return out;
        }
        out.put("code", "0");
        return out;

    }

//
//    public static void main(String[] args) throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, DocumentException, IOException {
//        System.out.println(qiYePayTwo("oz--A5jCboKkVW_ze9onJLk5_h_o","100"));
//    }


}
