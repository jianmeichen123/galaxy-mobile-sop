package com.galaxyinternet.project.dao;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.galaxyinternet.bo.project.MeetingSchedulingBo;
import com.galaxyinternet.dao.project.AppSignDao;
import com.galaxyinternet.framework.core.constants.SqlId;
import com.galaxyinternet.framework.core.dao.impl.BaseDaoImpl;
import com.galaxyinternet.framework.core.exception.DaoException;
import com.galaxyinternet.framework.core.utils.BeanUtils;
import com.galaxyinternet.model.project.AppSign;


@Repository("appSignDao")
public class AppSignDaoImpl extends BaseDaoImpl<AppSign, Long> implements AppSignDao {

	@Override

	public Long select(AppSign query) {
		try {
			Long count= sqlSessionTemplate.selectOne(getSqlName("selectIdByparms"),query);
			return count;
		} catch (Exception e) {
			throw new DaoException(String.format("查询对象总数出错！语句：%s", getSqlName("selectMonthSchedulingCount")),e);
		}
	}

/*	@Override
	public Long select(AppSign appsign) {
		try {
			Long count = sqlSessionTemplate.selectOne(getSqlName("selectId"), appsign);
			return count;
		} catch (Exception e) {
			throw new DaoException(String.format("查询对象总数出错！语句：%s", getSqlName("selectId")), e);
		}
	}
*/

}