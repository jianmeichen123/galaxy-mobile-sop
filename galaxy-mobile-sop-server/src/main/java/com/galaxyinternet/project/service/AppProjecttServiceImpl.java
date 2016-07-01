package com.galaxyinternet.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.galaxyinternet.bo.project.ProjectBo;
import com.galaxyinternet.dao.project.ProjecttDao;
import com.galaxyinternet.framework.core.dao.BaseDao;
import com.galaxyinternet.framework.core.model.Page;
import com.galaxyinternet.framework.core.service.impl.BaseServiceImpl;
import com.galaxyinternet.model.project.Project;
import com.galaxyinternet.service.AppProjecttService;
import com.galaxyinternet.vo.GeneralProjecttVO;
import com.galaxyinternet.vo.ProjectVO;

@Service("com.galaxyinternet.service.AppProjecttService")
public class AppProjecttServiceImpl extends BaseServiceImpl<ProjectVO> implements AppProjecttService {

	@Autowired
	private ProjecttDao  projecttDao;
	
/*	@Override
	public GeneralProjectVO queryPagingProjectList(ProjectBo query, Pageable pageable)throws Exception {
		Page<Project> pageBean =  projectDao.queryPagingList(query, pageable);
		GeneralProjectVO gpbean = new GeneralProjectVO();
		pageBean.setPageable(null);
		gpbean.setPvPage(pageBean);
		return gpbean;
	}*/
	
	public long queryCountProjectByParam(ProjectBo query)throws Exception{
		return projecttDao.queryCountProject(query);
	}
	
	//跟进中查询数目
		
		public Long queryProjectgjzCount(ProjectBo query) throws Exception {
			return projecttDao.queryCountgjz(query);
		}
	//投后运营查询数目
		
		public Long queryProjectthyyCount(ProjectBo query) throws Exception {
			return projecttDao.queryCountthyy(query);
		}
	//否决查询数目
		
		public Long queryProjectfjCount(ProjectBo query) throws Exception {
			return projecttDao.queryCountfj(query);
		}
	
	@Override
	protected BaseDao<ProjectVO, Long> getBaseDao() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GeneralProjecttVO querygjzProjectList(ProjectBo query, Pageable pageable) throws Exception {
		Page<Project> pageBean =  projecttDao.querygjzProjectList(query, pageable);
		GeneralProjecttVO gpbean = new GeneralProjecttVO();
		pageBean.setPageable(null);
		gpbean.setPvPage(pageBean);
		return gpbean;
	}

	@Override
	public GeneralProjecttVO querythyyList(ProjectBo query, Pageable pageable) throws Exception {
		Page<Project> pageBean =  projecttDao.querythyyList(query, pageable);
		GeneralProjecttVO gpbean = new GeneralProjecttVO();
		pageBean.setPageable(null);
		gpbean.setPvPage(pageBean);
		return gpbean;
	}

	@Override
	public GeneralProjecttVO queryfjList(ProjectBo query, Pageable pageable) throws Exception {
		Page<Project> pageBean =  projecttDao.queryfjList(query, pageable);
		GeneralProjecttVO gpbean = new GeneralProjecttVO();
		pageBean.setPageable(null);
		gpbean.setPvPage(pageBean);
		return gpbean;
	}

	@Override
	public GeneralProjecttVO queryPageList(ProjectBo query, Pageable pageable) throws Exception {
		Page<Project> pageBean =  projecttDao.queryPageList(query, pageable);
		GeneralProjecttVO gpbean = new GeneralProjecttVO();
		pageBean.setPageable(null);
		gpbean.setPvPage(pageBean);
		return gpbean;
	}

	@Override
	public GeneralProjecttVO queryPagingProjectList(ProjectBo query, Pageable pageable) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}


}
