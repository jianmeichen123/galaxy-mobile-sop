package com.galaxyinternet.rili.service;


import java.util.List;

import com.galaxyinternet.framework.core.service.BaseService;
import com.galaxyinternet.rili.model.ScheduleDepartUno;
import com.galaxyinternet.rili.model.ScheduleShared;
import com.galaxyinternet.rili.util.DeptNoUsers;


public interface ScheduleSharedService extends BaseService<ScheduleShared>{

	/**
	 * 选择查看成员，成员包括：自己
	 * 共享给自己的成员（默认为自己）
	 * @return {[createUid:111, createUname:"TEST"]}
	 */
	List<ScheduleShared> querySharedUsers(Object objUser);

	/**
	 * 自己共享的 共享人列表查询
	 * 共享人列表
	 * @param  toUname 查询
	 * @return {[toUid:111, toUname:"TEST",toDeptName:"DNAME"]}
	 */
	List<ScheduleShared> queryMySharedUsers(Object objUser,String toUname);

	/**
	 * 添加共享人
	  @param  {deptNoUsers : [{deptId:111,userCount:10,userIds:[222,333]},{deptId:111, userCount:10,userIds:[222,333]}]}
	 */
	//void saveSharedUsers(Object objUser, ScheduleShared query);
	//void saveSharedUsers(Object objUser, ScheduleShared comShareQ, ScheduleDepartUno dun, List<DeptNoUsers> deptNoUsers);
	void saveSharedUsers(Object objUser, ScheduleShared comShareQ, List<DeptNoUsers> deptNoUsers);
	/**
	 * 删除共享人
	 */
	void delSharedUser(Long id, ScheduleDepartUno queryDun);

	

	/**
	 * 查询出用户id
	 * @param query
	 * @return
	 */
	public List<Long> selectByUserId(ScheduleShared query);


}
