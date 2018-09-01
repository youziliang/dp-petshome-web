package com.dp.petshome.utils;

import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import com.dp.petshome.enums.CharSets;

@SuppressWarnings("deprecation")
public class HttpUtil {

	private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);

	public static final String GET = "Get";
	public static final String POST = "Post";
	public static final String HEAD = "Head";
	public static final String PUT = "Put";
	public static final String DELETE = "Delete";
	public static final String TRACE = "Trace";
	public static final String PATCH = "Patch";
	public static final String OPTIONS = "Options";

	public static final String CONTENT_TYPE_XML = "Content-Type: application/xml";
	public static final String CONTENT_TYPE_URLENCODED = "Content-Type: application/x-www-form-urlencoded";
	public static final String CONTENT_TYPE_JSON = "Content-Type: application/json";

	private static PoolingHttpClientConnectionManager connMgr;
	private static RequestConfig requestConfig;
	private static final int MAX_TIMEOUT = 8000;

	static {
		// 设置连接池
		connMgr = new PoolingHttpClientConnectionManager();

		// 设置连接池大小
		connMgr.setMaxTotal(100);
		connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());

		RequestConfig.Builder configBuilder = RequestConfig.custom();
		// 设置连接超时
		configBuilder.setConnectTimeout(MAX_TIMEOUT);
		// 设置读取超时
		configBuilder.setSocketTimeout(MAX_TIMEOUT);
		// 设置从连接池获取连接实例的超时
		configBuilder.setConnectionRequestTimeout(MAX_TIMEOUT);
		// 在提交请求之前测试连接是否可用
		configBuilder.setStaleConnectionCheckEnabled(true);

		requestConfig = configBuilder.build();
	}

	/**
	 * @Description http请求
	 */
	public static String request(String url, String methodType, Map<String, Object> paramsMap, List<Header> headerList,
			Boolean openSSL) {
		return request(url, methodType, paramsMap, headerList, null, openSSL);
	}

	/**
	 * @Description http请求
	 */
	public static String request(String url, String methodType, Map<String, Object> paramsMap,
			List<Header> headerList) {
		return request(url, methodType, paramsMap, headerList, null, false);
	}

	/**
	 * @Description http请求
	 */
	public static String request(String url, List<Header> headerList) {
		return request(url, GET, null, headerList, null, false);
	}

	/**
	 * @Description http请求
	 */
	public static String request(String url, String methodType, Map<String, Object> paramsMap, List<Header> headerList,
			String encoding, Boolean openSSL) {
		String body = "";
		try {
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();

			// HttpGet请求不带参数
			if (StringUtils.contains(url, "?")) {
				paramsMap = null;
			} else {
				// 将paramsMap参数封装到NameValuePair
				map2NVPair(nvps, paramsMap);
				url = StringUtils.equals(methodType, GET)
						? new StringBuffer().append(url).append("?")
								.append(EntityUtils.toString(new UrlEncodedFormEntity(nvps, encoding))).toString()
						: url;
			}
			// log.info("请求地址：" + url);

			encoding = StringUtils.isBlank(encoding) ? CharSets.UTF8 : encoding;

			// 创建请求对象
			HttpRequestBase httpRequest = getHttpRequest(url, methodType);

			// 设置header信息
			httpRequest.setHeaders(getHeaders(headerList));

			// 判断是否支持设置entity(仅HttpPost、HttpPut、HttpPatch支持)
			if (HttpEntityEnclosingRequestBase.class.isAssignableFrom(httpRequest.getClass())) {
				// 设置参数到请求对象中
				String contentType = httpRequest.getHeaders(HttpHeaders.CONTENT_TYPE)[0].toString();
				switch (contentType) {
				case CONTENT_TYPE_XML:
					String xmlString = FormatUtil.map2Xml(paramsMap);
					log.info("请求参数：" + xmlString.toString());
					((HttpEntityEnclosingRequestBase) httpRequest)
							.setEntity(new StringEntity(xmlString, CharSets.UTF8));
					break;
				case CONTENT_TYPE_URLENCODED:
					log.info("请求参数：" + nvps.toString());
					((HttpEntityEnclosingRequestBase) httpRequest).setEntity(new UrlEncodedFormEntity(nvps, encoding));
					break;
				case CONTENT_TYPE_JSON:
					// TODO 待完善
					break;
				default:
					log.error("错误的请求格式: {}", contentType);
					break;
				}
			}
			// 调用发送请求
			log.info("开始请求: {}", url);
			body = execute(httpRequest, encoding, openSSL);
		} catch (Exception e) {
			log.error("请求异常: {}", e.getMessage());
		}
		return body;
	}

	private static String execute(HttpRequestBase httpRequest, String encoding, Boolean openSSL) {
		String body = "";
		CloseableHttpResponse response = null;
		CloseableHttpClient httpClient = null;
		if (openSSL) {
			httpClient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory())
					.setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
			log.info("执行SSL安全连接");
		} else {
			httpClient = HttpClients.createDefault();
		}
		try {
			// 执行请求操作，并拿到结果（同步阻塞）
			response = httpClient.execute(httpRequest);

			// 获取响应码
			int code = response.getStatusLine().getStatusCode();
			log.info("Http响应码: {}", code);

			// 获取结果实体
			HttpEntity entity = response.getEntity();

			if (HttpStatus.OK.value() == code && entity != null) {
				// 按指定编码转换结果实体为String类型
				body = EntityUtils.toString(entity, encoding);
				log.info("Http响应报文体: {}", body);
			}
			EntityUtils.consume(entity);
		} catch (ParseException | IOException e) {
			log.error("Http链路异常: {}", e.getMessage());
		} finally {
			close(response);
		}
		return body;
	}

	/**
	 * 创建SSL安全连接
	 *
	 * @return
	 */
	private static SSLConnectionSocketFactory createSSLConnSocketFactory() {
		SSLConnectionSocketFactory sslsf = null;
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {

				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			}).build();
			sslsf = new SSLConnectionSocketFactory(sslContext, new X509HostnameVerifier() {

				@Override
				public boolean verify(String arg0, SSLSession arg1) {
					return true;
				}

				@Override
				public void verify(String host, SSLSocket ssl) throws IOException {
				}

				@Override
				public void verify(String host, X509Certificate cert) throws SSLException {
				}

				@Override
				public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
				}
			});
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
		return sslsf;
	}

	/**
	 * @Description 尝试关闭response
	 */
	private static void close(HttpResponse resp) {
		try {
			if (resp == null)
				return;
			// 如果CloseableHttpResponse 是resp的父类，则支持关闭
			if (CloseableHttpResponse.class.isAssignableFrom(resp.getClass())) {
				((CloseableHttpResponse) resp).close();
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * @Description 获取请求头数组
	 */
	private static Header[] getHeaders(List<Header> headerList) {
		if (null == headerList || 0 == headerList.size()) {
			return null;
		}
		int size = headerList.size();
		Header[] headers = new Header[size];
		for (int i = 0; i < size; i++) {
			headers[i] = headerList.get(i);
		}
		return headers;
	}

	/**
	 * @Description 获取HttpRequest对象
	 */
	@SuppressWarnings({ "unchecked" })
	private static HttpRequestBase getHttpRequest(String url, String methodType) {
		HttpRequestBase httpRequest = null;
		methodType = "org.apache.http.client.methods.Http" + methodType;
		try {
			Class<HttpRequestBase> httpRequestClass = (Class<HttpRequestBase>) Class.forName(methodType);
			httpRequest = httpRequestClass.newInstance();
			httpRequest.setURI(URI.create(url));
		} catch (Exception e) {
			log.error("通过名称获取HttpRequest对象失败: {}", e.getMessage());
		}
		return httpRequest;
	}

	/**
	 * @Description 参数转换，将map中的参数，转到参数列表中
	 */
	private static void map2NVPair(List<NameValuePair> nvps, Map<String, Object> map) {
		if (map == null || 0 == map.size()) {
			return;
		}
		// 拼接参数
		for (Entry<String, Object> entry : map.entrySet()) {
			nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
		}
	}

}
