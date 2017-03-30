package com.galaxyinternet.project.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.galaxyinternet.bo.project.ProjectBo;
import com.galaxyinternet.dao.project.ProjecttDao;
import com.galaxyinternet.framework.core.constants.SqlId;
import com.galaxyinternet.framework.core.dao.impl.BaseDaoImpl;
import com.galaxyinternet.framework.core.exception.DaoException;
import com.galaxyinternet.framework.core.model.Page;
import com.galaxyinternet.framework.core.utils.BeanUtils;
import com.galaxyinternet.model.project.Project;


@Repository("projecttDao")
public class ProjecttDaoImpl extends BaseDaoImpl<Project, Long> implements ProjecttDao {
	
	
	@Override
	public List<Project> selectProjectByMap(ProjectBo query) {
		Assert.notNull(query);
		Map<String, Object> params = BeanUtils.toMap(query);
		try {
		return sqlSessionTemplate.selectList(getSqlName(SqlId.SQL_SELECT),params);
		} catch (Exception e) {
			throw new DaoException(String.format("查询对象列表出错：%s", getSqlName(SqlId.SQL_SELECT)), e);
		}
	}

	@Override
	public Project selectTotalSummary(ProjectBo query) {
		return sqlSessionTemplate.selectOne(getSqlName("selectTotalSummary"),query);
	}

	@Override
	public List<Project> selectStageSummary(ProjectBo query) {
		return sqlSessionTemplate.selectList(getSqlName("selectStageSummary"),query);
	}

	@Override
	public long insertProject(Project project) {
		Assert.notNull(project);
		try {
			/*ID id = entity.getId();
			if (null == id) {
				if (StringUtils.isBlank(stringId)) {
					entity.setId((ID) generateId());
				}
			}*/
			sqlSessionTemplate.insert(getSqlName(SqlId.SQL_INSERT), project);
			return project.getId();
		} catch (Exception e) {
			throw new DaoException(String.format("添加对象出错！语句：%s", getSqlName(SqlId.SQL_INSERT)), e);
		}
	}

	@Override
	public List<Project> selectListById(List<Long> idList) {
		return sqlSessionTemplate.selectList(getSqlName("selectListById"),idList);
	}

	@Override
	public List<Long> queryProjectByUserId(Project project) {
		return sqlSessionTemplate.selectList(getSqlName("selectByUserId"),project);
	}
	
	public long queryCountProject(ProjectBo query){
		Assert.notNull(query);
		try{
			long count = sqlSessionTemplate.selectOne(getSqlName("selectCountRecordByParam"), query);
		   return count;
		}catch(Exception e){
			throw new DaoException(String.format("根据项目状态统计数据量出错！语句:%s" ,  getSqlName("selectCountRecordByParam")), e);
		}
		
	}

	@Override
	public Page<Project> queryPagingList(ProjectBo query , Pageable pageable) {
		// TODO Auto-generated method stub
		Assert.notNull(query);		
		try {
			List<Project> contentList = sqlSessionTemplate.selectList( getSqlName("selectPagingProjectList"),getParams(query, pageable));
					
			return new  Page<Project>(contentList, pageable, this.selectCount(query));
		} catch (Exception e) {
			throw new DaoException(String.format("根据分页对象查询列表出错！语句:%s", getSqlName("selectPagingProjectList")), e);
		}
		
	}

	@Override
	public Long queryCountgjz(ProjectBo query) {
		Assert.notNull(query);
		try{
			Long count = sqlSessionTemplate.selectOne(getSqlName("selectBygjzCount"), query);
		   return count;
		}catch(Exception e){
			throw new DaoException(String.format("根据项目状态gjz统计数据量出错！语句:%s" ,  getSqlName("selectBygjzCount")), e);
		}
	}

	@Override
	public Long queryCountthyy(ProjectBo query) {
		Assert.notNull(query);
		try{
			Long count = sqlSessionTemplate.selectOne(getSqlName("selectBythCount"), query);
		   return count;
		}catch(Exception e){
			throw new DaoException(String.format("根据项目状态th统计数据量出错！语句:%s" ,  getSqlName("selectBythCount")), e);
		}
	}

	@Override
	public Long queryCountfj(ProjectBo query) {
		Assert.notNull(query);
		try{
			Long count = sqlSessionTemplate.selectOne(getSqlName("selectByfjCount"), query);
		   return count;
		}catch(Exception e){
			throw new DaoException(String.format("根据项目状态统计数据量出错！语句:%s" ,  getSqlName("selectByfjCount")), e);
		}
	}

	/**
	 * projectList  跟进中
	 */
	@Override
	public Page<Project> querygjzProjectList(ProjectBo queryy , Pageable pageable) {
		// TODO Auto-generated method stub
		Assert.notNull(queryy);		
		try {
			List<Project> contentList = sqlSessionTemplate.selectList( getSqlName("selectBygjz"),getParams(queryy, pageable));
					
			return new  Page<Project>(contentList, pageable, queryCountgjz(queryy));
		} catch (Exception e) {
			throw new DaoException(String.format("根据分页对象查询列表出错！语句:%s", getSqlName("selectBygjz")), e);
		}
		
	}
	
	/**
	 * 投后运营
	 */
	@Override
	public Page<Project> querythyyList(ProjectBo query , Pageable pageable) {
		// TODO Auto-generated method stub
		Assert.notNull(query);		
		try {
			List<Project> contentList = sqlSessionTemplate.selectList( getSqlName("selectByth"),getParams(query, pageable));
					
			return new  Page<Project>(contentList, pageable, queryCountthyy(query));
		} catch (Exception e) {
			throw new DaoException(String.format("根据分页对象查询列表出错！语句:%s", getSqlName("selectByth")), e);
		}
		
	}
	
	
	
	/**
	 * 否决
	 */
	
	@Override
	public Page<Project> queryfjList(ProjectBo query , Pageable pageable) {
		// TODO Auto-generated method stub
		Assert.notNull(query);		
		try {
			List<Project> contentList = sqlSessionTemplate.selectList( getSqlName("selectByfj"),getParams(query, pageable));
					
			return new  Page<Project>(contentList, pageable, queryCountfj(query));
		} catch (Exception e) {
			throw new DaoException(String.format("根据分页对象查询列表出错！语句:%s", getSqlName("selectByfj")), e);
		}
		
	}

	@Override
	public Page<Project> queryPageList(ProjectBo query, Pageable pageable) {
		try {
			List<Project> contentList = sqlSessionTemplate.selectList( getSqlName(SqlId.SQL_SELECT),getParams(query, pageable));
				
			return new Page<Project>(contentList, pageable, this.selectCount(query));
		} catch (Exception e) {
			throw new DaoException(String.format("根据分页对象查询列表出错！语句:%s", getSqlName(SqlId.SQL_SELECT)), e);
		}
	}
	
	
	

}