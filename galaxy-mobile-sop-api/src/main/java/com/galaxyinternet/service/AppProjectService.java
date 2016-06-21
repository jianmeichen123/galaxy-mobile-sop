package com.galaxyinternet.service;

import org.springframework.data.domain.Pageable;

import com.galaxyinternet.bo.project.ProjectBo;
import com.galaxyinternet.vo.GeneralProjectVO;

/**
 * 移动端项目信息管理的Service接口
 * @author LZJ
 * @ClassName  : AppProjectService  
 * @Version  版本   
 * @ModifiedBy 
 * @Copyright  Galaxyinternet  
 * @date  2016年6月17日 上午10:16:26
 */
public interface AppProjectService {
	
	public GeneralProjectVO queryPagingProjectList(ProjectBo query, Pageable pageable)throws Exception ;
	
	public long queryCountProjectByParam(ProjectBo query)throws Exception;

}
