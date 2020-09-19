package com.sz.fts.impl.wxpay.sendred;

import com.lowagie.text.DocumentException;
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
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author 耿怀志
 * @version [版本号, 2018/4/12]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class MD5 {

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
     * 发送现金红包
     *
     * @throws KeyStoreException
     * @throws IOException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws UnrecoverableKeyException
     * @throws KeyManagementException
     * @throws DocumentException
     */
    public static void sendRedPack() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, KeyManagementException, UnrecoverableKeyException, DocumentException {
        // 获取uuid作为随机字符串
        String nonceStr = UUIDHexGenerator.generate();
        String today = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String code = createCode(10);
        //TODO 商户信息
        String mch_id = "1323521601";//商户号
        String appid = "wx1575a1a993950056";
        String openid = "oeSlKxGK-_NfZOCPnOFTGQhY0gJs"; //发送给指定微信用户的openid
        SendRedPackPo sendRedPackPo = new SendRedPackPo();
        String totalAmount = "100";

        sendRedPackPo.setNonce_str(nonceStr);
        sendRedPackPo.setMch_billno(mch_id + today + code);
        sendRedPackPo.setMch_id(mch_id);
        sendRedPackPo.setWxappid(appid);
        sendRedPackPo.setNick_name("怀志1");
        sendRedPackPo.setSend_name("怀志2");
        sendRedPackPo.setRe_openid(openid);
        sendRedPackPo.setTotal_amount(totalAmount);
        sendRedPackPo.setMin_value(totalAmount);
        sendRedPackPo.setMax_value(totalAmount);
        sendRedPackPo.setTotal_num("1");
        sendRedPackPo.setWishing("测试红包！");
        sendRedPackPo.setClient_ip("49.73.28.183"); //IP
        sendRedPackPo.setAct_name("测试红包");
        sendRedPackPo.setRemark("测试红包！");


        //把请求参数打包成数组
        Map<String, String> sParaTemp = new HashMap<String, String>();
        sParaTemp.put("nonce_str", sendRedPackPo.getNonce_str());
        sParaTemp.put("mch_billno", sendRedPackPo.getMch_billno());
        sParaTemp.put("mch_id", sendRedPackPo.getMch_id());
        sParaTemp.put("wxappid", sendRedPackPo.getWxappid());
        sParaTemp.put("nick_name", sendRedPackPo.getNick_name());
        sParaTemp.put("send_name", sendRedPackPo.getSend_name());
        sParaTemp.put("re_openid", sendRedPackPo.getRe_openid());
        sParaTemp.put("total_amount", sendRedPackPo.getTotal_amount());
        sParaTemp.put("min_value", sendRedPackPo.getMin_value());
        sParaTemp.put("max_value", sendRedPackPo.getMax_value());
        sParaTemp.put("total_num", sendRedPackPo.getTotal_num());
        sParaTemp.put("wishing", sendRedPackPo.getWishing());
        sParaTemp.put("client_ip", sendRedPackPo.getClient_ip());
        sParaTemp.put("act_name", sendRedPackPo.getAct_name());
        sParaTemp.put("remark", sendRedPackPo.getRemark());


        //除去数组中的空值和签名参数
        Map<String, String> sPara = paraFilter(sParaTemp);
        String prestr = createLinkString(sPara); //把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
        //TODO 商户支付密钥
        String key = "&key=HX123456WEIXIN65432120160328WEI8"; //商户支付密钥
        String mysign = MD5.sign(prestr, key, "utf-8").toUpperCase();

        sendRedPackPo.setSign(mysign);


        String respXml = MessageUtil.redToXml(sendRedPackPo);

        //打印respXml发现，得到的xml中有“__”不对，应该替换成“_”
        respXml = respXml.replace("__", "_");


        // 将解析结果存储在HashMap中
        Map<String, String> map = new HashMap<String, String>();


        KeyStore keyStore = KeyStore.getInstance("PKCS12");

        //TODO 证书位置
        FileInputStream instream = new FileInputStream(new File("C:\\Users\\genghz\\Desktop\\流量宝商户信息\\流量宝商户信息\\apiclient_cert.p12")); //此处为证书所放的绝对路径

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


            HttpPost httpPost = new HttpPost("https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack");

            StringEntity reqEntity = new StringEntity(respXml, "utf-8");

            // 设置类型
            reqEntity.setContentType("application/x-www-form-urlencoded");

            httpPost.setEntity(reqEntity);

            System.out.println("executing request" + httpPost.getRequestLine());


            CloseableHttpResponse response = httpclient.execute(httpPost);
            try {
                HttpEntity entity = response.getEntity();
                System.out.println("======="+response.getStatusLine());
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


        // 返回状态码
        String return_code = map.get("return_code");
        System.out.println("==="+return_code);
        // 返回信息
        String return_msg = map.get("return_msg");
        System.out.println("==="+return_msg);
        // 业务结果
        String result_code = map.get("result_code");
        System.out.println("==="+result_code);
        // 错误代码
        String err_code = map.get("err_code");
        System.out.println("==="+err_code);
        // 错误代码描述
        String err_code_des = map.get("err_code_des");
        System.out.println("==="+err_code_des);

/**
 * 根据以上返回码进行业务逻辑处理
 */

    }


    public static void main(String[] args) throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, DocumentException, IOException {
        sendRedPack();
    }
}