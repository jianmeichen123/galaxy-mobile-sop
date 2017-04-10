package com.galaxyinternet.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.galaxyinternet.dao.project.AppDeleteDao;
import com.galaxyinternet.framework.core.dao.BaseDao;
import com.galaxyinternet.framework.core.service.impl.BaseServiceImpl;
import com.galaxyinternet.model.project.AppDelete;
import com.galaxyinternet.model.project.AppSign;
import com.galaxyinternet.service.AppDeleteService;


@Service("com.galaxyinternet.service.AppDeleteService")
public class AppDeleteServiceImpl extends BaseServiceImpl<AppDelete> implements AppDeleteService {

	@Autowired
	private AppDeleteDao appDeleteDao;
	

	@Override
	protected BaseDao<AppDelete, Long> getBaseDao() {
		// TODO Auto-generated method stub
		return appDeleteDao;
	}
	
	@Override
	public Long select(AppDelete appDelete) {
		
		return appDeleteDao.select(appDelete);
	}

	@Override
	public List<String> selectappDelete(AppDelete appDelete) {
		// TODO Auto-generated method stub
		return appDeleteDao.selectappDelete(appDelete);
	}

	
	


}