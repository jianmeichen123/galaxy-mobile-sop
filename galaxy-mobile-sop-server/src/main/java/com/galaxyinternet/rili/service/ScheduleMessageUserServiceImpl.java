package com.galaxyinternet.rili.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.galaxyinternet.framework.core.dao.BaseDao;
import com.galaxyinternet.framework.core.service.impl.BaseServiceImpl;
import com.galaxyinternet.rili.dao.ScheduleMessageUserDao;
import com.galaxyinternet.rili.model.ScheduleMessageUser;


@Service("com.galaxyinternet.rili.service.ScheduleMessageUserService")
public class ScheduleMessageUserServiceImpl extends BaseServiceImpl<ScheduleMessageUser> 
	implements ScheduleMessageUserService{

	@Autowired
	private ScheduleMessageUserDao scheduleMessageUserDao;
	
	@Override
	protected BaseDao<ScheduleMessageUser, Long> getBaseDao() {
		return this.scheduleMessageUserDao;
	}

	
	
	
	
}
