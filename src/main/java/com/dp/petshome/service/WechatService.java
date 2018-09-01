package com.dp.petshome.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Dsecription 微信Service
 * @author DU
 */
public interface WechatService {

	String getAuthorizationUrl(String silenceScope, String pageName, String encode);

	Map<String, String> getOpenidAndAccessTokenByCode(String code);

	Map<String, Object> getUserInfoByAccessTokenAndOpenid(String access_token, String openid);

	Boolean isAccessTokenAvailable(String access_token, String openid);

	String getAccessToken(HttpServletRequest request, HttpServletResponse response);

	Map<String, String> refreshAccessToken(String appid, String refresh_token);

}
