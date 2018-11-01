package com.dp.petshome.service;

import java.util.List;

import com.dp.petshome.persistence.model.Order;
import com.dp.petshome.persistence.model.User;
import com.dp.petshome.persistence.vo.OrderVo;

/**
 * @Dsecription 訂單Service
 * @author DU
 */
public interface OrderService {

	int reservate(Order order);

	List<Order> getOrdersByOpenid(String openid);

	int cancelByOrderId(String orderId);

	List<OrderVo> getMyOrders(User user);
	
	List<OrderVo> getMyOrders(User user, Integer isFinished);

	int updateOrder(Order order);

	Integer adjustScoreAndBalance(String orderId);

	Order getByOrderId(String orderId);

}
