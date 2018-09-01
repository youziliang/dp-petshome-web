package com.dp.petshome.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Dsecription 测试服Controller
 * @author DU
 */
@Controller
@RequestMapping("petshome")
public class TestController {

	private static final Logger log = LoggerFactory.getLogger(TestController.class);

	/**
	 * produces属性：表示可接收的数据格式
	 */
	@PostMapping(value = "insertUser")
	@ResponseBody
	public Map<String, String> insertUser(HttpServletRequest request, HttpServletResponse response) {

		response.setHeader("Access-Control-Allow-Origin", "*");

		Map<String, String> map = new HashMap<>(1);
		try {
			map.put("result", "Post新增用户成功");
			log.info("新增用户信息结果: {}", "成功");
		} catch (Exception e) {
			log.error("新增用户异常: {}", e.getMessage());
		}
		return map;
	}

	@GetMapping(value = "selectUsers")
	@ResponseBody
	public Map<String, String> getInfo(HttpServletRequest request, HttpServletResponse response) {

		response.setHeader("Access-Control-Allow-Origin", "*");

		Map<String, String> map = new HashMap<>(1);
		try {
			map.put("result", "Get查询用户成功");
			log.info("查询用户列表结果: {}", "成功");
		} catch (Exception e) {
			log.error("查询用户异常: {}", e.getMessage());
		}
		return map;
	}

}
