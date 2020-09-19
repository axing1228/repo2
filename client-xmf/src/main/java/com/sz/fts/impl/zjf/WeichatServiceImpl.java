package com.sz.fts.impl.zjf;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sz.fts.bean.zjf.PersonalConstants;
import com.sz.fts.service.zjf.WeichatService;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @NAME WeichatServiceImpl
 * @AUTHOR 朱建峰
 * @DATE 2019/7/18 0018 下午 1:31
 * @DESCRIPTION 微信服务实现
 */
@Service
public class WeichatServiceImpl implements WeichatService {

    public String getOpenId(String code) throws IOException {
        //请求该链接获取access_token
        HttpPost httppost = new HttpPost("https://api.weixin.qq.com/sns/oauth2/access_token");
        //组装请求参数
        String reqEntityStr = "appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
        reqEntityStr = reqEntityStr.replace("APPID", PersonalConstants.APPID);
        reqEntityStr = reqEntityStr.replace("SECRET", PersonalConstants.SECRET);
        reqEntityStr = reqEntityStr.replace("CODE", code);
        StringEntity reqEntity = new StringEntity(reqEntityStr);
        //设置参数
        httppost.setEntity(reqEntity);
        //设置浏览器
        CloseableHttpClient httpclient = HttpClients.createDefault();
        //发起请求
        CloseableHttpResponse response = httpclient.execute(httppost);
        //获得请求内容
        String strResult = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));
        //获取openid
        JSONObject jsonObject = JSON.parseObject(strResult);
        return jsonObject.getString("openid");
    }


}
