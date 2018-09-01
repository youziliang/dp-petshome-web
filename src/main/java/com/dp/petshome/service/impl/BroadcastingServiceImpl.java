package com.dp.petshome.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dp.petshome.persistence.dao.BroadcastingMapper;
import com.dp.petshome.persistence.model.Broadcasting;
import com.dp.petshome.service.BroadcastingService;

@Service
public class BroadcastingServiceImpl implements BroadcastingService {

	@Autowired
	protected BroadcastingMapper broadcastingMapper;

	@Override
	public Integer publishBroadcasting(Broadcasting broadcasting) {
		return broadcastingMapper.insertSelective(broadcasting);
	}

}
