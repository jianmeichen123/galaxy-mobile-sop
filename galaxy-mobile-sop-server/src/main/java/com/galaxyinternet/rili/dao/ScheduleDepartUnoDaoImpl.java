package com.galaxyinternet.rili.dao;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.galaxyinternet.framework.core.dao.impl.BaseDaoImpl;
import com.galaxyinternet.rili.model.ScheduleDepartUno;

@Repository("ScheduleDepartUnoDao")
public class ScheduleDepartUnoDaoImpl extends BaseDaoImpl<ScheduleDepartUno, Long> implements ScheduleDepartUnoDao{

	/**
	 * 删除部门下一个人时， 同步修改部门人数对应
	 */
	public void changeUno(ScheduleDepartUno query) {
		
		List<ScheduleDepartUno> oldDuns = this.selectList(query);
		
		if(oldDuns!=null && !oldDuns.isEmpty()){
			int userCount = 0;
			
			ScheduleDepartUno oldDun = oldDuns.get(0);
			
			if(oldDun.getUserCount()!=null){
				userCount = oldDun.getUserCount().intValue() - 1;
			}
			
			if(userCount == 0){
				this.delete(query);
			}else{
				query.setId(oldDun.getId());
				query.setUserCount(userCount);
				this.updateById(query);
			}
		}
	}

}
	