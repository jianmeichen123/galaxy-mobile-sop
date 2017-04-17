package com.galaxyinternet.rili.dao;

import java.util.List;

import com.galaxyinternet.framework.core.dao.BaseDao;
import com.galaxyinternet.rili.model.ScheduleContacts;
import com.galaxyinternet.rili.model.SchedulePersonPlan;

public interface SchedulePersonPlanDao extends BaseDao<SchedulePersonPlan, Long>{
	
	
	/**
	 * 查询出 联系人的id 
	 * @param schedulePersonPlan
	 * @return
	 */
	public List<Long> selectCountsId(SchedulePersonPlan schedulePersonPlan);
	
	
	
	/**
	 * 根据拜访id查询出拜访人
	 * @param schedulePersonPlan
	 * @return
	 */
	public List<ScheduleContacts> queryByCountsId(SchedulePersonPlan schedulePersonPlan);

}
