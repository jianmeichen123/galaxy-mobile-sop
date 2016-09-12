package com.galaxyinternet.dao.sopfile;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;

import com.galaxyinternet.bo.sopfile.SopFileBo;
import com.galaxyinternet.framework.core.dao.BaseDao;
import com.galaxyinternet.framework.core.model.Page;
import com.galaxyinternet.model.sopfile.SopFile;

public interface SopFileDao extends BaseDao<SopFile, Long> {

	public List<SopFile> queryByFileTypeList(SopFileBo sbo);
	
	/**
	 * 通过项目及业务分类获取唯一档案
	 * @param sf
	 * @return
	 */
	public SopFile queryByProjectAndFileWorkType(SopFile sf);
	
	/**
	 * 查询项目名称
	 * @param map
	 * @return
	 */
	public List<Map<String,String>> queryProjectName(Map<String,Object> map);
	
	
	
	
	/**
	 * 2016/8/17
	 * 文件查询
	 */
	public Page<SopFile> queryappFileList(SopFile query, Pageable pageable);
	
	
	
}