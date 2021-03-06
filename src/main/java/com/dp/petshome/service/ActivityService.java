package com.dp.petshome.service;

import com.dp.petshome.persistence.model.Activity;

/**
 * @Dsecription 活動Service
 * @author DU
 */
public interface ActivityService {

	Integer publishActivity(Activity activity);

	Activity getActivityInfoById(Integer id);

	String getSignUpUsersById(Integer id);

	int signUp(Integer activityId, Integer userId);

	Activity getActivityById(Integer valueOf);

}
