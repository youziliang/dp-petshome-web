package com.dp.petshome.web;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dp.petshome.enums.HttpStatus;
import com.dp.petshome.persistence.dto.HttpResult;
import com.dp.petshome.service.UserService;
import com.dp.petshome.service.WechatService;
import com.dp.petshome.utils.CookieUtil;
import com.dp.petshome.utils.EhCacheUtil;
import com.dp.petshome.utils.SignUtil;

/**
 * @Dsecription 微信Controller
 * @author DU
 */
@Controller
@RequestMapping("wechat")
public class WechatController {

	// private static final String ACCESS_TOKEN_KEY = "access_token";
	// private static final String REFRESH_TOKEN_KEY = "refresh_token";
	// private static final String OPENID_KEY = "openid";
	private static final String USER_CACHE = "userCache";
	private static final String USER_ID = "userId";

	private static final Logger log = LoggerFactory.getLogger(WechatController.class);

	@Autowired
	protected EhCacheUtil ehCacheUtil;

	@Autowired
	protected WechatService wechatService;

	@Autowired
	protected UserService userService;

	@Value("${wechat.appid}")
	private String appid;

	/**
	 * @Description 微信檢查
	 */
	@GetMapping(value = "check")
	@ResponseBody
	public String check(HttpServletRequest request, HttpServletResponse response) {

		// 微信加密签名
		String signature = request.getParameter("signature");
		// 时间戳
		String timestamp = request.getParameter("timestamp");
		// 随机数
		String nonce = request.getParameter("nonce");
		// 随机字符串
		String echostr = request.getParameter("echostr");
		try {
			// 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
			if (SignUtil.checkWechatSignature(signature, timestamp, nonce)) {
				return echostr;
			}
			log.info("微信檢查结果: {}", echostr);
		} catch (Exception e) {
			log.error("微信檢查异常: {}", e.getMessage());
		}
		return echostr;
	}

	/**
	 * @Description 加載用戶信息（用戶登陸）
	 */
	@SuppressWarnings("unchecked")
	@GetMapping(value = "loadUserInfo")
	@ResponseBody
	public HttpResult<Object> loadUserInfo(HttpServletRequest request, HttpServletResponse response) {

		HttpResult<Object> result = new HttpResult<>();

		Map<String, Object> userInfo = new HashMap<>(1);
		try {
			// 获取临时用户id
			String userId = CookieUtil.getCookie(request, USER_ID);
			// 處於登陸狀態，根据userId获取相应的access_token和openid、refresh_token
			HashMap<String, String> importances = (HashMap<String, String>) ehCacheUtil.get(USER_CACHE, userId);
			String access_token = importances.get("access_token");
			String openid = importances.get("openid");
			String refresh_token = importances.get("refresh_token");
			log.info("缓存中的的 access_token: {}, openid: {},  refresh_token: {}", access_token, openid, refresh_token);

			if (StringUtils.isNotBlank(access_token) && StringUtils.isNotBlank(openid) && StringUtils.isNotBlank(refresh_token)) {
				// TODO 判断access_token是否还有效 wechatService.isAccessTokenAvailable(access_token,
				// openid)
				// 通过緩存的access_token和openid获取用户信息
				userInfo = wechatService.getUserInfoByAccessTokenAndOpenid(access_token, openid);
				// 檢驗access_token是否還有效
				if (userInfo.containsKey("errcode")) {
					log.info("獲取用戶信息失敗: {}", userInfo.get("errcode"));

					// 這裏暫不做具體判斷，一律按access_key過期處理
					Map<String, String> refreshMap = wechatService.refreshAccessToken(appid, refresh_token);
					String access_token_refresh = refreshMap.get("access_token");
					String openid_refresh = refreshMap.get("openid");
					String refresh_token_refresh = refreshMap.get("refresh_token");

					// TODO 之后会通过定时任务刷新
					// 將刷新后的access_token和openid、refresh_token存入ehcache
					HashMap<String, String> aof = new HashMap<>();
					aof.put("access_token", access_token_refresh);
					aof.put("openid", openid_refresh);
					aof.put("refresh_token", refresh_token_refresh);
					ehCacheUtil.set(USER_CACHE, userId, aof);

					userInfo = wechatService.getUserInfoByAccessTokenAndOpenid(access_token_refresh, openid_refresh);
				}
				result.setStatus(HttpStatus.SUCCESS.status);
				result.setData(userInfo);
			}
		} catch (Exception e) {
			log.error("加載用戶信息异常: {}", e);
		}
		return result;
	}

	/**
	 * @Description 授权回调获取用户信息
	 */
	@GetMapping(value = "getUserInfoAfterAuth")
	public void getUserInfoAfterAuth(HttpServletRequest request, HttpServletResponse response) {

		// 用戶授權后獲取code
		String code = request.getParameter("code");
		log.info("code: {}", code);

		String redirectUrl = request.getParameter("state");

		// 生成用户临时id，作为用户是否处于登陆状态的判断
		String userId = UUID.randomUUID().toString().trim().replaceAll("-", "");
		try {
			// 通過code獲取access_token和openid
			Map<String, String> openidAndAccessToken = wechatService.getOpenidAndAccessTokenByCode(code);
			String access_token = openidAndAccessToken.get("access_token");
			String openid = openidAndAccessToken.get("openid");
			String refresh_token = openidAndAccessToken.get("refresh_token");
			log.info("通過code獲取的 access_token: {}, openid: {},  refresh_token: {}, 已存入session", access_token, openid, refresh_token);

			// 將最新access_token和openid、refresh_token存入ehcache
			HashMap<String, String> aof = new HashMap<>();
			aof.put("access_token", access_token);
			aof.put("openid", openid);
			aof.put("refresh_token", refresh_token);
			ehCacheUtil.set(USER_CACHE, userId, aof);

			CookieUtil.setCookie(response, USER_ID, userId);
			log.info("存入cookie的userId為: {}", userId);

			log.info("授权回调获取用户信息后跳转的地址: {}", redirectUrl);
			response.sendRedirect(redirectUrl);
		} catch (Exception e) {
			log.error("授权回调获取用户信息异常: {}", e);
		}
	}

}
