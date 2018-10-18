package com.dp.petshome.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dp.petshome.persistence.dao.RecordMapper;
import com.dp.petshome.persistence.model.Record;
import com.dp.petshome.service.RecordService;

/**
 * @Dsecription 操作记录ServiceImpl
 * @author DU
 */
@Service
public class RecordServiceImpl implements RecordService {
	
	@Autowired
	protected RecordMapper recordMapper;

	@Override
	public int insertRecord(Record record) {
		return recordMapper.insertSelective(record);
	}

}
