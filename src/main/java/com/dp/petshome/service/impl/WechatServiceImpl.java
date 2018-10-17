package com.dp.petshome.service.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.dp.petshome.enums.CharSets;
import com.dp.petshome.enums.OrderStatus;
import com.dp.petshome.persistence.dto.UnifiedOrder;
import com.dp.petshome.persistence.model.User;
import com.dp.petshome.persistence.vo.OrderVo;
import com.dp.petshome.service.OrderService;
import com.dp.petshome.service.UserService;
import com.dp.petshome.service.WechatService;
import com.dp.petshome.utils.HttpUtil;
import com.dp.petshome.utils.PropertyUtil;
import com.dp.petshome.utils.SignUtil;

/**
 * @Dsecription 微信ServiceImpl
 * @author DU
 */
@Service
public class WechatServiceImpl implements WechatService {

	private static final List<Header> headers = new ArrayList<Header>();

	static {
		// 封装通用请求头
		headers.add(new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded"));
		headers.add(new BasicHeader(HttpHeaders.ACCEPT, "application/json"));
		headers.add(new BasicHeader(HttpHeaders.ACCEPT_CHARSET, CharSets.UTF8));
	}

	private static final Logger log = LoggerFactory.getLogger(WechatServiceImpl.class);

	@Autowired
	protected UserService userService;

	@Autowired
	protected OrderService orderService;

	/**
	 * @Description 通过code换取access_code和openid
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> getOpenidAndAccessTokenByCode(String code) {

		Map<String, String> map = new HashMap<>();

		Map<String, Object> params = new HashMap<>(4);
		params.put("appid", PropertyUtil.getProperty("wechat.appid"));
		params.put("secret", PropertyUtil.getProperty("wechat.secret"));
		params.put("grant_type", "authorization_code");
		params.put("code", code);

		// 通過code獲取access_token和openid
		String reqResult = HttpUtil.request("https://api.weixin.qq.com/sns/oauth2/access_token", HttpUtil.GET, params, headers);
		log.info("通过code换取access_code、openid、refresh_token结果: {}", reqResult);
		if (StringUtils.isNotBlank(reqResult) && StringUtils.startsWith(reqResult, "{") && StringUtils.endsWith(reqResult, "}")) {
			map = JSONObject.parseObject(reqResult, HashMap.class);
		}
		return map;
	}

	/**
	 * @Description 通过access_token和openid获取用户信息
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getUserInfoByAccessTokenAndOpenid(String access_token, String openid) {

		Map<String, Object> map = new HashMap<>(2);

		// 通过access_token和openid获取用户信息
		Map<String, Object> params = new HashMap<>(3);
		params.put("access_token", access_token);
		params.put("openid", openid);
		params.put("lang", "zh_CN");

		String reqResult = HttpUtil.request("https://api.weixin.qq.com/sns/userinfo", HttpUtil.GET, params, headers);
		log.info("通过access_token和openid获取用户信息結果: {}", reqResult);

		User user = new User();
		if (StringUtils.isNotBlank(reqResult) && StringUtils.startsWith(reqResult, "{") && StringUtils.endsWith(reqResult, "}")) {
			HashMap<String, Object> tempInfo = JSONObject.parseObject(reqResult, HashMap.class);
			Object errcode = tempInfo.get("errcode");
			Object errmsg = tempInfo.get("errmsg");
			if (null != errcode) {
				// access_token 過期，刷新一下
				map.put("errcode", errcode);
				map.put("errmsg", errmsg);
				return map;
			}

			user.setOpenid(openid);
			user.setNickname((String) tempInfo.get("nickname"));
			user.setSex((Integer) tempInfo.get("sex"));
			user.setCity((String) tempInfo.get("city"));
			user.setProvince((String) tempInfo.get("province"));
			user.setCountry((String) tempInfo.get("country"));
			user.setHeadImg((String) tempInfo.get("headimgurl"));

			// 根据openid獲取數據庫User，存在则更新，不存在则新增
			User temp = userService.getUserByOpenid(openid);
			if (null != temp) {
				userService.updateUser(user);
				user.setRole(temp.getRole());
				user.setScore(temp.getScore());
				user.setBalance(temp.getBalance());

				List<OrderVo> orders = orderService.getMyOrders(temp, OrderStatus.UNFINISHED.status);
				map.put("orders", orders);
			} else {
				userService.insertUser(user);
			}
			map.put("user", user);
		}
		return map;
	}

	/**
	 * @Description 刷新access_token
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> refreshAccessToken(String appid, String refresh_token) {

		Map<String, String> map = new HashMap<>(3);

		Map<String, Object> params = new HashMap<>(3);
		params.put("appid", appid);
		params.put("grant_type", "refresh_token");
		params.put("refresh_token", refresh_token);

		String reqResult = HttpUtil.request("https://api.weixin.qq.com/sns/oauth2/refresh_token", HttpUtil.GET, params, headers);
		log.info("刷新access_token結果: {}", reqResult);
		if (StringUtils.isNotBlank(reqResult) && StringUtils.startsWith(reqResult, "{") && StringUtils.endsWith(reqResult, "}")) {
			HashMap<String, Object> tempInfo = JSONObject.parseObject(reqResult, HashMap.class);
			String access_token_refresh = (String) tempInfo.get("access_token");
			String openid_refresh = (String) tempInfo.get("openid");
			String refresh_token_refresh = (String) tempInfo.get("refresh_token");
			map.put("access_token", access_token_refresh);
			map.put("openid", openid_refresh);
			map.put("refresh_token", refresh_token_refresh);
		}
		return map;
	}

	/**
	 * @Description 獲取微信授权地址
	 */
	@Override
	public String getAuthorizationUrl(String scope, String state, String redirect_uri) {

		StringBuffer sb = new StringBuffer();

		String uri = "https://open.weixin.qq.com/connect/oauth2/authorize";
		String appid = PropertyUtil.getProperty("wechat.appid");
		String response_type = "code";

		return sb.append(uri).append("?").append("appid=").append(appid).append("&redirect_uri=").append(redirect_uri).append("&response_type=").append(response_type)
				.append("&scope=").append(scope).append("&state=").append(state).append("#wechat_redirect").toString();
	}

	/**
	 * @Description 检查access_token有效性
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Boolean isAccessTokenAvailable(String access_token, String openid) {

		Boolean flag = false;

		Map<String, Object> params = new HashMap<>(2);
		params.put("access_token", access_token);
		params.put("openid", openid);

		String reqResult = HttpUtil.request("https://api.weixin.qq.com/cgi-bin/token", HttpUtil.GET, params, headers, true);
		log.info("检查access_token有效性结果: {}", reqResult);
		if (StringUtils.isNotBlank(reqResult) && StringUtils.startsWith(reqResult, "{") && StringUtils.endsWith(reqResult, "}")) {
			HashMap<String, Object> tempInfo = JSONObject.parseObject(reqResult, HashMap.class);
			Integer errcode = (Integer) tempInfo.get("errcode");
			if (0 == errcode) {
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * @Description 獲取AccessToken
	 */
	@Override
	public String getAccessToken(HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> params = new HashMap<>(3);
		params.put("grant_type", "client_credential");
		params.put("appid", PropertyUtil.getProperty("wechat.appid"));
		params.put("secret", PropertyUtil.getProperty("wechat.secret"));

		String reqResult = HttpUtil.request("https://api.weixin.qq.com/cgi-bin/token", HttpUtil.GET, params, headers, true);
		log.info("獲取AccessToken结果: {}", reqResult);
		return reqResult;
	}

	public static void main(String[] args) {
		System.out.println(UUID.randomUUID().toString());
	}

	/**
	 * @throws UnknownHostException
	 * @Description 统一下单
	 */
	@Override
	public String unifiedOrder(UnifiedOrder unifiedOrder) throws UnknownHostException {
		List<Header> headers = new ArrayList<Header>();
		headers.add(new BasicHeader(HttpHeaders.CONTENT_TYPE, "text/xml"));
		headers.add(new BasicHeader(HttpHeaders.ACCEPT_CHARSET, CharSets.UTF8));
		Map<String, Object> params = new HashMap<>(15);
		params.put("appid", PropertyUtil.getProperty("wechat.appid"));
		params.put("mch_id", PropertyUtil.getProperty("wechat.mchid"));
		params.put("device_info", "xinnanluyihao");
		params.put("nonce_str", StringUtils.replace(UUID.randomUUID().toString(), "-", ""));
		params.put("sign_type", "MD5");
		params.put("out_trade_no", unifiedOrder.getOrderNo());
		params.put("total_fee", unifiedOrder.getAmount());
		params.put("body", unifiedOrder.getCommDesc());
		params.put("detail", unifiedOrder.getCommDetail());
		params.put("openid", unifiedOrder.getOpenid());
		params.put("spbill_create_ip", InetAddress.getLocalHost().getHostAddress().toString());
		params.put("notify_url", PropertyUtil.getProperty("domain") + "/mine.html");
		params.put("trade_type", "JSAPI");
		// params.put("attach", "自定义附加参数");
		log.info("统一下单参数: {}", params);
		params.put("sign", SignUtil.getWechatSign(params, PropertyUtil.getProperty("wechat.secret")).toUpperCase());

		String reqResult = HttpUtil.request("https://api.mch.weixin.qq.com/pay/unifiedorder", HttpUtil.POST, params, headers);
		log.info("统一下单结果: {}", reqResult);
		return reqResult;
	}

}
