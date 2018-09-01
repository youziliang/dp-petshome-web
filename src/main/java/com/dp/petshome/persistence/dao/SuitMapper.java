package com.dp.petshome.persistence.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.dp.petshome.persistence.model.Suit;

public interface SuitMapper {
	int insert(Suit record);

	int insertSelective(Suit record);

	Suit selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(Suit record);

	int updateByPrimaryKey(Suit record);

	List<Suit> selectByPeople(@Param("people") Integer people);

	String selectNameByPrimaryKey(Integer id);

}