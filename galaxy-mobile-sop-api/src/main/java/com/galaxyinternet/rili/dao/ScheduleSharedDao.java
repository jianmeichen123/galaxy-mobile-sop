package com.galaxyinternet.rili.dao;

import java.util.List;

import com.galaxyinternet.framework.core.dao.BaseDao;
import com.galaxyinternet.rili.model.ScheduleShared;

public interface ScheduleSharedDao extends BaseDao<ScheduleShared, Long>{
	
	public List<Long> selectByUserId(ScheduleShared query);

}
