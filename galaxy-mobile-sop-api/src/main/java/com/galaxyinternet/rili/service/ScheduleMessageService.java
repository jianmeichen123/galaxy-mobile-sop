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
	 * 个人消息列表  设为全部已读
	 * 1.查询出 消息user 表中      个人的   可用   未读  未删除  的数据
	 * 2.查询出消息内容列表          状态为可用的消息
     */
	void perMessageToRead(Object objUser);
	
	/**
	 * 个人消息列表 清空
     */
	void perMessageToClear(Object objUser);
	
	
	/**
	 * 消息   查询   当天需要推送的消息
     */
	List<ScheduleMessage> queryTodayMessToSend();
	

	/**
	 * 新增  修改  删除（日程 、、）操作完成后
	 * 消息同步修改
	 * ScheduleMessage    ScheduleMessageUser
     */
	void operateMessageBySaveInfo(Object scheduleInfo);
	void operateMessageByDeleteInfo(Object scheduleInfo, String messageType);
	void operateMessageByUpdateInfo(Object scheduleInfo, String messageType);

	
	

	
}
