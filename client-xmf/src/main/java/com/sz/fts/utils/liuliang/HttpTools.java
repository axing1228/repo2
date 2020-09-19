package com.sz.fts.utils.liuliang;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpTools {

    private static Logger logger = LogManager.getLogger(HttpTools.class);

    public static String sendMsg(String msg, String url, String font) {
        return sendMsg(msg, url, font, 0);
    }

    public static String sendMsg(String msg, String url, String font, int option) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        //设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom()
        .setSocketTimeout(30000).setConnectTimeout(7000).build();
        post.setConfig(requestConfig);
        CloseableHttpResponse res = null;
        try {
            StringEntity s = new StringEntity(msg);
            s.setContentEncoding("UTF-8");
            s.setContentType("text/xml;charset=UTF-8");// 发送数据需要设置contentType
            post.setEntity(s);
            res = client.execute(post);
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = res.getEntity();
                String result = EntityUtils.toString(entity, "UTF-8");//
                return result;
            }
            else{
                System.out.println("requestUrl:{" + url + "} result http status code: " +  + res.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            logger.error("HttpTools post error.",e);
        }
        finally{
            close(client, res);
        }
        return null;
    }
    
    /**
     * 释放资源
     * @param client
     * @param res
     */
    private static void close(CloseableHttpClient client, CloseableHttpResponse res)
    {
        try {
            if (res != null)
            {
                res.close();                
            }
        } catch (Exception e) {
            logger.error("CloseableHttpResponse close error.", e);
        }
        try {
            if (client != null)
            {
                client.close();                
            }
        } catch (Exception e) {
            logger.error("CloseableHttpClient close error.", e);
        }
    }
}
