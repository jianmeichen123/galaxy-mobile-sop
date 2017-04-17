package com.galaxyinternet.rili.service;

import java.util.List;

import com.galaxyinternet.framework.core.service.BaseService;
import com.galaxyinternet.rili.model.ScheduleContacts;
import com.galaxyinternet.rili.model.SchedulePersonPlan;

public interface SchedulePersonPlanService extends BaseService<SchedulePersonPlan>
{
	
	
	
	//查询拜访联系人的id
	public List<Long> selectCountsId(SchedulePersonPlan schedulePersonPlan);
	
	
	/**
	 * 根据拜访id查询出拜访人
	 * @param schedulePersonPlan
	 * @return
	 */
	public List<ScheduleContacts> queryByCountsId(SchedulePersonPlan schedulePersonPlan);
}
