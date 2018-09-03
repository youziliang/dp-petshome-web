package com.dp.petshome.interceptor;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * @Dsecription 參數校驗攔截器
 * @author DU
 */
public class ValidInterceptor implements HandlerInterceptor {

	private static final Logger log = LoggerFactory.getLogger(ValidInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		try {
			String servletPath = request.getServletPath();

			Enumeration<String> parameterNames = request.getParameterNames();
			while (parameterNames.hasMoreElements()) {
				String name = parameterNames.nextElement();
				String value = request.getParameter(name);
				log.info("【參數校驗攔截器】請求: {} , 接收到的參數  key : {}, value : {}", servletPath, name, value);
			}
			return true;
		} catch (Exception e) {
			log.info("參數校驗攔截器異常: {}", e);
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
