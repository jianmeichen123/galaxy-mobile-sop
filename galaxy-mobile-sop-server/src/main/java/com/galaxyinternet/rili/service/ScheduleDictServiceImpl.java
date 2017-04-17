package com.galaxyinternet.rili.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.galaxyinternet.framework.core.dao.BaseDao;
import com.galaxyinternet.framework.core.service.impl.BaseServiceImpl;
import com.galaxyinternet.rili.dao.ScheduleDictDao;
import com.galaxyinternet.rili.model.ScheduleDict;


@Service("com.galaxyinternet.rili.service.ScheduleDictService")
public class ScheduleDictServiceImpl extends BaseServiceImpl<ScheduleDict> implements ScheduleDictService{

	@Autowired
	private ScheduleDictDao scheduleDictDao;
	
	@Override
	protected BaseDao<ScheduleDict, Long> getBaseDao() {
		return this.scheduleDictDao;
	}

	
	
	
	
}
