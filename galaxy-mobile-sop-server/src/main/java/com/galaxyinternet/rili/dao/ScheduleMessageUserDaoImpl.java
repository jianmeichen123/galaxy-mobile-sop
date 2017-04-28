package com.galaxyinternet.rili.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.galaxyinternet.framework.core.dao.impl.BaseDaoImpl;
import com.galaxyinternet.framework.core.exception.DaoException;
import com.galaxyinternet.framework.core.model.Page;
import com.galaxyinternet.framework.core.model.PageRequest;
import com.galaxyinternet.framework.core.utils.BeanUtils;
import com.galaxyinternet.rili.model.ScheduleMessageUser;

@Repository("scheduleMessageUserDao")
public class ScheduleMessageUserDaoImpl extends BaseDaoImpl<ScheduleMessageUser, Long> implements ScheduleMessageUserDao{

	/**
	 * 个人消息 列表查询
     */
	@Override
	public Page<ScheduleMessageUser> selectMuserAndMcontentList(ScheduleMessageUser query, PageRequest pageable) {
		try {
			
			Map<String, Object> params = getParams(query, pageable);
			if(query.getMessage() != null){
				Map<String, Object> message = BeanUtils.toMap(query.getMessage());
				params.put("message", message);
			}
			
			
			List<ScheduleMessageUser> contentList = sqlSessionTemplate.selectList(getSqlName("selectMuserAndMcontentList") ,params);
			
			return new Page<ScheduleMessageUser>(contentList, pageable, selectMuserAndMcontentCount(query));
		} catch (Exception e) {
			throw new DaoException(String.format("根据分页对象查询列表出错！语句:%s", getSqlName("selectMuserAndMcontentList")), e);
		}
	}
	
	/**
	 * 个人消息 列表总数
     */
	@Override
	public Long selectMuserAndMcontentCount(ScheduleMessageUser query) {
		try {
			Map<String, Object> params = BeanUtils.toMap(query);
			if(query.getMessage() != null){
				Map<String, Object> message = BeanUtils.toMap(query.getMessage());
				params.put("message", message);
			}
			return sqlSessionTemplate.selectOne(getSqlName("selectMuserAndMcontentCount"), params);
		} catch (Exception e) {
			throw new DaoException(String.format("查询对象总数出错！语句：%s", getSqlName("selectMuserAndMcontentCount")), e);
		}
	}

	
}
