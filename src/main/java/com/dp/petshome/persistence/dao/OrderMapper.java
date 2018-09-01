package com.dp.petshome.persistence.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.dp.petshome.persistence.model.Order;

public interface OrderMapper {
	int insert(Order record);

	int insertSelective(Order record);

	Order selectByPrimaryKey(String id);

	int updateByPrimaryKeySelective(Order record);

	int updateByPrimaryKey(Order record);

	List<Order> selectByOpenid(@Param("openid") String openid);

	int deleteByOrderId(@Param("id") String id);

	List<Order> selectAll();

	List<Order> selectUnfinishedAll();

	List<Order> selectUnfinishedByOpenid(String openid);

	List<Order> selectFinishedAll();

	List<Order> selectFinishedByOpenid(String openid);
}