package com.galaxyinternet.rili.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.galaxyinternet.framework.core.dao.BaseDao;
import com.galaxyinternet.framework.core.service.impl.BaseServiceImpl;
import com.galaxyinternet.rili.dao.SchedulePersonPlanDao;
import com.galaxyinternet.rili.model.ScheduleContacts;
import com.galaxyinternet.rili.model.SchedulePersonPlan;

@Service("SchedulePersonPlanService")
public class SchedulePersonPlanServiceImpl  extends BaseServiceImpl<SchedulePersonPlan> implements SchedulePersonPlanService{

	@Autowired
	private SchedulePersonPlanDao schedulePersonPlanDao;

	@Override
	protected BaseDao<SchedulePersonPlan, Long> getBaseDao() {
		// TODO Auto-generated method stub
		return this.schedulePersonPlanDao;
	}

	/**
	 * 查询出拜访人的id
	 */
	@Override
	public List<Long> selectCountsId(SchedulePersonPlan schedulePersonPlan) {
		// TODO Auto-generated method stub
		return schedulePersonPlanDao.selectCountsId(schedulePersonPlan);
	}

	@Override
	public List<ScheduleContacts> queryByCountsId(SchedulePersonPlan schedulePersonPlan) {
		// TODO Auto-generated method stub
		return schedulePersonPlanDao.queryByCountsId(schedulePersonPlan);
	}


	

	
}
