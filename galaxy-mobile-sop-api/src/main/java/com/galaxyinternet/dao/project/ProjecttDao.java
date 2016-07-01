package com.galaxyinternet.dao.project;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.galaxyinternet.bo.project.ProjectBo;
import com.galaxyinternet.framework.core.dao.BaseDao;
import com.galaxyinternet.framework.core.model.Page;
import com.galaxyinternet.model.project.Project;
import com.galaxyinternet.vo.ProjectVO;

public interface ProjecttDao extends BaseDao<Project, Long> {
	/**
	 * @author chenjianmei
	 * @category 根据条件查询项目
	 * @param query
	 * @return
	 */
	public List<Project> selectProjectByMap(ProjectBo query);
	
	public Project selectTotalSummary(ProjectBo query);
	
	public List<Project> selectStageSummary(ProjectBo query);
	
	public long insertProject(Project project);
	
	public List<Project> selectListById(List<Long> idList);
	
	public List<Long> queryProjectByUserId(Project project);
	
	public Page<Project> queryPagingList(ProjectBo query , Pageable pageable);
	/**
	 * 新增跟进中的项目列表
	 * @param query
	 * @param pageable
	 * @return
	 */
	public Page<Project> querygjzProjectList(ProjectBo query , Pageable pageable);
	/**
	 * 新增投后运营的项目列表
	 * @param query
	 * @param pageable
	 * @return
	 */
	public Page<Project> querythyyList(ProjectBo query , Pageable pageable);
	/**
	 * 新增否决的项目列表
	 * @param query
	 * @param pageable
	 * @return
	 */
	public Page<Project> queryfjList(ProjectBo query , Pageable pageable);
	
	public long queryCountProject(ProjectBo query);
	
	/**
	 * 新增查询项目列表跟进中的数目
	 */
	public Long queryCountgjz(ProjectBo query);
	/**
	 * 新增查询项目列表跟进中的数目
	 */
	public Long queryCountthyy(ProjectBo query);
	/**
	 * 新增查询项目列表跟进中的数目
	 */
	public Long queryCountfj(ProjectBo query);
	
	
	/**
	 * 新增项目列表查询
	 */
	public Page<Project> queryPageList(ProjectBo query , Pageable pageable);
	
}