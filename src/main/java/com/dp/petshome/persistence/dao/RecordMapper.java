package com.dp.petshome.persistence.dao;

import com.dp.petshome.persistence.model.Record;

public interface RecordMapper {
    int insert(Record record);

    int insertSelective(Record record);

    Record selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Record record);

    int updateByPrimaryKey(Record record);
}