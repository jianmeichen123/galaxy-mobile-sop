package com.galaxyinternet.service;

import com.galaxyinternet.framework.core.service.BaseService;
import com.galaxyinternet.model.project.AppDelete;
import com.galaxyinternet.model.project.AppSign;

public interface AppDeleteService extends BaseService<AppDelete> {
	
	Long select(AppDelete appDelete);

}