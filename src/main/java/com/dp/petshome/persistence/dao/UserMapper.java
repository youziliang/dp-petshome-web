package com.dp.petshome.persistence.dao;

import org.apache.ibatis.annotations.Param;

import com.dp.petshome.persistence.model.User;

public interface UserMapper {
	int insert(User record);

	int insertSelective(User record);

	User selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(User record);

	int updateByPrimaryKey(User record);

	User selectByOpenid(@Param("openid") String openid);

	int updateByOpenidSelective(User user);

	int updateScoreAndBalanceByOpenid(@Param("openid") String openid, @Param("balance") Integer balance, @Param("score") Integer score);
}