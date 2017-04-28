package com.galaxyinternet.rili.dao;

import com.galaxyinternet.framework.core.dao.BaseDao;
import com.galaxyinternet.rili.model.ScheduleDepartUno;

public interface ScheduleDepartUnoDao extends BaseDao<ScheduleDepartUno, Long>{

	/**
	 * 删除部门下一个人时， 同步修改部门人数对应
	 */
	void changeUno(ScheduleDepartUno query);

}
