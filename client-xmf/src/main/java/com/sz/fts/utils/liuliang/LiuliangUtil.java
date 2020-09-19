package com.sz.fts.utils.liuliang;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 耿怀志
 * @version [版本号, 2017/5/2]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class LiuliangUtil {
    private static final int connectionTimeOut = 2000;

    private static final int soTimeout = 40000;

    private static Logger logger = LogManager.getLogger(LiuliangUtil.class);

    public static String post2(String actionUrl, Map<String, String> params) {
        BasicHttpParams bp = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(bp, connectionTimeOut); // 超时时间设置
        HttpConnectionParams.setSoTimeout(bp, soTimeout);
        HttpClient httpclient = new DefaultHttpClient(bp);
        HttpPost httpPost = new HttpPost(actionUrl);
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry : params.entrySet()) {// 构建表单字段内容
            String key = entry.getKey();
            String value = entry.getValue();
            list.add(new BasicNameValuePair(key, value));
        }
        HttpResponse httpResponse;
        String responseString = "";
        logger.warn("传入后台的参数：" + list);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(list, HTTP.UTF_8));
            httpResponse = httpclient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                responseString = EntityUtils.toString(httpResponse.getEntity());
                return responseString;
            } else if (httpResponse.getStatusLine().getStatusCode() == 404) {
                logger.warn("actionUrl:{} not found 404!" + actionUrl);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return null;
    }

    public static void main(String[] args) {
        String request;
        Map<String, String> params = new HashMap<String, String>();
        try {

            request = SimpleDesAndroid
                    .encrypt(
                            "accNbr=1895160****;actionCode=接口编号;ztInterSource=渠道号",
                            DataDirection.TO_SERVER);
            params.put("para", request);
            String rs = post2(
                    "http://202.102.111.142/jszt/ipauth/**",
                    params);
            System.out.println(rs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
