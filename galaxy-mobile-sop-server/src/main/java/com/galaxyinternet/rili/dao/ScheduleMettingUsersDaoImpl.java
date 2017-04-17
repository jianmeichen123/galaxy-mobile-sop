package com.galaxyinternet.rili.dao;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.galaxyinternet.framework.core.dao.impl.BaseDaoImpl;
import com.galaxyinternet.framework.core.exception.DaoException;
import com.galaxyinternet.rili.model.ScheduleMettingUsers;
import com.galaxyinternet.rili.util.Receipt;

@Repository("ScheduleMettingUsersDao")
public class ScheduleMettingUsersDaoImpl extends BaseDaoImpl<ScheduleMettingUsers, Long> implements ScheduleMettingUsersDao{

	@Override
	public void insertInBatch(List<ScheduleMettingUsers> entityList)
	{
		sqlSessionTemplate.insert(getSqlName("insertBatch"), entityList);
	}

	@Override
	public List<Receipt> queryMettingUsersCount(Map<String, Long> queryMap) {
		Assert.notNull(queryMap);	
		try {
			return sqlSessionTemplate.selectList("selectByCount" , queryMap);
		} catch (Exception e) {
			throw new DaoException(String.format("查询对象列表出错：%s", "selectByCount"), e);
		}
	}

	
}
	