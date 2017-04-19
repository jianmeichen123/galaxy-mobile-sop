package com.galaxyinternet.rili.service;


import java.util.List;

import com.galaxyinternet.framework.core.model.PageRequest;
import com.galaxyinternet.framework.core.service.BaseService;
import com.galaxyinternet.rili.model.ScheduleMessage;
import com.galaxyinternet.rili.model.ScheduleMessageUser;


public interface ScheduleMessageService extends BaseService<ScheduleMessage>{


	/**
	 * 查询 
	 */
	List<ScheduleMessageUser> queryAndConvertList(ScheduleMessageUser query, PageRequest pageable);
	

	void saveMessageByInfo(Object scheduleInfo);

	
}
