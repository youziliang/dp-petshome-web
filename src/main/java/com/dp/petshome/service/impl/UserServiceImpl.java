package com.dp.petshome.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dp.petshome.persistence.dao.UserMapper;
import com.dp.petshome.persistence.model.User;
import com.dp.petshome.service.UserService;

/**
 * @Dsecription 用户ServiceImpl
 * @author DU
 */
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	protected UserMapper userMapper;

	@Override
	public User getUserInfo(Integer id) {
		return userMapper.selectByPrimaryKey(id);
	}

	@Override
	public Integer insertUser(User user) {
		return userMapper.insertSelective(user);
	}

	@Override
	public User getUserByOpenid(String openid) {
		return userMapper.selectByOpenid(openid);
	}

	@Override
	public Integer updateUser(User user) {
		return userMapper.updateByOpenidSelective(user);
	}

	@Override
	public Boolean isAdmin(User user) {
		return 0 == user.getRole();
	}

	@Override
	public Integer addTelByOpenid(User user) {
		return userMapper.updateByOpenidSelective(user);
	}

}
