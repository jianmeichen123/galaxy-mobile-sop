package com.galaxyinternet.rili.util;


import java.util.List;
import java.util.Map;

import com.galaxyinternet.framework.core.service.BaseService;
import com.galaxyinternet.rili.model.ScheduleShared;


public interface UtilService extends BaseService<ScheduleShared>{

	/**
	 * 查询  user id-name 对应的map
	 */
	Map<Long,String> queryUidNmaeMap(List<Long> uids);

	/**
	 * 查询 dept id-name 对应的map
	 */
	Map<Long,String> queryDeptIdNmaeMap(List<Long> depts);
	
	
}
