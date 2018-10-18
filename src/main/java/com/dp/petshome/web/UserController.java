package com.dp.petshome.web;

import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.dp.petshome.enums.HttpStatus;
import com.dp.petshome.persistence.dto.HttpResult;
import com.dp.petshome.persistence.model.Record;
import com.dp.petshome.persistence.model.User;
import com.dp.petshome.service.RecordService;
import com.dp.petshome.service.UserService;
import com.dp.petshome.utils.CookieUtil;
import com.dp.petshome.utils.EhCacheUtil;

/**
 * @Dsecription 用戶Controller
 * @author DU
 */
@Controller
@RequestMapping("user")
public class UserController {

	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	private static final String USER_CACHE = "userCache";
	private static final String USER_ID = "userId";

	@Autowired
	protected EhCacheUtil ehCacheUtil;

	@Autowired
	protected UserService userService;

	@Autowired
	protected RecordService recordService;

	@Autowired
	protected ThreadPoolTaskExecutor threadPoolTaskExecutor;

	/**
	 * @Description 用戶登陸
	 */
	@GetMapping(value = "login")
	public void login(HttpServletRequest request, HttpServletResponse response) {

		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			log.info("獲取的請求頭信息: {}", request.getHeader(headerNames.nextElement()));
		}
		log.info("用戶登陸");
	}

	/**
	 * @Description 获取用户信息
	 */
	@GetMapping(value = "loadUser")
	@ResponseBody
	public HttpResult<User> loadUser(HttpServletRequest request, HttpServletResponse response) {

		HttpResult<User> result = new HttpResult<>();

		User userInfo = null;
		try {
			String idStr = request.getParameter("id");
			Integer id = Integer.valueOf(idStr);
			userInfo = userService.getUserInfo(id);
			log.info("获取用户信息结果: {}", userInfo);
			result.setStatus(HttpStatus.SUCCESS.status);
			result.setData(userInfo);
		} catch (Exception e) {
			log.error("获取用户信息异常: {}", e.getMessage());
			result.setStatus(HttpStatus.EXCEPTION.status);
		}
		return result;
	}

	/**
	 * @Description 完善用戶信息
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value = "addAddition")
	@ResponseBody
	public HttpResult<Object> addAddition(HttpServletRequest request, HttpServletResponse response) {

		HttpResult<Object> result = new HttpResult<>();

		String userId = CookieUtil.getCookie(request, USER_ID);

		String name = request.getParameter("name");
		String sex = request.getParameter("sex");
		String tel = request.getParameter("tel");
		// 處於登陸狀態
		HashMap<String, String> importances = (HashMap<String, String>) ehCacheUtil.get(USER_CACHE, userId);
		String openid = null != importances ? importances.get("openid") : null;
		try {
			User user = new User();
			user.setOpenid(openid);
			user.setName(name);
			user.setTel(tel);
			if (StringUtils.isNotBlank(sex)) {
				user.setSex(Integer.valueOf(sex));
			}
			Integer addTelResult = userService.addTelByOpenid(user);
			if (0 < addTelResult) {
				log.info("完善用戶信息成功");
				result.setStatus(HttpStatus.SUCCESS.status);
			} else {
				log.info("完善用戶信息失败");
				result.setStatus(HttpStatus.FAIL.status);
			}
		} catch (Exception e) {
			log.error("完善用戶信息异常: {}", e);
			result.setStatus(HttpStatus.EXCEPTION.status);
		}
		return result;
	}

	/**
	 * @Description 用户充值操作
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value = "recharge")
	@ResponseBody
	public HttpResult<Object> recharge(HttpServletRequest request, HttpServletResponse response) {

		HttpResult<Object> result = new HttpResult<>();

		String userId = CookieUtil.getCookie(request, USER_ID);
		// 處於登陸狀態
		HashMap<String, String> importances = (HashMap<String, String>) ehCacheUtil.get(USER_CACHE, userId);
		String openid = null != importances ? importances.get("openid") : null;

		HashMap<String, String> commDetail = JSON.parseObject(request.getParameter("comm_detail"), HashMap.class);
		// String orderNo = commDetail.get("order_no");
		String amount = commDetail.get("amount");
		// String timeStamp = request.getParameter("timeStamp");

		// 更新账户余额
		User user = userService.getUserByOpenid(openid);
		Double balanceBeforeRecharge = user.getBalance();
		Double balance = new BigDecimal(user.getBalance()).add(new BigDecimal(Double.valueOf(amount))).doubleValue();
		user.setBalance(balance);
		Integer rechargeResult = userService.recharge(user);
		if (0 < rechargeResult) {
			log.info("用户充值操作成功");
			result.setStatus(HttpStatus.SUCCESS.status);
		} else {
			log.info("用户充值操作失败");
			result.setStatus(HttpStatus.FAIL.status);
		}
		// 记录充值操作

		threadPoolTaskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				Record record = new Record();
				record.setUserId(user.getId());
				record.setAction(0);
				record.setRecord("{'充值前余额':" + balanceBeforeRecharge + ",'充值后余额':" + balance + "}");
				int insertResult = recordService.insertRecord(record);
				if (0 < insertResult) {
					log.info(user.getId() + "插入操作记录成功");
				} else {
					log.info(user.getId() + "插入操作记录失败");
				}

			}
		});
		return result;
	}
}
