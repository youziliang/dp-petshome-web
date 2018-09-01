package com.dp.petshome.web;

import java.util.List;

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
import com.dp.petshome.persistence.model.Suit;
import com.dp.petshome.service.SuitService;

/**
 * @Dsecription 套餐Controller
 * @author DU
 */
@Controller
@RequestMapping("suit")
public class SuitController {

	private static final Logger log = LoggerFactory.getLogger(SuitController.class);

	@Autowired
	protected SuitService suitService;

	/**
	 * @Description 獲取套餐詳情
	 */
	@GetMapping(value = "getSuitsByPeople")
	@ResponseBody
	public HttpResult<List<Suit>> getSuitsByPeople(HttpServletRequest request, HttpServletResponse response) {

		HttpResult<List<Suit>> result = new HttpResult<>();

		String people = request.getParameter("people");
		try {
			List<Suit> suits = suitService.getSuitByPeople(Integer.valueOf(people));
			log.info("獲取套餐詳情结果: {}", suits);
			result.setStatus(HttpStatus.SUCCESS.status);
			result.setData(suits);
		} catch (Exception e) {
			log.error("獲取套餐詳情异常: {}", e);
			result.setStatus(HttpStatus.EXCEPTION.status);
		}
		return result;
	}

}
