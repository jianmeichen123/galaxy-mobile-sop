package com.galaxyinternet.appuser;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.galaxyinternet.dao.project.AppUserListDao;
import com.galaxyinternet.framework.core.dao.impl.BaseDaoImpl;
import com.galaxyinternet.framework.core.exception.DaoException;
import com.galaxyinternet.framework.core.model.Page;
import com.galaxyinternet.model.user.appUserList;

@Repository(value="AppUserListDao")
public class AppUserListDaoImpl extends BaseDaoImpl<appUserList, Long> implements AppUserListDao {

	@Override
	public Page<appUserList> queryPageListapp(appUserList query, Pageable pageable) {
		try {
			List<appUserList> contentList = sqlSessionTemplate.selectList(("selectbiao"),
					getParams(query, pageable));
			return new  Page<appUserList>(contentList, pageable, this.selectCountc());
		} catch (Exception e) {
			throw new DaoException(String.format("根据分页对象查询列表出错！语句:%s", "selectbiao"), e);
		}
	}
	
	@Override
	public Long selectCountc() {
		try {
			return sqlSessionTemplate.selectOne("selectCountc");
		} catch (Exception e) {
			throw new DaoException(String.format("查询对象总数出错！语句：%s", "selectCountc"), e);
		}
	}
}
