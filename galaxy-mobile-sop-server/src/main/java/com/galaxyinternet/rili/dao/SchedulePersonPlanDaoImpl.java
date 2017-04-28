package com.galaxyinternet.rili.dao;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.galaxyinternet.framework.core.dao.impl.BaseDaoImpl;
import com.galaxyinternet.framework.core.exception.DaoException;
import com.galaxyinternet.framework.core.utils.BeanUtils;
import com.galaxyinternet.rili.model.ScheduleContacts;
import com.galaxyinternet.rili.model.SchedulePersonPlan;

@Repository("SchedulePersonPlanDao")
public class SchedulePersonPlanDaoImpl extends BaseDaoImpl<SchedulePersonPlan, Long> implements SchedulePersonPlanDao{

	//查询出联系人的id
	@Override
	public List<Long> selectCountsId(SchedulePersonPlan schedulePersonPlan) {
		Assert.notNull(schedulePersonPlan);
		try {
			Map<String, Object> params = BeanUtils.toMap(schedulePersonPlan);
			return sqlSessionTemplate.selectOne(getSqlName("selectCountsId"), params);
		} catch (Exception e) {
			throw new DaoException(String.format("查询一条记录出错！语句：%s", getSqlName("selectCountsId")), e);
		}
	}

	@Override
	public List<ScheduleContacts> queryByCountsId(SchedulePersonPlan schedulePersonPlan) {
		Assert.notNull(schedulePersonPlan);
		try {
			Map<String, Object> params = BeanUtils.toMap(schedulePersonPlan);
			return sqlSessionTemplate.selectList(getSqlName("queryByCountsId"), params);
		} catch (Exception e) {
			throw new DaoException(String.format("查询一条记录出错！语句：%s", getSqlName("queryByCountsId")), e);
		}
	}

}
	