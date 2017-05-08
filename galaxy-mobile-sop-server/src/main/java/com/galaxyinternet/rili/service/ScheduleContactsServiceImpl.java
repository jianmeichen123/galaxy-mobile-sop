package com.galaxyinternet.rili.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.galaxyinternet.framework.core.dao.BaseDao;
import com.galaxyinternet.framework.core.service.impl.BaseServiceImpl;
import com.galaxyinternet.rili.dao.ScheduleContactsDao;
import com.galaxyinternet.rili.model.ScheduleContacts;

@Service("ScheduleContactsService")
public class ScheduleContactsServiceImpl  extends BaseServiceImpl<ScheduleContacts> implements ScheduleContactsService{

	@Autowired
	private ScheduleContactsDao scheduleContactsDao;

	@Override
	protected BaseDao<ScheduleContacts, Long> getBaseDao() {
		// TODO Auto-generated method stub
		return this.scheduleContactsDao;
	}


	

	
}
