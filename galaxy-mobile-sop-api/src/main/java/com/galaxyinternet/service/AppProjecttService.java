package com.galaxyinternet.service;

import org.springframework.data.domain.Pageable;

import com.galaxyinternet.bo.project.ProjectBo;
import com.galaxyinternet.vo.GeneralProjecttVO;

/**
 * 移动端项目信息管理的Service接口
 * @author LZJ
 * @ClassName  : AppProjectService  
 * @Version  版本   
 * @ModifiedBy 
 * @Copyright  Galaxyinternet  
 * @date  2016年6月17日 上午10:16:26
 */
public interface AppProjecttService  {
	
	public GeneralProjecttVO queryPagingProjectList(ProjectBo query, Pageable pageable)throws Exception ;
	
	public long queryCountProjectByParam(ProjectBo query)throws Exception;

	/**
	 * 为项目列表新增数字查询的dao gjz
	 */
	public Long queryProjectgjzCount(ProjectBo query) throws Exception;
	
	/**
	 * 为项目列表新增数字查询的dao thyy
	 */
	public Long queryProjectthyyCount(ProjectBo query) throws Exception;
	/**
	 * 为项目列表新增数字查询的dao fj
	 */
	public Long queryProjectfjCount(ProjectBo query) throws Exception;
	/**
	 * 项目列表查询 gjz
	 */
	public GeneralProjecttVO querygjzProjectList(ProjectBo query, Pageable pageable)throws Exception ;
	
	
	
	/**
	 * 项目列表查询 投后运营
	 */
	public GeneralProjecttVO querythyyList(ProjectBo query, Pageable pageable)throws Exception ;
	
	
	/**
	 * 项目列表查询 否决
	 * @param query
	 * @param pageable
	 * @return
	 * @throws Exception
	 */
	public GeneralProjecttVO queryfjList(ProjectBo query, Pageable pageable)throws Exception ;
	
	/**
	 * 项目列表搜索
	 */
	
	public GeneralProjecttVO queryPageList(ProjectBo query, Pageable pageable)throws Exception ;
	
	
	
}
