package com.sz.fts.utils;

import com.sz.fts.utils.sms.MD5;
import net.sf.json.JSONObject;

/**
 * @author 征华兴
 * @date 下午 5:57  2019/7/28 0028
 * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
 */
public class SerectUtil {

    private static String md5_key = "RGSlkg012_KGFqwe_a";

    public static boolean check(String timestamp, String user, String sign) {
        if (timestamp == null || user == null || sign == null) {
            return false;
        }
        if (DateUtils.getSecondsCompared(timestamp) > 600) {
            return false;
        }
        String value = user + timestamp + md5_key;
        String check = MD5.GetMD5Code(value);
        System.out.println("============check========" + check + ",sign" + sign);
        return sign.equals(check);
    }

    public static boolean checkTX(JSONObject json) {
        if (DateUtils.getSecondsCompared(json.getString("timestamp")) > 600) {
            return false;
        }
        String value = "money="+json.getString("money")+
                "&openid="+json.getString("openid")+
                "&orderSeq="+json.getString("orderSeq")+
                "&returnUrl="+json.getString("returnUrl")+
                "&timestamp="+json.getString("timestamp")+
                "&user="+json.getString("user")+
                "&key="+md5_key;
        String check = MD5.GetMD5Code(value);
        System.out.println("============check========" + check + ",sign" + json.getString("sign"));
        return json.getString("sign").equals(check);
    }

    public static void main(String[] args) {
//        String time = String.valueOf(System.currentTimeMillis()).substring(0, 10);
//        String openid = "oFYchs9TVX9tXdHzr2Q-udywYdGc";
//        String money = "1";
//        String orderSeq = "103320200403095247343324";
//        String returnUrl = "http://www.118114sz.com.cn/xmf/test/testPayMoney";
//        System.out.println(time);
//        String user = "payMent";
//        String value = "money="+money+"&openid="+openid+
//                "&orderSeq="+orderSeq+"&returnUrl="+returnUrl+
//                "&timestamp="+time+"&user="+user+"&key="+md5_key;
//        System.out.println(value);
//        String sign = MD5.GetMD5Code(value);
//        System.out.println(sign);
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("money",money);
//        jsonObject.put("openid",openid);
//        jsonObject.put("orderSeq",orderSeq);
//        jsonObject.put("returnUrl",returnUrl);
//        jsonObject.put("timestamp",time);
//        jsonObject.put("user",user);
//        jsonObject.put("sign",sign);
//        System.out.println(jsonObject);
        JSONObject json = new JSONObject();
        json.put("fee", "1");
        json.put("flag", "0");
        json.put("msg", "成功");
        json.put("orderSeq", "103320200403095247343324");
        json.put("payTime", "20200721120118");
        json.put("timestamp",System.currentTimeMillis()/1000L);
        json.put("uptranSeq", "4200000525202005210133154818");
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
        System.out.println(json);

    }

}
