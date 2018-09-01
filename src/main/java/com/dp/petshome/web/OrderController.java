package com.dp.petshome.web;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dp.petshome.enums.HttpStatus;
import com.dp.petshome.persistence.dto.HttpResult;
import com.dp.petshome.persistence.model.Order;
import com.dp.petshome.persistence.model.Suit;
import com.dp.petshome.persistence.model.User;
import com.dp.petshome.persistence.vo.OrderVo;
import com.dp.petshome.service.OrderService;
import com.dp.petshome.service.SuitService;
import com.dp.petshome.service.UserService;
import com.dp.petshome.utils.CookieUtil;
import com.dp.petshome.utils.DateUtil;
import com.dp.petshome.utils.EhCacheUtil;

/**
 * @Dsecription 訂單Controller
 * @author DU
 */
@Controller
@RequestMapping("order")
public class OrderController {

	private static final Logger log = LoggerFactory.getLogger(OrderController.class);

	private static final String USER_CACHE = "userCache";
	private static final String USER_ID = "userId";

	@Autowired
	protected EhCacheUtil ehCacheUtil;

	@Autowired
	protected OrderService orderService;

	@Autowired
	protected SuitService suitService;

	@Autowired
	protected UserService userService;

	/**
	 * @Description 新增用戶預約
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value = "reservate")
	@ResponseBody
	public HttpResult<Object> reservate(HttpServletRequest request, HttpServletResponse response) {

		HttpResult<Object> result = new HttpResult<>();

		String userId = CookieUtil.getCookie(request, USER_ID);
		try {
			// 處於登陸狀態
			HashMap<String, String> importances = (HashMap<String, String>) ehCacheUtil.get(USER_CACHE, userId);
			String openid = null != importances ? importances.get("openid") : null;

			String payment = request.getParameter("order_payment");
			String suitId = request.getParameter("order_suit");
			if (StringUtils.equals("1", payment)) {
				// 餘額付，校驗餘額是否充足
				Suit suit = suitService.getSuitBySuitId(Integer.valueOf(suitId));
				Integer price = suit.getPrice();
				User user = userService.getUserByOpenid(openid);
				Integer balance = user.getBalance();
				if (balance < price) {
					result.setStatus(HttpStatus.NOBALANCE.status);
					result.setMsg("Insufficient Balance");
					return result;
				}
			}
			String pickdate = request.getParameter("pickdate");
			String picktime = request.getParameter("picktime");
			String people = request.getParameter("order_people");
			String remark = request.getParameter("order_remark");
			Order order = new Order();
			String[] pickdates = StringUtils.split(pickdate, " ");
			String[] picktimes = StringUtils.split(picktime, " ");
			StringBuffer sb = new StringBuffer();
			String date = sb.append(pickdates[0]).append(" ").append(picktimes[0]).toString();
			// 新增訂單
			order.setId(DateUtil.getNowDateStr(DateUtil.SHORTFMTD8) + "_" + UUID.randomUUID().toString().substring(0, 8));
			order.setOpenid(openid);
			order.setDate(DateUtil.dateStrToTimestamp(date, DateUtil.LONGFMT16));
			order.setCount(Integer.valueOf(people));
			order.setSuitId(Integer.valueOf(suitId));
			order.setPayment(Boolean.valueOf(payment));
			order.setRemark(remark);

			int reservateResult = orderService.reservate(order);
			log.info("新增預約结果: {}", reservateResult);
			if (0 < reservateResult) {
				result.setStatus(HttpStatus.SUCCESS.status);
				result.setData(order.getId());
			} else {
				result.setStatus(HttpStatus.FAIL.status);
			}
		} catch (Exception e) {
			log.error("新增預約异常: {}", e);
			result.setStatus(HttpStatus.EXCEPTION.status);
		}
		return result;
	}

	/**
	 * @Description 獲取訂單
	 */
	@SuppressWarnings("unchecked")
	@GetMapping(value = "getOrders")
	@ResponseBody
	public HttpResult<Object> getOrders(HttpServletRequest request, HttpServletResponse response) {

		HttpResult<Object> result = new HttpResult<>();

		String isFinished = request.getParameter("status");
		log.info("獲取狀態為" + isFinished + "的訂單");
		try {
			// 获取临时用户id
			String userId = CookieUtil.getCookie(request, USER_ID);
			HashMap<String, String> importances = (HashMap<String, String>) ehCacheUtil.get(USER_CACHE, userId);
			String openid = importances.get("openid");

			User user = userService.getUserByOpenid(openid);
			List<OrderVo> orders = orderService.getMyOrders(user, Integer.valueOf(isFinished));
			result.setStatus(HttpStatus.SUCCESS.status);
			result.setData(orders);
		} catch (Exception e) {
			log.error("獲取訂單异常: {}", e);
			result.setStatus(HttpStatus.EXCEPTION.status);
		}
		return result;
	}

	/**
	 * @Description 取消用戶預約
	 */
	@PostMapping(value = "cancel")
	@ResponseBody
	public HttpResult<Object> cancel(HttpServletRequest request, HttpServletResponse response) {

		HttpResult<Object> result = new HttpResult<>();

		String orderId = request.getParameter("orderId");
		try {
			int cancelResult = orderService.cancelByOrderId(orderId);
			log.info("預約订单" + orderId + "取消结果: {}", cancelResult);
			if (cancelResult > 0) {
				result.setStatus(HttpStatus.SUCCESS.status);
			} else {
				result.setStatus(HttpStatus.FAIL.status);
			}
		} catch (Exception e) {
			log.error("取消預約异常: {}", e);
			result.setStatus(HttpStatus.EXCEPTION.status);
		}
		return result;
	}

	/**
	 * @Description 订单（流转）状态更新
	 */
	@PostMapping(value = "exchange")
	@ResponseBody
	public HttpResult<Object> exchange(HttpServletRequest request, HttpServletResponse response) {

		HttpResult<Object> result = new HttpResult<>();

		String orderId = request.getParameter("orderId");
		Integer status = Integer.valueOf(request.getParameter("status"));

		Order order = new Order();
		order.setId(orderId);
		order.setStatus(status);
		try {
			int updateResult = orderService.updateOrder(order);
			log.info("更新订单" + orderId + "状态结果: {}", updateResult);
			if (updateResult > 0) {
				if (2 == status) {
					// 訂單完成時，進行積分或扣款操作
					// TODO 異步執行，重試3次（有條件就用MQ）
					orderService.adjustScoreAndBalance(orderId);
				}
				result.setStatus(HttpStatus.SUCCESS.status);
			} else {
				result.setStatus(HttpStatus.FAIL.status);
			}
		} catch (Exception e) {
			log.error("更新订单状态异常: {}", e);
			result.setStatus(HttpStatus.EXCEPTION.status);
		}
		return result;
	}

	/**
	 * @Description 預約前檢查是否登陸和是否注冊了手機號
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value = "checkBeforeReservation")
	@ResponseBody
	public HttpResult<Object> checkBeforeReservation(HttpServletRequest request, HttpServletResponse response) {

		HttpResult<Object> result = new HttpResult<>();

		String userId = CookieUtil.getCookie(request, USER_ID);

		// 處於登陸狀態
		HashMap<String, String> importances = (HashMap<String, String>) ehCacheUtil.get(USER_CACHE, userId);
		String openid = null != importances ? importances.get("openid") : null;
		// 判断是否完善手机号
		User user = userService.getUserByOpenid(openid);
		if (null != user) {
			if (StringUtils.isBlank(user.getName()) && 11 != user.getTel().length()) {
				result.setStatus(HttpStatus.FAIL.status);
				result.setMsg("AIN");
			} else if (StringUtils.isBlank(user.getName()) && 11 == user.getTel().length()) {
				result.setStatus(HttpStatus.FAIL.status);
				result.setMsg("NIN");
			} else if (StringUtils.isNotBlank(user.getName()) && 11 != user.getTel().length()) {
				result.setStatus(HttpStatus.FAIL.status);
				result.setMsg("TIN");
			} else {
				result.setStatus(HttpStatus.SUCCESS.status);
			}
		} else {
			result.setStatus(HttpStatus.FAIL.status);
		}
		return result;
	}

}
