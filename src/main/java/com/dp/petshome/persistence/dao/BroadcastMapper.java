package com.dp.petshome.persistence.dao;

import com.dp.petshome.persistence.model.Broadcast;

public interface BroadcastMapper {
    int insert(Broadcast record);

    int insertSelective(Broadcast record);

    Broadcast selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Broadcast record);

    int updateByPrimaryKey(Broadcast record);

	Broadcast selectByLastest();
}