package com.dp.petshome.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import com.dp.petshome.persistence.model.User;
import com.dp.petshome.service.ActivityService;
import com.dp.petshome.service.UserService;
import com.dp.petshome.utils.CookieUtil;
import com.dp.petshome.utils.EhCacheUtil;

/**
 * @Dsecription 活动Controller
 * @author DU
 */
@Controller
@RequestMapping("activity")
public class ActivityController {

	private static final Logger log = LoggerFactory.getLogger(ActivityController.class);
	private static final String USER_CACHE = "userCache";
	private static final String USER_ID = "userId";

	@Autowired
	protected ActivityService activityService;

	@Autowired
	protected EhCacheUtil ehCacheUtil;

	@Autowired
	protected UserService userService;

	/**
	 * @Description 报名活动
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value = "signUp")
	@ResponseBody
	public HttpResult<Object> signUp(HttpServletRequest request, HttpServletResponse response) {

		HttpResult<Object> result = new HttpResult<>();
		try {
			String activityId = request.getParameter("activityId");

			String tempUserId = CookieUtil.getCookie(request, USER_ID);
			HashMap<String, String> importances = (HashMap<String, String>) ehCacheUtil.get(USER_CACHE, tempUserId);
			String openid = importances.get("openid");
			User user = userService.getUserByOpenid(openid);
			Integer userId = user.getId();

			String signUp = activityService.getSignUpUserById(Integer.valueOf(activityId));
			String[] userIds = StringUtils.split(signUp, ",");
			for (String uid : userIds) {
				if (userId.intValue() == Integer.valueOf(uid).intValue()) {
					result.setStatus(HttpStatus.HAVEDONE.status);
					return result;
				}
			}
			int signUpResult = activityService.signUp(Integer.valueOf(activityId), userId);
			log.info("活動: {} 的报名活动结果: {}", activityId, signUpResult);
			if (signUpResult > 0) {
				result.setStatus(HttpStatus.SUCCESS.status);
			} else {
				result.setStatus(HttpStatus.FAIL.status);
			}
		} catch (Exception e) {
			log.error("报名活动异常: {}", e);
			result.setStatus(HttpStatus.EXCEPTION.status);
		}
		return result;
	}

	/**
	 * @Description 加載已報名用戶
	 */
	@SuppressWarnings("unchecked")
	@GetMapping(value = "loadSignUpUsers")
	@ResponseBody
	public HttpResult<Object> loadSignUpUsers(HttpServletRequest request, HttpServletResponse response) {

		HttpResult<Object> result = new HttpResult<>();
		List<User> list = new ArrayList<>();
		try {
			String tempUserId = CookieUtil.getCookie(request, USER_ID);
			HashMap<String, String> importances = (HashMap<String, String>) ehCacheUtil.get(USER_CACHE, tempUserId);

			String activityId = request.getParameter("activityId");
			String signUp = activityService.getSignUpUserById(Integer.valueOf(activityId));
			String[] userIds = StringUtils.split(signUp, ",");

			if (null != importances && 0 == userService.getUserByOpenid(importances.get("openid")).getRole()) {
				// 登陸狀態并且是管理員管理員可以看到全手機號
				for (String userId : userIds) {
					User signUpUser = userService.getUserInfo(Integer.valueOf(userId));
					list.add(signUpUser);
				}
			} else {
				// 未登錄狀態或非管理員只能看到部分手機號
				for (String userId : userIds) {
					User signUpUser = userService.getUserInfo(Integer.valueOf(userId));
					String tel = signUpUser.getTel();
					StringBuffer sb = new StringBuffer();
					String telHidden = sb.append(StringUtils.substring(tel, 0, 3)).append("****").append(StringUtils.substring(tel, 7, 11)).toString();
					signUpUser.setTel(telHidden);
					list.add(signUpUser);
				}
			}
			log.info("活動: {} 的报名用户列表: {}", activityId, list);
			result.setStatus(HttpStatus.SUCCESS.status);
			result.setData(list);
		} catch (Exception e) {
			log.error("报名活动异常: {}", e);
			result.setStatus(HttpStatus.EXCEPTION.status);
		}
		return result;
	}

}
