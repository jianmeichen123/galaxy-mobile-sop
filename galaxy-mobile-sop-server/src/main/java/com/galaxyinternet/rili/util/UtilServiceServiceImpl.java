package com.galaxyinternet.rili.util;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.galaxyinternet.framework.core.dao.BaseDao;
import com.galaxyinternet.framework.core.service.impl.BaseServiceImpl;
import com.galaxyinternet.model.department.Department;
import com.galaxyinternet.model.user.User;
import com.galaxyinternet.rili.model.ScheduleShared;
import com.galaxyinternet.service.DepartmentService;
import com.galaxyinternet.service.UserService;


@Service("com.galaxyinternet.rili.service.UtilService")
public class UtilServiceServiceImpl extends BaseServiceImpl<ScheduleShared> implements UtilService{

	@Autowired
	private DepartmentService departmentService;
	
	@Autowired
	private UserService userService;
		
	@Override
	protected BaseDao<ScheduleShared, Long> getBaseDao() {
		return null;
	}

	
	/**
	 * 查询  user id-name 对应的map
	 */
	@Override
	public Map<Long, String> queryUidNmaeMap(List<Long> uids) {
		Map<Long, String> uIdNameMap = new HashMap<Long, String>();
		if(uids!=null && !uids.isEmpty()){
			User userQ = new User();
			userQ.setIds(uids);
			List<User> uList = userService.queryList(userQ);
			for (User aUser : uList) {
				uIdNameMap.put(aUser.getId(), aUser.getRealName());
			}
		}
		return uIdNameMap;
	}


	/**
	 * 查询 dept id-name 对应的map
	 */
	@Override
	public Map<Long, String> queryDeptIdNmaeMap(List<Long> depts) {
		
		Map<Long, String> departIdNameMap = new HashMap<Long, String>();
		
		if(depts!=null && !depts.isEmpty()){
			Department query = new Department();
			query.setType(1);
			//query.setIds(depts);
			List<Department> deptList = departmentService.queryList(query);
			
			for (Department dep : deptList) {
				departIdNameMap.put(dep.getId(), dep.getName());
			}
		}
		
		return departIdNameMap;
	}

	
	
	
	
}
