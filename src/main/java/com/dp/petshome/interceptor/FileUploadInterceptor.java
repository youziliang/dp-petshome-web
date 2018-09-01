package com.dp.petshome.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * @Dsecription 文件上传攔截器
 * @author DU
 * TODO 未测试
 */
public class FileUploadInterceptor implements HandlerInterceptor {

	private static final Logger log = LoggerFactory.getLogger(FileUploadInterceptor.class);
	
	private long maxSize;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (null != request && ServletFileUpload.isMultipartContent(request)) {
			ServletRequestContext ctx = new ServletRequestContext(request);
			long requestSize = ctx.contentLength();
			log.info("文件上传攔截器拦截的文件大小: {}", requestSize);
			if (requestSize > maxSize) {
				throw new MaxUploadSizeExceededException(maxSize);
			}
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
	}

	public void setMaxSize(long maxSize) {
		this.maxSize = maxSize;
	}
}