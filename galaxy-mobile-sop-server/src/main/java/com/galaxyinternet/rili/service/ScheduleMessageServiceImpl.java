package com.galaxyinternet.rili.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.galaxyinternet.framework.core.dao.BaseDao;
import com.galaxyinternet.framework.core.service.impl.BaseServiceImpl;
import com.galaxyinternet.rili.dao.ScheduleMessageDao;
import com.galaxyinternet.rili.model.ScheduleMessage;


@Service("com.galaxyinternet.rili.service.ScheduleMessageService")
public class ScheduleMessageServiceImpl extends BaseServiceImpl<ScheduleMessage> implements ScheduleMessageService{

	@Autowired
	private ScheduleMessageDao scheduleMessageDao;
	
	@Override
	protected BaseDao<ScheduleMessage, Long> getBaseDao() {
		return this.scheduleMessageDao;
	}

	
	
	
	
}
