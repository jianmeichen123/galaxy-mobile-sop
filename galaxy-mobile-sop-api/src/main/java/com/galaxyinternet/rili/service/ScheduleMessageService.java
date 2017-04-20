package com.galaxyinternet.rili.service;


import java.util.LinkedHashMap;
import java.util.List;

import com.galaxyinternet.framework.core.model.PageRequest;
import com.galaxyinternet.framework.core.service.BaseService;
import com.galaxyinternet.rili.model.ScheduleMessage;
import com.galaxyinternet.rili.model.ScheduleMessageUser;


public interface ScheduleMessageService extends BaseService<ScheduleMessage>{


	/**
	 * 个人消息 列表查询
     */
	List<ScheduleMessageUser> queryPerMessAndConvertList(ScheduleMessageUser query, PageRequest pageable);
	
	/**
	 * 消息   查询   当天需要推送的消息
     */
	LinkedHashMap<Long,ScheduleMessage> queryTodayMessToSend();
	

	void saveMessageByInfo(Object scheduleInfo);

	
}
