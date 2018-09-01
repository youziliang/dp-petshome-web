package com.dp.petshome.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dp.petshome.enums.HttpStatus;
import com.dp.petshome.persistence.dto.HttpResult;
import com.dp.petshome.service.HomeService;

/**
 * @Dsecription 主頁Controller
 * @author DU
 */
@Controller
@RequestMapping("home")
public class HomeController {

	private static final Logger log = LoggerFactory.getLogger(HomeController.class);

	@Autowired
	protected HomeService homeService;

	/**
	 * @Description 獲取主頁信息
	 */
	@GetMapping(value = "loadHomeInfo")
	@ResponseBody
	public HttpResult<Map<String, Object>> loadHomeInfo(HttpServletRequest request, HttpServletResponse response) {

		HttpResult<Map<String, Object>> result = new HttpResult<>();
		Map<String, Object> homeInfo = new HashMap<>(1);
		try {
			homeInfo = homeService.loadHomeInfo();

			log.info("獲取主頁信息结果: {}", homeInfo.toString());
			result.setStatus(HttpStatus.SUCCESS.status);
			result.setData(homeInfo);
		} catch (Exception e) {
			log.error("獲取主頁信息异常: {}", e);
			result.setStatus(HttpStatus.EXCEPTION.status);
		}
		return result;
	}

}
