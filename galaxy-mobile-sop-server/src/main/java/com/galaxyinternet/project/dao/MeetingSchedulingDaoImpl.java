package com.galaxyinternet.project.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.galaxyinternet.bo.project.MeetingSchedulingBo;
import com.galaxyinternet.dao.project.MeetingSchedulingDao;
import com.galaxyinternet.framework.core.constants.SqlId;
import com.galaxyinternet.framework.core.dao.impl.BaseDaoImpl;
import com.galaxyinternet.framework.core.exception.DaoException;
import com.galaxyinternet.framework.core.model.Page;
import com.galaxyinternet.framework.core.model.PageRequest;
import com.galaxyinternet.framework.core.query.Query;
import com.galaxyinternet.framework.core.utils.BeanUtils;
import com.galaxyinternet.model.project.MeetingScheduling;


@Repository("meetingSchedulingDao")
public class MeetingSchedulingDaoImpl extends BaseDaoImpl<MeetingScheduling, Long> implements MeetingSchedulingDao {


	@Override
	public int updateCountBySelective(MeetingScheduling meetingScheduling){
		Assert.notNull(meetingScheduling);
		meetingScheduling.setUpdatedTime(new Date().getTime());
		try {
			return sqlSessionTemplate.update(getSqlName("updateCountBySelective"), meetingScheduling);
		} catch (Exception e) {
			throw new DaoException(String.format("更新排期池对象某些属性出错！语句：%s", getSqlName("updateCountBySelective")),
					e);
		}
	}


	@Override
	public List<MeetingSchedulingBo> selectTop5ProjectMeetingByType(
			String type) {
		try {
			return sqlSessionTemplate.selectList(getSqlName("selectTop5ProjectMeetingByType"),type);
		} catch (Exception e) {
			throw new DaoException(String.format("查询top5排期出错！语句：%s", getSqlName("selectTop5ProjectMeetingByType")),
					e);
		}
	}

	@Override
	public List<MeetingSchedulingBo> selectProjectMeetingByType(String type) {
		try {
			return sqlSessionTemplate.selectList(getSqlName("selectProjectMeetingByType"),type);
		} catch (Exception e) {
			throw new DaoException(String.format("查询排期出错！语句：%s", getSqlName("selectProjectMeetingByType")),
					e);
		}
	}


	@Override
	public int updateBySelective(MeetingScheduling ms) {
		return sqlSessionTemplate.update(getSqlName("updateBySelective"), ms);
	}

	/**
	 * 批量更新
	 */
	@Override
	public void updateBatch(List<MeetingScheduling> entityList) {
		
		if (entityList == null || entityList.isEmpty())
			return;
		for (MeetingScheduling entity : entityList) {
			this.updateBySelective(entity);
		}
	}
	@Override
	public Page<MeetingScheduling> getMeetingList(MeetingScheduling bo,PageRequest page) {
		// TODO Auto-generated method stub
		try {
			List<MeetingScheduling> list=sqlSessionTemplate.selectList(getSqlName("select"), getParams(bo, page));
			return new  Page<MeetingScheduling>(list, page, this.selectCount(bo));
		} catch (Exception e) {
			throw new DaoException(String.format("查询对象出错！语句：%s", getSqlName("selectTotal")), e);
		}
	}

	@Override
	public List<MeetingScheduling> getMeetingListByIds(MeetingScheduling bo) {
		try {
			return sqlSessionTemplate.selectList(getSqlName("selectMeetingSchedulingId"),bo);
		} catch (Exception e) {
			throw new DaoException(String.format("查询排期出错！语句：%s", getSqlName("selectMeetingSchedulingId")),
					e);
		}
	}

	/**
	 * 搜索排期集合
	 */
	@Override
	public Page<MeetingScheduling> queryMeschedulingAll(MeetingScheduling query, PageRequest pageRequest) {
		// TODO Auto-generated method stub
				try {
					List<MeetingScheduling> list=sqlSessionTemplate.selectList(getSqlName("selectqueryAll"), getParams(query, pageRequest));
					return new  Page<MeetingScheduling>(list, pageRequest, selectqueryAllCount(query));
				} catch (Exception e) {
					throw new DaoException(String.format("查询对象出错！语句：%s", getSqlName("selectqueryAll")), e);
				}
	}


	@Override
	public List<MeetingSchedulingBo> selectMonthScheduling(MeetingSchedulingBo query) {
		try {
			return sqlSessionTemplate.selectList(getSqlName("selectMonthScheduling"),query);
		} catch (Exception e) {
			throw new DaoException(String.format("查询排期出错！语句：%s", getSqlName("selectMonthScheduling")),
					e);
		}
	}


	@Override
	public Long selectMonthSchedulingCount(MeetingSchedulingBo query) {
		try {
			Long count= sqlSessionTemplate.selectOne(getSqlName("selectMonthSchedulingCount"),query);
			return count;
		} catch (Exception e) {
			throw new DaoException(String.format("查询对象总数出错！语句：%s", getSqlName("selectMonthSchedulingCount")),e);
		}
	}


	@Override
	public List<MeetingSchedulingBo> selectDayScheduling(MeetingSchedulingBo query) {
		try {
			return sqlSessionTemplate.selectList(getSqlName("selectDayScheduling"),query);
		} catch (Exception e) {
			throw new DaoException(String.format("查询当日事项出错！语句：%s", getSqlName("selectDayScheduling")),
					e);
		}
	}


	@Override
	public Long selectdpqCount(MeetingSchedulingBo query) {
		try {
			Long count= sqlSessionTemplate.selectOne(getSqlName("selectdpqCount"),query);
			return count;
		} catch (Exception e) {
			throw new DaoException(String.format("查询全部未排期数出错！语句：%s", getSqlName("selectdpqCount")),e);
		}
	}

	

	//2016/12/26 为了 新一期 排期列表的排队数目
	@Override
	public Long selectpdCount(MeetingScheduling query) {
		try {
			Long count= sqlSessionTemplate.selectOne(getSqlName("selectpdCount"),query);
			return count;
		} catch (Exception e) {
			throw new DaoException(String.format("查询排队数出错！语句：%s", getSqlName("selectpdCount")),e);
		}
	}

	//2016/12/26 为了 新一期 排期列表的排队数目
	@Override
	public Long selectltpdCount(MeetingScheduling query) {
		try {
			Long count= sqlSessionTemplate.selectOne(getSqlName("selectltpdCount"),query);
			return count;
		} catch (Exception e) {
			throw new DaoException(String.format("查询排队数出错！语句：%s", getSqlName("selectltpdCount")),e);
		}
	}
	
	@Override
	public Long selectqueryAllCount(MeetingScheduling query) {
		try {
			Map<String, Object> params = BeanUtils.toMap(query);
			return sqlSessionTemplate.selectOne(getSqlName("selectqueryAllCount"), params);
		} catch (Exception e) {
			throw new DaoException(String.format("查询对象总数出错！语句：%s", getSqlName("selectqueryAllCount")), e);
		}
	}
	
	
}