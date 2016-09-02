package com.galaxyinternet.service;

import org.springframework.data.domain.Pageable;

import com.galaxyinternet.framework.core.model.Page;
import com.galaxyinternet.framework.core.service.BaseService;
import com.galaxyinternet.model.user.appUserList;

public interface AppUserListService extends BaseService<appUserList>{
	

	

	//分页查询
	public Page<appUserList> queryPageListapp(appUserList query, Pageable pageable);
	

	

}
