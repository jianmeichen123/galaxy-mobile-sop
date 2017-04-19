package com.galaxyinternet.rili.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.galaxyinternet.framework.core.constants.SqlId;
import com.galaxyinternet.framework.core.dao.impl.BaseDaoImpl;
import com.galaxyinternet.framework.core.exception.DaoException;
import com.galaxyinternet.framework.core.utils.BeanUtils;
import com.galaxyinternet.rili.model.ScheduleShared;

@Repository("scheduleSharedDao")
public class ScheduleSharedDaoImpl extends BaseDaoImpl<ScheduleShared, Long> implements ScheduleSharedDao{


	@Override
	public List<Long> selectByUserId(ScheduleShared query) {
		try {
			Map<String, Object> params = BeanUtils.toMap(query);
			return sqlSessionTemplate.selectList(getSqlName("selectByUserId"), params);
		} catch (Exception e) {
			throw new DaoException(String.format("查询对象列表出错！语句：%s", getSqlName("selectByUserId")), e);
		}
	}

	

	
}
