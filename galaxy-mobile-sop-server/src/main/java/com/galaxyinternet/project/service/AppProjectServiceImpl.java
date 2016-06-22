package com.galaxyinternet.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.galaxyinternet.bo.project.ProjectBo;
import com.galaxyinternet.dao.project.ProjectDao;
import com.galaxyinternet.framework.core.dao.BaseDao;
import com.galaxyinternet.framework.core.model.Page;
import com.galaxyinternet.framework.core.service.impl.BaseServiceImpl;
import com.galaxyinternet.service.AppProjectService;
import com.galaxyinternet.vo.GeneralProjectVO;
import com.galaxyinternet.vo.ProjectVO;

@Service("com.galaxyinternet.service.AppProjectService")
public class AppProjectServiceImpl extends BaseServiceImpl<ProjectVO> implements AppProjectService {

	@Autowired
	private ProjectDao  projectDao;
	
	@Override
	public GeneralProjectVO queryPagingProjectList(ProjectBo query, Pageable pageable)throws Exception {
		Page<ProjectVO> pageBean =  projectDao.queryPagingList(query, pageable);
		GeneralProjectVO gpbean = new GeneralProjectVO();
		pageBean.setPageable(null);
		gpbean.setPvPage(pageBean);
		return gpbean;
	}
	
	public long queryCountProjectByParam(ProjectBo query)throws Exception{
		return projectDao.queryCountProject(query);
	}
	
	
	@Override
	protected BaseDao<ProjectVO, Long> getBaseDao() {
		// TODO Auto-generated method stub
		return null;
	}

}
