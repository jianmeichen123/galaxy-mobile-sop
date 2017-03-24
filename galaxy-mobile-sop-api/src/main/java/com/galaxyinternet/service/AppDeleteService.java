package com.galaxyinternet.service;

import java.util.List;

import com.galaxyinternet.framework.core.service.BaseService;
import com.galaxyinternet.model.project.AppDelete;

public interface AppDeleteService extends BaseService<AppDelete> {
	
	Long select(AppDelete appDelete);
	
	List<String> selectappDelete(AppDelete appDelete);

}