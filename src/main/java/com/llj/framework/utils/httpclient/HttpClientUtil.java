package com.llj.framework.utils.httpclient;

import java.io.File;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * http工具类
 * 
 * @author lu
 *
 */
public class HttpClientUtil {

	/** 日志 */
	private static Logger logger = LogManager.getLogger(HttpClientUtil.class.getName());

	/** 请求超时设置 */
	private static RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(15000).setConnectionRequestTimeout(7500)
			.setSocketTimeout(15000).build();

	/** 编码设置 */
	private static final String encoding = "UTF-8";

	/** 请求方式枚举 */
	public static enum httpRequestMethod {
		GET, POST;
	};

	/**
	 * 创建HTTPS连接
	 * 
	 * @return 自动关闭的https连接
	 * @throws Exception
	 * @author lu
	 */
	public static CloseableHttpClient getHttpsClient() throws Exception {
		CloseableHttpClient httpClient = null;
		// 重写ssl证书校验 do something or do nothing
		TrustManager trustManager = new X509TrustManager() {

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			@Override
			public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {

			}

			@Override
			public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {

			}
		};
		try {
			// 开启SSL
			SSLContext sslContext = SSLContext.getInstance("TLS");
			// 初始化ssl校验
			sslContext.init(null, new TrustManager[]{trustManager}, null);
			// 注册ssl链接工厂
			SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
			// 请求配置
			RequestConfig requestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD_STRICT).setExpectContinueEnabled(true)
					.setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
					.setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC)).build();
			// 链接配置
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
					.register("http", PlainConnectionSocketFactory.INSTANCE).register("https", socketFactory).build();
			// 创建ConnectionManager，添加Connection配置信息
			PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
			// 创建连接
			httpClient = HttpClients.custom().setConnectionManager(connectionManager).setDefaultRequestConfig(requestConfig).build();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("创建https连接出错。" + e.getMessage());
		}
		return httpClient;
	}

	/**
	 * 发送http请求
	 * 
	 * @param url
	 *            请求地址
	 * @param method
	 *            请求方式
	 * @param params
	 *            参数 K-V模式
	 * @return 返回结果字符串
	 * @throws Exception
	 * @author lu
	 */
	public static String sendHttpRequest(String url, httpRequestMethod method, Map<String, String> params) throws Exception {
		CloseableHttpResponse response = null;
		String result = null;
		logger.info(method.name() + ":" + url + "--" + params);
		if (httpRequestMethod.GET == method) {
			response = sendGetRequest(url, params);
		} else if (httpRequestMethod.POST == method) {
			response = sendPostRequest(url, params);
		}
		HttpEntity entity = response.getEntity();
		result = EntityUtils.toString(entity, encoding);
		int statusCode = response.getStatusLine().getStatusCode();
		response.close();
		if (statusCode == HttpStatus.SC_OK) {
			EntityUtils.consume(entity);
			logger.info("HTTP-RESPONSE" + result);
		} else {
			throw new Exception("发送HTTP请求:" + url + "出现异常,返回内容为:" + result);
		}
		return result;
	}

	/**
	 * 发送http请求
	 * 
	 * @param url
	 *            请求地址
	 * @param method
	 *            请求方式
	 * @param params
	 *            参数 K-V模式
	 * @return 返回结果字符串
	 * @throws Exception
	 * @author lu
	 */
	public static String sendHttpRequestForMsg(String url, httpRequestMethod method, Map<String, String> params) throws Exception {
		CloseableHttpResponse response = null;
		String result = null;
		logger.info(method.name() + ":" + url + "--" + params);
		if (httpRequestMethod.GET == method) {
			response = sendGetRequest(url, params);
		} else if (httpRequestMethod.POST == method) {
			response = sendPostRequest(url, params);
		}
		HttpEntity entity = response.getEntity();
		result = EntityUtils.toString(entity, encoding);
		int statusCode = response.getStatusLine().getStatusCode();
		response.close();
		if (statusCode == HttpStatus.SC_OK) {
			EntityUtils.consume(entity);
			logger.info("HTTP-RESPONSE" + result);
		} else {
			logger.error("发送HTTP请求:" + url + "出现异常,返回内容为:" + result);
		}
		return result;
	}

	/**
	 * http发送文件
	 * 
	 * @param url
	 *            请求路径
	 * @param params
	 *            参数
	 * @param files
	 *            文件集合
	 * @return
	 * @throws Exception
	 * @author lu
	 */
	public static String sendHttpRequestFile(String url, Map<String, String> params, File[] files) throws Exception {
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		String result = null;
		try {
			Charset charset = CharsetUtils.get(encoding);
			// post模式且带上传文件
			httpClient = getHttpClient();
			HttpPost httpPost = new HttpPost(url);
			httpPost.setConfig(requestConfig);
			// 创建提交
			MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
			// 开启浏览器兼容模式
			multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			// 设置编码集
			multipartEntityBuilder.setCharset(charset);
			// 非文件参数
			for (Map.Entry<String, String> entry : params.entrySet()) {
				StringBody value = new StringBody(entry.getValue(), ContentType.create("text/plain", charset));
				multipartEntityBuilder.addPart(entry.getKey(), value);
			}
			// 文件参数
			for (File file : files) {
				FileBody fileBody = new FileBody(file);
				multipartEntityBuilder.addPart(new String(file.getName().getBytes(), charset), fileBody);
			}
			// httpEntity
			HttpEntity httpEntity = multipartEntityBuilder.build();
			httpPost.setEntity(httpEntity);
			// 执行
			response = httpClient.execute(httpPost);
			// http 状态码
			int statusCode = response.getStatusLine().getStatusCode();
			result = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
			if (statusCode != HttpStatus.SC_OK) {
				throw new Exception("发送HTTP-FILE请求:" + url + "出现异常,返回内容为:" + result);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			throw new Exception(e.getMessage(), e);
		} finally {
			response.close();
			httpClient.close();
		}
		return result;
	}

	/**
	 * 获取http连接
	 * 
	 * @return
	 * @throws Exception
	 * @author lu
	 */
	public static CloseableHttpClient getHttpClient() throws Exception {
		CloseableHttpClient httpClient = null;
		try {
			// 创建连接
			httpClient = HttpClients.createDefault();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("创建http连接出错。" + e.getMessage());
		}
		return httpClient;
	}

	/**
	 * 发送get请求
	 * 
	 * @param url
	 * @param method
	 * @param params
	 * @return
	 * @throws Exception
	 */
	private static CloseableHttpResponse sendGetRequest(String url, Map<String, String> params) throws Exception {
		HttpGet httpGet = new HttpGet();
		httpGet.setConfig(requestConfig);
		if (params != null && params.size() > 0) {
			// 组装get 带参url
			StringBuffer urlParams = new StringBuffer(url + "?");
			for (Map.Entry<String, String> entry : params.entrySet()) {
				if (urlParams.toString().endsWith("?")) {
					urlParams.append(entry.getKey() + "=" + entry.getValue());
				} else {
					urlParams.append("&" + entry.getKey() + "=" + entry.getValue());
				}
			}
			httpGet.setURI(new URI(urlParams.toString()));
		} else {
			httpGet.setURI(new URI(url));
		}
		return getHttpClient().execute(httpGet);
	}

	/**
	 * 发送post请求
	 * 
	 * @param url
	 * @param method
	 * @param params
	 * @return
	 * @throws Exception
	 */
	private static CloseableHttpResponse sendPostRequest(String url, Map<String, String> params) throws Exception {
		HttpPost httpPost = new HttpPost(url);
		httpPost.setConfig(requestConfig);
		if (params != null && params.size() > 0) {
			List<NameValuePair> nvpList = new ArrayList<NameValuePair>();
			for (Map.Entry<String, String> entry : params.entrySet()) {
				nvpList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			httpPost.setEntity(new UrlEncodedFormEntity(nvpList, encoding));
		}

		return getHttpClient().execute(httpPost);
	}

	/**
	 * 获取request的IP地址
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 * @author lu
	 */
	public static String getRequestIp(HttpServletRequest request) throws Exception {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
	}
}