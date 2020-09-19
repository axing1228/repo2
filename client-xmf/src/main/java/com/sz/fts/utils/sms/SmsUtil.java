package com.sz.fts.utils.sms;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;

/**
 * 模拟第三方平台接口订购测试
 * 
 * @author Administrator
 * 
 */
public class SmsUtil {
    // 接口地址
    private static String requestUrl = "http://192.168.5.6:39090/itpi/sms/sendSMS";
    private static String source = "hd-szqx01-0324-1109";
    private static String token = "qAz4#6RfV1=3yHn8";


    /**
     * 短信接口
     * @param phone  手机号
     * @param content 短信内容
     */
    public static void sendSms(String phone, String content){

        String sign = sign(phone, content, token);
        JSONObject requestJson = new JSONObject();
        requestJson.put("phone", phone);
        requestJson.put("content", content);
        requestJson.put("source", source);
        requestJson.put("sign", sign);
        System.out.println(requestJson);
        HttpURLConnection connection = null;
        URL url;
        try {
            url = new URL(requestUrl);
            URLConnection theConnection = url.openConnection();
            if (!(theConnection instanceof HttpURLConnection)) {
                return;
            }
            connection = (HttpURLConnection) theConnection;
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(7000);
            connection.setReadTimeout(15000);
            connection.setRequestProperty("Content-type", "text/xml;charset=UTF-8");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.connect();
            connection.getOutputStream().write(requestJson.toJSONString().getBytes("UTF-8"));
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                System.out.println("response code:" + responseCode);
            }
            InputStream inputStream = connection.getInputStream();
            String resultJson = new String(readAllData(inputStream), "UTF-8");
            JSONObject response = JSON.parseObject(resultJson);
            System.out.println(response.toJSONString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static String sign(String phone, String content, String token){
        try {
            String signStr = phone + content + token;
            ArrayList<String> tt = new ArrayList<String>();
            tt.add(phone);
            tt.add(content);
            tt.add(token);
            Collections.sort(tt);
            return MD5.GetMD5Code(Base64.encode(signStr.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        return "";
    }
    
    public static byte[] readAllData(InputStream inputStream){
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        byte[] cache = new byte[1024];
        int readCount = -1;
        try {
            while((readCount=inputStream.read(cache))!=-1){
                arrayOutputStream.write(cache, 0, readCount);
            }
            arrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return arrayOutputStream.toByteArray();
    }

}
