package com.dp.petshome.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dp.petshome.persistence.dao.ActivityMapper;
import com.dp.petshome.persistence.model.Activity;
import com.dp.petshome.service.ActivityService;

@Service
public class ActivityServiceImpl implements ActivityService {

	@Autowired
	protected ActivityMapper activityMapper;

	@Override
	public Integer publishActivity(Activity activity) {
		return activityMapper.insert(activity);
	}

	@Override
	public Activity getActivityInfoById(Integer id) {
		return activityMapper.selectByPrimaryKey(id);
	}

}
