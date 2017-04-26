package com.galaxyinternet.rili.dao;

import com.galaxyinternet.framework.core.dao.BaseDao;
import com.galaxyinternet.framework.core.model.Page;
import com.galaxyinternet.framework.core.model.PageRequest;
import com.galaxyinternet.rili.model.ScheduleMessageUser;

public interface ScheduleMessageUserDao extends BaseDao<ScheduleMessageUser, Long>{

	/**
	 * 个人消息 列表查询
     */
	Page<ScheduleMessageUser> selectMuserAndMcontentList(ScheduleMessageUser query, PageRequest pageable);
	/**
	 * 个人消息 列表总数
     */
	Long selectMuserAndMcontentCount(ScheduleMessageUser query);

}
