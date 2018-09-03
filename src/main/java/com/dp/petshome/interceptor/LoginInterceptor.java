package com.dp.petshome.interceptor;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.dp.petshome.enums.HttpStatus;
import com.dp.petshome.persistence.dto.HttpResult;
import com.dp.petshome.service.WechatService;
import com.dp.petshome.utils.CookieUtil;
import com.dp.petshome.utils.EhCacheUtil;
import com.dp.petshome.utils.PropertyUtil;

/**
 * @Dsecription 登陸攔截器
 * @author DU
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

	private static final Logger log = LoggerFactory.getLogger(LoginInterceptor.class);

	// private static final String SILENCE_SCOPE = "snsapi_base";
	private static final String SCOPE = "snsapi_userinfo";
	private static final String USER_CACHE = "userCache";
	private static final String USER_ID = "userId";

	@Autowired
	protected EhCacheUtil ehCacheUtil;

	@Autowired
	protected WechatService wechatService;

	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		try {
			String referer = request.getHeader(HttpHeaders.REFERER);
			log.info("登陆拦截器拦截到的页面来源: {}", referer);
			
			String userId = CookieUtil.getCookie(request, USER_ID);
			Object importances = ehCacheUtil.get(USER_CACHE, userId);
			// 有可能出现cookie的缓存时间大于ehCache的缓存时间，所以这里再做一次判断
			String openid = null == importances ? null : ((HashMap<String, String>) importances).get("openid");

			if (StringUtils.isBlank(userId) || StringUtils.isBlank(openid)) {
				// 处于未登录状态
				log.info("登陸攔截器未拦截到用户登陆状态信息");
				HttpResult<Object> result = new HttpResult<>();
				Map<String, Object> map = new HashMap<>(1);
				String url = wechatService.getAuthorizationUrl(SCOPE, referer, URLEncoder.encode(PropertyUtil.getProperty("domain.project") + "/wechat/getUserInfoAfterAuth"));
				map.put("url", url);
				log.info("登陸攔截器返回前端授權地址: {}", url);
				result.setStatus(HttpStatus.NOLOGIN.status);
				result.setData(map);
				String resultJsonStr = JSON.toJSONString(result);
				response.getWriter().write(resultJsonStr);
				return false;
			} else {
				log.info("登陸攔截器拦截到的用户: {}", userId);
				return true;
			}
		} catch (Exception e) {
			log.info("登陸攔截器異常: {}", e);
			return false;
		}
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
	}

}
