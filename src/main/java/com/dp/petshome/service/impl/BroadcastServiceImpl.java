package com.dp.petshome.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dp.petshome.persistence.dao.BroadcastMapper;
import com.dp.petshome.persistence.model.Broadcast;
import com.dp.petshome.service.BroadcastService;

@Service
public class BroadcastServiceImpl implements BroadcastService {

	@Autowired
	protected BroadcastMapper broadcastMapper;

	@Override
	public Integer publishBroadcast(Broadcast broadcast) {
		return broadcastMapper.insertSelective(broadcast);
	}

}
