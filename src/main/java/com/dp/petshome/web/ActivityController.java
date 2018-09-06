package com.dp.petshome.web;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
		String activityId = request.getParameter("activityId");
		try {
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
			log.info("活動: " + activityId + "报名活动结果: {}", signUpResult);
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

}
