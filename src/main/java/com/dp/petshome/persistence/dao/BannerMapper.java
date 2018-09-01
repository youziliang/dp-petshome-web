package com.dp.petshome.persistence.dao;

import java.util.List;

import com.dp.petshome.persistence.model.Banner;

public interface BannerMapper {
    int insert(Banner record);

    int insertSelective(Banner record);

    Banner selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Banner record);

    int updateByPrimaryKey(Banner record);

	List<Banner> selectByCreateTimeDesc();
}