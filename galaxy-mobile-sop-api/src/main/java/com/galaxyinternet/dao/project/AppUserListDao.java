package com.galaxyinternet.dao.project;

import org.springframework.data.domain.Pageable;

import com.galaxyinternet.framework.core.dao.BaseDao;
import com.galaxyinternet.framework.core.model.Page;
import com.galaxyinternet.model.user.appUserList;

public interface AppUserListDao  extends BaseDao<appUserList, Long>{
	

	
	public Page<appUserList> queryPageListapp(appUserList query, Pageable pageable);
	
	public Long selectCountc();
}
