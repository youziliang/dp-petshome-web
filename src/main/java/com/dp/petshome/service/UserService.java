package com.dp.petshome.service;

import com.dp.petshome.persistence.model.User;

/**
 * @Dsecription 用户Service
 * @author DU
 */
public interface UserService {

	User getUserInfo(Integer id);

	Integer insertUser(User user);

	User getUserByOpenid(String openid);

	Integer updateUser(User user);

	Boolean isAdmin(User user);

	Integer addTelByOpenid(User user);

}
