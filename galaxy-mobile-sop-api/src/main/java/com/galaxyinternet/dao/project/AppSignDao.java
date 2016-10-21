package com.galaxyinternet.dao.project;

import com.galaxyinternet.framework.core.dao.BaseDao;
import com.galaxyinternet.model.project.AppSign;

public interface AppSignDao  extends BaseDao<AppSign, Long>{
	

	Long select(AppSign appsign);
	
	
}
