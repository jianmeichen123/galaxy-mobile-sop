package com.galaxyinternet.project.dao;

import org.springframework.stereotype.Repository;

import com.galaxyinternet.dao.project.AppDeleteDao;
import com.galaxyinternet.framework.core.dao.impl.BaseDaoImpl;
import com.galaxyinternet.framework.core.exception.DaoException;
import com.galaxyinternet.model.project.AppDelete;
import com.galaxyinternet.model.project.AppSign;


@Repository("appDeleteDao")
public class AppDeleteDaoImpl extends BaseDaoImpl<AppDelete, Long> implements AppDeleteDao {


	@Override

	public Long select(AppDelete query) {
		try {
			Long count= sqlSessionTemplate.selectOne(getSqlName("selectIdByparms"),query);
			return count;
		} catch (Exception e) {
			throw new DaoException(String.format("查询对象总数出错！语句：%s", getSqlName("selectIdByparms")),e);
		}
	}
}