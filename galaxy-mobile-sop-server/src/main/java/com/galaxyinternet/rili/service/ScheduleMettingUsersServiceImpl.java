package com.galaxyinternet.rili.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.galaxyinternet.framework.core.dao.BaseDao;
import com.galaxyinternet.framework.core.service.impl.BaseServiceImpl;
import com.galaxyinternet.rili.dao.ScheduleMettingUsersDao;
import com.galaxyinternet.rili.model.ScheduleMettingUsers;
import com.galaxyinternet.rili.util.Receipt;

@Service("ScheduleMettingUsersService")
public class ScheduleMettingUsersServiceImpl  extends BaseServiceImpl<ScheduleMettingUsers> implements ScheduleMettingUsersService{

	@Autowired
	private ScheduleMettingUsersDao scheduleMettingUsersDao;

	@Override
	protected BaseDao<ScheduleMettingUsers, Long> getBaseDao() {
		// TODO Auto-generated method stub
		return this.scheduleMettingUsersDao;
	}

	@Override
	public List<Receipt> queryMettingUsersCount(String id) {
		// TODO Auto-generated method stub
		
		Map<String,Long> queryMap = new HashMap<String,Long>();
		queryMap.put("scheduleId", Long.valueOf(id));
		
		return scheduleMettingUsersDao.queryMettingUsersCount(queryMap);
	}


	

	
}
