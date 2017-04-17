package com.galaxyinternet.rili.dao;

import java.util.List;
import java.util.Map;

import com.galaxyinternet.framework.core.dao.BaseDao;
import com.galaxyinternet.rili.model.ScheduleMettingUsers;
import com.galaxyinternet.rili.util.Receipt;

public interface ScheduleMettingUsersDao extends BaseDao<ScheduleMettingUsers, Long>{

	//获取 回执的数字
	public List<Receipt> queryMettingUsersCount(Map<String,Long> queryMap);
}
