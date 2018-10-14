package com.dp.petshome.web;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
import com.dp.petshome.persistence.model.Activity;
import com.dp.petshome.persistence.model.Broadcast;
import com.dp.petshome.service.ActivityService;
import com.dp.petshome.service.BroadcastService;
import com.dp.petshome.utils.DateUtil;
import com.dp.petshome.utils.FormatUtil;
import com.dp.petshome.utils.ImageUtil;
import com.dp.petshome.utils.PropertyUtil;

/**
 * @Dsecription 活動Controller
 * @author DU
 */
@Controller
@RequestMapping("publish")
public class PublishController {

	private static final Logger log = LoggerFactory.getLogger(PublishController.class);

	@Autowired
	protected ActivityService activityService;

	@Autowired
	protected BroadcastService broadcastService;

	private static final String FILE_PATH = PropertyUtil.getProperty("path.img", "/www/server/dp-petshome-web/img") + "/";

	/**
	 * @Dsecription 發佈活動消息
	 */
	@PostMapping(value = "publishActivity")
	@ResponseBody
	public HttpResult<Object> publishActivity(HttpServletRequest request, HttpServletResponse response) {

		HttpResult<Object> result = new HttpResult<Object>();

		String theme = request.getParameter("activity_theme");
		String startTime = request.getParameter("start_time");
		String endTime = request.getParameter("end_time");
		String address = request.getParameter("activity_address");
		String richText = request.getParameter("richText");
		log.info("活动主题: {}, 活动时间: {}, 活动地址: {}, 活动详情: {}", theme, startTime + "~" + endTime, address, richText);

		Activity activity = new Activity();
		StringBuffer urls = null;
		try {
			// 解析並替換base64碼
			String[] srcs = StringUtils.substringsBetween(richText, "src=\"", "\"");
			if (null != srcs) {
				urls = new StringBuffer();
				for (int i = 0; i < srcs.length; i++) {
					String[] temp = StringUtils.split(srcs[i], ",");
					richText = StringUtils.replace(richText, srcs[i], "placeholder");
					String fileName = StringUtils.remove(UUID.randomUUID().toString(), "-") + StringUtils.substringsBetween(richText, "data-filename=\"", "\"")[0];
					String url = PropertyUtil.getProperty("domain.img", "http://www.whatu1.com/img") + "/thumb_" + fileName;
					urls = i == srcs.length - 1 ? urls.append(url) : urls.append(url).append(",");
					richText = StringUtils.replace(richText, "placeholder", url);
					// base64流轉圖片
					Map<Integer, Map<String, Object>> params = new HashMap<>();
					Map<String, Object> param = new HashMap<>();
					param.put(ImageUtil.RATIO, 0.6);
					params.put(ImageUtil.SCALE, param);
					final File file = FormatUtil.base64ToFile(temp[1], FILE_PATH, fileName, params);

					// 创建压缩文件
					new Thread(new Runnable() {
						@Override
						public void run() {
							ImageUtil.createThumb(file, params);
						}
					}).start();
				}
				// 刪除活動的時候要用到img字段
				activity.setImg(urls.toString());
			}
			log.info("最终的richText: {}", richText);

			activity.setTheme(theme);
			activity.setStartTime(DateUtil.dateStrToTimestamp(startTime, DateUtil.LONGFMT16));
			activity.setEndTime(DateUtil.dateStrToTimestamp(endTime, DateUtil.LONGFMT16));
			activity.setAddress(address);
			// 第一句话（第一个句号或换行为止）将作为首页展示的副标题
			String deputy = "活动详情~ " + StringUtils.substringsBetween(richText, ">", "<")[0];
			activity.setDeputy(deputy);
			activity.setDetail(richText);
			// 初始活动默认黄珏已报名参加
			activity.setSignUp("2");

			Integer publishActivityResult = activityService.publishActivity(activity);
			if (0 < publishActivityResult) {
				log.info("新增活動成功: {}", publishActivityResult);
				result.setStatus(HttpStatus.SUCCESS.status);
			} else {
				log.info("新增活動失敗: {}", publishActivityResult);
				result.setStatus(HttpStatus.FAIL.status);
			}
		} catch (Exception e) {
			log.error("發佈活動消息异常: {}", e);
			result.setStatus(HttpStatus.EXCEPTION.status);
		}
		return result;
	}

	/**
	 * @Dsecription 發佈通知
	 */
	@PostMapping(value = "publishBroadcast")
	@ResponseBody
	public HttpResult<Object> publishBroadcast(HttpServletRequest request, HttpServletResponse response) {

		HttpResult<Object> result = new HttpResult<Object>();

		Broadcast broadcast = new Broadcast();
		String notice = request.getParameter("notice");
		broadcast.setNotice(notice);

		Integer publishBroadcastResult = broadcastService.publishBroadcast(broadcast);
		if (0 < publishBroadcastResult) {
			log.info("發佈通知成功: {}", publishBroadcastResult);
			result.setStatus(HttpStatus.SUCCESS.status);
		} else {
			log.info("發佈通知失敗: {}", publishBroadcastResult);
			result.setStatus(HttpStatus.FAIL.status);
		}
		return result;
	}

}
