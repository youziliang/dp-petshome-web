package com.dp.petshome.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dp.petshome.persistence.dao.SuitMapper;
import com.dp.petshome.persistence.model.Suit;
import com.dp.petshome.service.SuitService;

/**
 * @Dsecription 套餐ServiceImpl
 * @author DU
 */
@Service
public class SuitServiceImpl implements SuitService {

	@Autowired
	protected SuitMapper suitMapper;

	@Override
	public List<Suit> getSuitByPeople(Integer people) {
		return suitMapper.selectByPeople(people);
	}

	@Override
	public Suit getSuitBySuitId(Integer id) {
		return suitMapper.selectByPrimaryKey(id);
	}

}
