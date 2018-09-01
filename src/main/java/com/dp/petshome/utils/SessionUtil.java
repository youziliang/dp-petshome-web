package com.dp.petshome.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @Dsecription Session 工具类
 *
 */
public final class SessionUtil {

	/**
	 * @Dsecription 获取sessionId
	 * 
	 * @param request
	 */
	public static Object getSessionId(HttpServletRequest request) {
		return request.getSession(true) == null ? null : request.getSession(true).getId();
	}

	/**
	 * @Dsecription 设置session的值
	 * 
	 * @param request
	 * @param key
	 * @param value
	 */
	public static void setSession(HttpServletRequest request, String key, Object value) {
		request.getSession(true).setAttribute(key, value);
	}

	/**
	 * @Dsecription 获取session的值
	 * 
	 * @param request
	 * @param key
	 */
	public static Object getSession(HttpServletRequest request, String key) {
		return request.getSession(true) == null ? null : request.getSession(true).getAttribute(key);
	}

	/**
	 * @Dsecription 删除Session值
	 * 
	 * @param request
	 * @param key
	 */
	public static void removeSession(HttpServletRequest request, String key) {
		request.getSession(true).removeAttribute(key);
	}

}
