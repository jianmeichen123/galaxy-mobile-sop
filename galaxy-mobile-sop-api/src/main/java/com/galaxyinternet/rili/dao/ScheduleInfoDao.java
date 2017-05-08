package com.galaxyinternet.rili.dao;

import java.util.List;

import com.galaxyinternet.framework.core.dao.BaseDao;
import com.galaxyinternet.rili.model.ScheduleInfo;

public interface ScheduleInfoDao extends BaseDao<ScheduleInfo, Long>{

	List<ScheduleInfo> selectConflictSchedule(ScheduleInfo query);

}
