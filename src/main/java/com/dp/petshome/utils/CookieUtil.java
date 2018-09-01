package com.dp.petshome.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Dsecription Cookie工具类
 * 
 */
public class CookieUtil {

	private CookieUtil() {
	}

	/**
	 * @Dsecription 添加cookie
	 * 
	 * @param response
	 * @param name
	 * @param value
	 */
	public static void setCookie(HttpServletResponse response, String name, String value) {
		Cookie cookie = new Cookie(name, value);
		cookie.setPath("/");
		cookie.setMaxAge(60 * 60 * 24 * 10); // 单位为 秒
		response.addCookie(cookie);
	}

	/**
	 * @Dsecription 添加cookie
	 * 
	 * @param response
	 * @param name
	 * @param value
	 * @param maxAge
	 */
	public static void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
		Cookie cookie = new Cookie(name, value);
		cookie.setPath("/");
		if (maxAge > 0) {
			cookie.setMaxAge(maxAge); // 单位为 秒
		}
		response.addCookie(cookie);
	}

	/**
	 * @Dsecription 删除cookie
	 * 
	 * @param response
	 * @param name
	 */
	public static void removeCookie(HttpServletResponse response, String name) {
		Cookie uid = new Cookie(name, null);
		uid.setPath("/");
		uid.setMaxAge(0);
		response.addCookie(uid);
	}

	/**
	 * @Dsecription 获取cookie值
	 * 
	 * @param request
	 * @return
	 */
	public static String getCookie(HttpServletRequest request, String cookieName) {
		Cookie[] cookies = request.getCookies();
		if (null != cookies) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(cookieName)) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}
}
