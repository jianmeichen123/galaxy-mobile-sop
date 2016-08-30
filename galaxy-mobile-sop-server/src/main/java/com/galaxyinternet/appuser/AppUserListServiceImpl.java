package com.galaxyinternet.appuser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.galaxyinternet.dao.project.AppUserListDao;
import com.galaxyinternet.framework.core.dao.BaseDao;
import com.galaxyinternet.framework.core.model.Page;
import com.galaxyinternet.framework.core.service.impl.BaseServiceImpl;
import com.galaxyinternet.model.user.appUserList;
import com.galaxyinternet.service.AppUserListService;

@Service("com.galaxyinternet.service.AppUserListService")
public class AppUserListServiceImpl extends BaseServiceImpl<appUserList> implements AppUserListService {
	
	@Autowired
	private AppUserListDao  appUserListDao ;


   //app端分页查询
	@Override
	public Page<appUserList> queryPageListapp(appUserList query, Pageable pageable) {
		// TODO Auto-generated method stub
		return appUserListDao.queryPageListapp(query, pageable);
	}


	@Override
	protected BaseDao<appUserList, Long> getBaseDao() {
		// TODO Auto-generated method stub
		return null;
	}
	

}
