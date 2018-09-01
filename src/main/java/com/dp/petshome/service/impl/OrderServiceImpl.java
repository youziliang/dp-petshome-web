package com.dp.petshome.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dp.petshome.enums.OrderStatus;
import com.dp.petshome.persistence.dao.OrderMapper;
import com.dp.petshome.persistence.dao.SuitMapper;
import com.dp.petshome.persistence.dao.UserMapper;
import com.dp.petshome.persistence.model.Order;
import com.dp.petshome.persistence.model.Suit;
import com.dp.petshome.persistence.model.User;
import com.dp.petshome.persistence.vo.OrderVo;
import com.dp.petshome.service.OrderService;

/**
 * @Dsecription 訂單ServiceImpl
 * @author DU
 */
@Service
public class OrderServiceImpl implements OrderService {

	private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

	@Autowired
	protected OrderMapper orderMapper;

	@Autowired
	protected UserMapper userMapper;

	@Autowired
	protected SuitMapper suitMapper;

	@Override
	public int reservate(Order order) {
		return orderMapper.insertSelective(order);
	}

	@Override
	public List<Order> getOrdersByOpenid(String openid) {
		return orderMapper.selectByOpenid(openid);
	}

	@Override
	public int cancelByOrderId(String orderId) {
		return orderMapper.deleteByOrderId(orderId);
	}

	@Override
	public List<OrderVo> getMyOrders(User user) {
		return getMyOrders(user, null);
	}

	@Override
	public List<OrderVo> getMyOrders(User user, Integer isFinished) {
		if (null == user) {
			return null;
		}
		List<OrderVo> orderList = new ArrayList<>();
		List<Order> tempList = new ArrayList<>();
		if (OrderStatus.ALL.status == isFinished) {
			// 所有訂單
			if (OrderStatus.ALL.status == user.getRole()) {
				tempList = orderMapper.selectAll();
				log.info("我是管理员: {}, 目前需要处理的订单: {}", user.getName(), tempList);
			} else {
				tempList = orderMapper.selectByOpenid(user.getOpenid());
				log.info("我是普通用户: {}, 目前需要处理的订单: {}", user.getName(), tempList);
			}
		} else if (OrderStatus.UNFINISHED.status == isFinished) {
			// 未完成訂單
			if (0 == user.getRole()) {
				tempList = orderMapper.selectUnfinishedAll();
				log.info("我是管理员: {}, 目前需要处理的订单: {}", user.getName(), tempList);
			} else {
				tempList = orderMapper.selectUnfinishedByOpenid(user.getOpenid());
				log.info("我是普通用户: {}, 目前需要处理的订单: {}", user.getName(), tempList);
			}
		} else if (OrderStatus.FINISHED.status == isFinished) {
			// 已完成訂單
			if (0 == user.getRole()) {
				tempList = orderMapper.selectFinishedAll();
				log.info("我是管理员: {}, 目前需要处理的订单: {}", user.getName(), tempList);
			} else {
				tempList = orderMapper.selectFinishedByOpenid(user.getOpenid());
				log.info("我是普通用户: {}, 目前需要处理的订单: {}", user.getName(), tempList);
			}
		}
		for (Order order : tempList) {
			User tempUser = userMapper.selectByOpenid(order.getOpenid());
			OrderVo orderVo = new OrderVo();
			orderVo.setId(order.getId());
			orderVo.setOpenid(order.getOpenid());
			orderVo.setDate(order.getDate());
			orderVo.setCount(order.getCount());

			String suitName = suitMapper.selectNameByPrimaryKey(order.getSuitId());
			orderVo.setSuitName(suitName);
			orderVo.setRemark(order.getRemark());
			orderVo.setStatus(order.getStatus());
			orderVo.setNickname(tempUser.getNickname());
			orderVo.setTel(tempUser.getTel());
			orderVo.setRole(user.getRole());
			orderList.add(orderVo);
		}
		return orderList;
	}

	@Override
	public int updateOrder(Order order) {
		return orderMapper.updateByPrimaryKeySelective(order);
	}

	@Override
	public Integer adjustScoreAndBalance(String orderId) {
		Order order = orderMapper.selectByPrimaryKey(orderId);
		String openid = order.getOpenid();
		Integer suitid = order.getSuitId();

		Suit suit = suitMapper.selectByPrimaryKey(suitid);
		Integer price = suit.getPrice();
		Integer scoreInSuit = suit.getScore();

		User user = userMapper.selectByOpenid(openid);
		Integer balance = user.getBalance();
		Integer scoreInDB = user.getScore();

		if (false == order.getPayment()) {
			// 到店付
			price = 0;
		}
		log.info("订单: {} 类型为{}, 需要扣除{}余额, 需要增加{}积分", openid, order.getPayment(), price, scoreInSuit);
		return userMapper.updateScoreAndBalanceByOpenid(openid, balance - price, scoreInDB + scoreInSuit);
	}

}
