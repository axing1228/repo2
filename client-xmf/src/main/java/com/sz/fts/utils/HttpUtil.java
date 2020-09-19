package com.sz.fts.utils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class HttpUtil {

	private static final int connectionTimeOut = 10000;

	private static final int soTimeout = 40000;

	private static Logger logger = LogManager.getLogger(HttpUtil.class);

	/**
	 * 将数据流写入文件
	 *
	 * @param file
	 *            文件对象
	 */
	public static void streamToFile(MultipartFile filedate, File file) {

		OutputStream out = null;
		InputStream in = null;

		try {
			out = new FileOutputStream(file);
			in = filedate.getInputStream();
			int bytesRead;

			byte[] buffer = new byte[8192];

			while ((bytesRead = in.read(buffer, 0, 8192)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
		} catch (FileNotFoundException fnfe) {

		} catch (IOException ioe) {
		} finally {
			close(out);
			close(in);
		}
	}

	/**
	 * 文件流
	 *
	 * @param out
	 *            写文件流
	 * @see [类、类#方法、类#成员]
	 */
	public static void close(OutputStream out) {
		try {
			if (null != out) {
				out.flush();

				out.close();
			}
		} catch (IOException io) {
		}
	}

	/**
	 * 关闭流
	 *
	 * @param in
	 *            in
	 */
	public static void close(InputStream in) {
		try {
			if (null != in) {
				in.close();
			}
		} catch (IOException e) {
		}
	}



	/**
	 * post消息到服务器
	 * 
	 * @param serverUrl
	 *            服务器地址
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public static String postUrl(String serverUrl, String content) throws Exception{

		// 如果服务url为空，直接返回空
		if (null == serverUrl || 0 == serverUrl.length()) {
			return null;
		}

		BufferedReader br = null;
		HttpURLConnection urlCon = null;
		BufferedWriter bw = null;
		String result;
		try {
			URL url = new URL(serverUrl);
			urlCon = (HttpURLConnection) url.openConnection();
			urlCon.setRequestMethod("POST");
//			urlCon.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.112 Safari/537.36");
//			urlCon.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			urlCon.setDoInput(true);
			urlCon.setDoOutput(true);
			urlCon.setConnectTimeout(10000);  // 设置10秒后返回
			bw = new BufferedWriter(new OutputStreamWriter(urlCon.getOutputStream(), "utf-8"));
			bw.write(content);
			bw.flush();
			br = new BufferedReader(new InputStreamReader(urlCon.getInputStream(), "utf-8"));
			result = read(br);
//		} catch (Exception e) {
////			throw new RuntimeException("post message to server error!url " + serverUr, e);
//			logger.error("异常："+e);
//			return "post message to server error!url" + serverUrl;
		} finally {
			if (null != br) {
				try {
					br.close();
				} catch (IOException e) {
				}
			}
			if (null != bw) {
				try {
					bw.close();
				} catch (IOException e) {
				}
			}
			if (null != urlCon) {
				try {
					urlCon.disconnect();
				} catch (Exception e) {
				}
			}
		}
		return result;
	}
	
	/**
	 * post消息到服务器
	 * 
	 * @param serverUrl
	 *            服务器地址
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public static String njqd_post(String serverUrl, String content) {

		// 如果服务url为空，直接返回空
		if (null == serverUrl || 0 == serverUrl.length()) {
			return null;
		}
		BufferedReader br = null;
		HttpURLConnection urlCon = null;
		BufferedWriter bw = null;
		String result;
		try {
			URL url = new URL(serverUrl);
			urlCon = (HttpURLConnection) url.openConnection();
			urlCon.setRequestMethod("POST");
			urlCon.setConnectTimeout(3000);
			urlCon.setDoInput(true);
			urlCon.setDoOutput(true);
			bw = new BufferedWriter(new OutputStreamWriter(urlCon.getOutputStream(), "utf-8"));
			bw.write(content);
			bw.flush();
			br = new BufferedReader(new InputStreamReader(urlCon.getInputStream(), "utf-8"));
			result = read(br);
		} catch (Exception e) {
			throw new RuntimeException("post message to server error!url " + serverUrl, e);
		} finally {
			if (null != br) {
				try {
					br.close();
				} catch (IOException e) {
				}
			}
			if (null != bw) {
				try {
					bw.close();
				} catch (IOException e) {
				}
			}
			if (null != urlCon) {
				try {
					urlCon.disconnect();
				} catch (Exception e) {
				}
			}
		}
		return result;
	}

	/**
	 * 读响应码
	 * 
	 * @param br
	 *            输入流
	 * @return 响应码
	 * @see [类、类#方法、类#成员]
	 */
	public static String read(BufferedReader br) {
		StringBuilder buffer = new StringBuilder();
		try {
			String line = null;
			while (null != (line = br.readLine())) {
				buffer.append(line);
			}
		} catch (Exception e) {
			throw new RuntimeException("send short message error", e);
		}

		return buffer.toString();
	}

	/**
	 * 发送消息到服务器
	 * 
	 * @param serverUrl
	 *            服务器地址
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public static String sendUrl(String serverUrl) {

		String result = sendUrl(serverUrl, "utf-8");

		return result;
	}
	
	
	/**
	 * 发送消息到服务器
	 * 
	 * @param serverUrl
	 *            服务器地址
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public static String sendUrlCode(String serverUrl) {

		String result = sendUrl(serverUrl, "GBK");

		return result;
	}

	public static void main(String[] args) {
		System.out.println(sendUrl("http://quanzitest.118114sz.com/wechat/thirdPart?action=share&u=http://www.118114sz.com.cn/szfts/pages/active/sudoku/Discount.html"));
	}

	/**
	 * 发送消息到服务器
	 * 
	 * @param serverUrl
	 *            服务器地址
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public static String sendUrl(String serverUrl, String charsetName) {

		BufferedReader br = null;
		HttpURLConnection urlCon = null;
		String result;
		try {
			URL url = new URL(serverUrl);
			urlCon = (HttpURLConnection) url.openConnection();
			urlCon.setRequestMethod("GET");
			br = new BufferedReader(new InputStreamReader(urlCon.getInputStream(), charsetName));
			result = read(br);
		} catch (Exception e) {
			throw new RuntimeException("send request message error", e);
		} finally {
			if (null != br) {
				try {
					br.close();
				} catch (IOException e) {
				}
			}
			if (null != urlCon) {
				try {
					urlCon.disconnect();
				} catch (Exception e) {
				}
			}
		}
		return result;
	}

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

	public static String post(String actionUrl, Map<String, String> params) {
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
		logger.warn("-----HttpClientHelper4TelephoneFee-----" + list);
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(list, HTTP.UTF_8));
			httpResponse = httpclient.execute(httpPost);
			logger.warn("-----httpResponse-----" + httpResponse);
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

	private static PoolingHttpClientConnectionManager connManager = null;

	private static CloseableHttpClient httpClient = null;

	/**
	 * HTTP保留时间
	 */
	private final static int MAX_HTTP_KEEP_ALIVE = 30 * 1000;

	/**
	 * 最大连接数
	 */
	private final static int MAX_TOTAL_CONNECTIONS = 800;

	/**
	 * 每个路由最大连接数
	 */
	private final static int MAX_ROUTE_CONNECTIONS = 400;

	/**
	 * 获取连接超时时间
	 */
	private static final int CONNECT_TIMEOUT = 5000;

	/**
	 * 连接处理超时时间
	 */
	private static final int SOCKET_TIMEOUT = 20000;

	static {
		HttpRequestRetryHandler myRetryHandler        = customRetryHandler();
		ConnectionKeepAliveStrategy customKeepAliveHander = customKeepAliveHander();
		connManager = new PoolingHttpClientConnectionManager();
		connManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);
		connManager.setDefaultMaxPerRoute(MAX_ROUTE_CONNECTIONS);
		HttpHost localhost = new HttpHost("locahost", 80);
		connManager.setMaxPerRoute(new HttpRoute(localhost), 50);
		httpClient = HttpClients.custom().setConnectionManager(connManager).setKeepAliveStrategy(customKeepAliveHander).setRetryHandler(myRetryHandler).build();
	}

	/**
	 * 设置HTTP连接保留时间
	 *
	 * @return 保留策略
	 */
	private static ConnectionKeepAliveStrategy customKeepAliveHander() {
		ConnectionKeepAliveStrategy myKeepAliveHander = new ConnectionKeepAliveStrategy() {

			@Override
			public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
				return MAX_HTTP_KEEP_ALIVE;
			}

		};
		return myKeepAliveHander;
	}

	/**
	 * 是否重试
	 *
	 * @return false，不重试
	 */
	private static HttpRequestRetryHandler customRetryHandler() {
		HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() {

			@Override
			public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
				return false;
			}

		};
		return myRetryHandler;
	}

	public static String doPost(String url, String content) throws IOException {
		String charset = "UTF-8";
		List<Header> headers = new ArrayList<Header>();
		headers.add(new BasicHeader("clientName", "szfts"));
		headers.add(new BasicHeader("version", "3.2"));

		CloseableHttpResponse response = null;

		HttpPost httpPost = new HttpPost(url);

		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
		httpPost.setConfig(requestConfig);

		if (CollectionUtils.isNotEmpty(headers)) {
			for (Header header : headers) {
				httpPost.setHeader(header);
			}
		}

		if (content!=null && !content.equals("")) {
			httpPost.addHeader("Content-type","application/json; charset=utf-8");
			httpPost.setHeader("Accept", "application/json");
			httpPost.setEntity(new StringEntity(content, Charset.forName("UTF-8")));
		}

		try {
			response = httpClient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {

				HttpEntity repEntity = response.getEntity();
				if (repEntity != null) {
					System.out.println("请求响应:resp="+EntityUtils.toString(repEntity, charset));
				}
				return "";
			}
			HttpEntity repEntity = response.getEntity();
			String     resp      = EntityUtils.toString(repEntity, charset);
			System.out.println("请求响应:"+resp);
			return resp;
		} finally {
			HttpClientUtils.closeQuietly(response);
		}
	}


}
