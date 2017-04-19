package com.galaxyinternet.rili.service;


import java.util.List;

import com.galaxyinternet.framework.core.service.BaseService;
import com.galaxyinternet.rili.model.ScheduleInfo;
import com.galaxyinternet.rili.util.ScheduleUtil;


public interface ScheduleInfoService extends BaseService<ScheduleInfo>{

	/**
	 * 查询 , 封装数据 ，返回列表集合
	 */
	public List<ScheduleUtil> queryAndConvertList(ScheduleInfo query)  throws Exception ;
	
	/** 
	 *  验证要保存的日程是否有冲突的日程
	 *  @param   query.getCreatedId()  query.getStartTime()   query.getEndTime()
	 *  @return  日程“XXX”  /   2个日程
	 */
	String getCconflictSchedule(ScheduleInfo query);
	
	/**
	 * 添加日程-会议
	 */
	//public Long saveMeetSchedule(Object objUser,ScheduleInfo scheduleInfo);


	/**
	 * 添加日程-拜访计划的
	 */
//	public Long savePlanSchedule(Object objUser,ScheduleInfo scheduleInfo);


	
	/**
	 * 
	 * 拜访的删除
	 */
//	public void delePlanSchedule(ScheduleInfo scheduleInfo);
	
	
	/**
	 * 拜访的更新
	 */
	//public void updatePlanSchedule(Object objUser,ScheduleInfo scheduleInfo);
	
	
	
	
}
