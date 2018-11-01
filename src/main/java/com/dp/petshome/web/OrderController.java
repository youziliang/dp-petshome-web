package com.dp.petshome.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.dp.petshome.service.WechatService;
import com.dp.petshome.utils.CookieUtil;
import com.dp.petshome.utils.DateUtil;
import com.dp.petshome.utils.EhCacheUtil;
import com.dp.petshome.utils.PropertyUtil;

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

	@Autowired
	protected WechatService wechatService;

	/**
	 * @Description 新增用户预约
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
				Double balance = user.getBalance();
				if (balance < price) {
					result.setStatus(HttpStatus.NOBALANCE.status);
					result.setMsg("Insufficient Balance");
					return result;
				}
			}
			String datetime = request.getParameter("datetime");
			String people = request.getParameter("order_people");
			String remark = request.getParameter("order_remark");
			Order order = new Order();
			// 新增訂單
			order.setId(DateUtil.getNowDateStr(DateUtil.SHORTFMTD8) + (int) (Math.random() * 8999) + 1001);
			order.setOpenid(openid);
			order.setDate(DateUtil.dateStrToTimestamp(datetime, DateUtil.LONGFMT16));
			order.setCount(Integer.valueOf(people));
			order.setSuitId(Integer.valueOf(suitId));
			order.setPayment(StringUtils.equals("0", payment) ? false : true);
			order.setRemark(remark);

			int reservateResult = orderService.reservate(order);
			log.info("新增预约结果: {}", reservateResult);
			if (0 < reservateResult) {
				result.setStatus(HttpStatus.SUCCESS.status);
				result.setData(order.getId());
			} else {
				result.setStatus(HttpStatus.FAIL.status);
			}
		} catch (Exception e) {
			log.error("新增预约异常: {}", e);
			result.setStatus(HttpStatus.EXCEPTION.status);
		}
		return result;
	}

	/**
	 * @Description 获取訂單
	 */
	@SuppressWarnings("unchecked")
	@GetMapping(value = "getOrders")
	@ResponseBody
	public HttpResult<Object> getOrders(HttpServletRequest request, HttpServletResponse response) {

		HttpResult<Object> result = new HttpResult<>();

		String isFinished = request.getParameter("status");
		log.info("获取狀態為" + isFinished + "的訂單");
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
			log.error("获取訂單异常: {}", e);
			result.setStatus(HttpStatus.EXCEPTION.status);
		}
		return result;
	}

	/**
	 * @Description 取消用户预约
	 */
	@PostMapping(value = "cancel")
	@ResponseBody
	public HttpResult<Object> cancel(HttpServletRequest request, HttpServletResponse response) {

		HttpResult<Object> result = new HttpResult<>();

		String orderId = request.getParameter("orderId");
		try {
			int cancelResult = orderService.cancelByOrderId(orderId);
			log.info("预约订单: " + orderId + "取消结果: {}", cancelResult);
			if (cancelResult > 0) {
				result.setStatus(HttpStatus.SUCCESS.status);
			} else {
				result.setStatus(HttpStatus.FAIL.status);
			}
		} catch (Exception e) {
			log.error("取消预约异常: {}", e);
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
	 * @Description 预约前检查
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
		// 判断是否實名或完善手机号
		User user = userService.getUserByOpenid(openid);
		log.info("预约前检查获取的用户信息: {}", user);

		if (null == user) {
			// 首次直接進入order頁面，用户信息還未寫入數據庫（正常在進入mine頁面時寫入），此時获取一次用户信息
			String access_token = importances.get("access_token");
			String refresh_token = importances.get("refresh_token");
			log.info("缓存中的的 access_token: {}, openid: {},  refresh_token: {}", access_token, openid, refresh_token);

			if (StringUtils.isNotBlank(access_token) && StringUtils.isNotBlank(openid) && StringUtils.isNotBlank(refresh_token)) {
				// 通过緩存的access_token和openid获取用户信息
				Map<String, Object> userInfo = wechatService.getUserInfoByAccessTokenAndOpenid(access_token, openid);
				// 檢驗access_token是否還有效
				if (userInfo.containsKey("errcode")) {
					log.info("获取用户信息失败: {}", userInfo.get("errcode"));

					// 這裏暫不做具體判斷，一律按access_key過期處理
					Map<String, String> refreshMap = wechatService.refreshAccessToken(PropertyUtil.getProperty("wechat.appid"), refresh_token);
					String access_token_refresh = refreshMap.get("access_token");
					String openid_refresh = refreshMap.get("openid");
					String refresh_token_refresh = refreshMap.get("refresh_token");

					// 將刷新后的access_token和openid、refresh_token存入ehcache
					HashMap<String, String> aof = new HashMap<>();
					aof.put("access_token", access_token_refresh);
					aof.put("openid", openid_refresh);
					aof.put("refresh_token", refresh_token_refresh);
					ehCacheUtil.set(USER_CACHE, userId, aof);

					userInfo = wechatService.getUserInfoByAccessTokenAndOpenid(access_token_refresh, openid_refresh);
				}
				user = (User) userInfo.get("user");
			}
		}
		if (null != user) {
			String name = user.getName();
			String tel = user.getTel();
			log.info("预约前检查 获取用户后 用户的实名: {}, 电话: {}", name, tel);
			if (StringUtils.isBlank(name) && (null == tel || 11 != tel.length())) {
				result.setStatus(HttpStatus.FAIL.status);
				result.setMsg("AIN");
			} else if (StringUtils.isBlank(name) && (null != tel && 11 == tel.length())) {
				result.setStatus(HttpStatus.FAIL.status);
				result.setMsg("NIN");
			} else if (StringUtils.isNotBlank(name) && (null == tel || 11 != tel.length())) {
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
