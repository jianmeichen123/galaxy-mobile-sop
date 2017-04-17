package com.galaxyinternet.rili.service;

import java.util.List;

import com.galaxyinternet.framework.core.service.BaseService;
import com.galaxyinternet.rili.model.ScheduleMettingUsers;
import com.galaxyinternet.rili.util.Receipt;

public interface ScheduleMettingUsersService extends BaseService<ScheduleMettingUsers>
{
	

	public List<Receipt> queryMettingUsersCount(String id) ;
	
	
}
