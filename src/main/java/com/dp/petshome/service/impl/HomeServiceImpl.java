package com.dp.petshome.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dp.petshome.persistence.dao.ActivityMapper;
import com.dp.petshome.persistence.dao.BannerMapper;
import com.dp.petshome.persistence.dao.BroadcastingMapper;
import com.dp.petshome.persistence.model.Activity;
import com.dp.petshome.persistence.model.Banner;
import com.dp.petshome.persistence.model.Broadcasting;
import com.dp.petshome.service.HomeService;

/**
 * @Dsecription 主頁ServiceImpl
 * @author DU
 */
@Service
public class HomeServiceImpl implements HomeService {

	@Autowired
	protected ActivityMapper activityMapper;

	@Autowired
	protected BroadcastingMapper broadcastingMapper;

	@Autowired
	protected BannerMapper bannerMapper;

	@Override
	public Map<String, Object> loadHomeInfo() {

		Map<String, Object> map = new HashMap<>();

		List<Banner> banners = bannerMapper.selectByCreateTimeDesc();
		Broadcasting broadcasting = broadcastingMapper.selectByLastest();
		List<Activity> activities = activityMapper.selectByNoPerformance();
		if (null != banners && null != activities) {
			map.put("banners", banners);
			map.put("broadcasting", broadcasting);
			map.put("activities", activities);
		}
		return map;
	}

}
