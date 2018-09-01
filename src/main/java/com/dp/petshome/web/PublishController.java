package com.dp.petshome.web;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.dp.petshome.enums.HttpStatus;
import com.dp.petshome.persistence.dto.HttpResult;
import com.dp.petshome.persistence.model.Activity;
import com.dp.petshome.persistence.model.Broadcast;
import com.dp.petshome.service.ActivityService;
import com.dp.petshome.service.BroadcastService;
import com.dp.petshome.utils.DateUtil;
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

	private static final String UPLOAD_ROOT_URL = PropertyUtil.getProperty("domain", "http://www.whatu1.com") + "/img/";
	// private static final String UPLOAD_ROOT_URL = "http://192.168.0.106/dp-img/";

	/**
	 * @Dsecription 發佈活動消息
	 */
	@PostMapping(value = "publishActivity")
	@ResponseBody
	public HttpResult<Object> publishActivity(@RequestParam(value = "img_file", required = false) MultipartFile imgFile, @RequestParam(value = "activity_theme") String theme,
			@RequestParam(value = "pickdate") String date, @RequestParam(value = "picktime") String time, @RequestParam(value = "activity_address") String address,
			@RequestParam(value = "activity_detail") String detail, HttpServletRequest request, HttpServletResponse response) {

		HttpResult<Object> result = new HttpResult<Object>();

		Activity activity = new Activity();

		String fullPath = "";
		try {
			// 先判断文件是否为空
			if (!imgFile.isEmpty()) {

				// 获得原始文件名
				String fileName = imgFile.getOriginalFilename();
				long fileSize = imgFile.getSize();
				log.info("上傳活動圖片 原始文件名: {}, 文件大小: {}", fileName, fileSize);

				// 重命名文件
				String newFileName = System.currentTimeMillis() + fileName.toLowerCase();
				log.info("上傳活動圖片 新文件名: {}", newFileName);

				// 获得物理路径webapps所在路径
				String rootPath = "/www/server/dp-petshome-web/img/";
				fullPath = rootPath + newFileName;
				log.info("文件 {} 上傳的完整路徑: {} ", newFileName, fullPath);

				// 创建文件实例
				File tempFile = new File(fullPath);
				// 判断父级目录是否存在，不存在则创建
				if (!tempFile.getParentFile().exists()) {
					tempFile.getParentFile().mkdir();
				}
				// 判断文件是否存在，否则创建文件（夹）
				if (!tempFile.exists()) {
					tempFile.mkdir();
				}
				// 将接收的文件保存到指定文件中
				imgFile.transferTo(tempFile);
				activity.setImg(UPLOAD_ROOT_URL + newFileName);
			}
			activity.setTheme(theme);

			StringBuffer sb = new StringBuffer();
			String datetime = sb.append(date).append(" ").append(time).toString();
			activity.setDate(DateUtil.dateStrToTimestamp(datetime, DateUtil.LONGFMT16));
			activity.setAddress(address);
			activity.setDetail(detail);
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
