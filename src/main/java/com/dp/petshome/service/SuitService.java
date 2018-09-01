package com.dp.petshome.service;

import java.util.List;

import com.dp.petshome.persistence.model.Suit;

/**
 * @Dsecription 套餐Service
 * @author DU
 */
public interface SuitService {

	List<Suit> getSuitByPeople(Integer people);

	Suit getSuitBySuitId(Integer valueOf);

}
