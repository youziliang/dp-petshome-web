package com.dp.petshome.persistence.dao;

import com.dp.petshome.persistence.model.Broadcasting;

public interface BroadcastingMapper {
    int insert(Broadcasting record);

    int insertSelective(Broadcasting record);

    Broadcasting selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Broadcasting record);

    int updateByPrimaryKey(Broadcasting record);

	Broadcasting selectByLastest();
}