package com.galaxyinternet.dao.project;

import com.galaxyinternet.framework.core.dao.BaseDao;
import com.galaxyinternet.model.project.AppDelete;
import com.galaxyinternet.model.project.AppSign;

public interface AppDeleteDao  extends BaseDao<AppDelete, Long>{
	

	Long select(AppDelete appDelete);
	
}
