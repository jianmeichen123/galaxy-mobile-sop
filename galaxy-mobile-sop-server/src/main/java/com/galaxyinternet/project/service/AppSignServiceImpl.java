package com.galaxyinternet.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.galaxyinternet.dao.project.AppSignDao;
import com.galaxyinternet.framework.core.dao.BaseDao;
import com.galaxyinternet.framework.core.service.impl.BaseServiceImpl;
import com.galaxyinternet.model.project.AppSign;
import com.galaxyinternet.service.AppSignService;


@Service("com.galaxyinternet.service.AppSignService")
public class AppSignServiceImpl extends BaseServiceImpl<AppSign> implements AppSignService {

	@Autowired
	private AppSignDao appSignDao;
	
	@Override
	protected BaseDao<AppSign, Long> getBaseDao() {
		return this.appSignDao;
	}
	


}
