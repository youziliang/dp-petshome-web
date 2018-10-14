package com.dp.petshome.persistence.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.dp.petshome.persistence.model.Activity;

public interface ActivityMapper {
    int insert(Activity record);

    int insertSelective(Activity record);

    Activity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Activity record);

    int updateByPrimaryKey(Activity record);
    
	List<Activity> selectByNoPerformance();

	String selectSignUpUserByPrimaryKey(Integer id);

	int updateSignUpByPrimaryKey(@Param("id") Integer id, @Param("userId") Integer userId);
}