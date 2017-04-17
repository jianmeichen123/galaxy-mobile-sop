package com.galaxyinternet.rili.service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.galaxyinternet.framework.core.dao.BaseDao;
import com.galaxyinternet.framework.core.service.impl.BaseServiceImpl;
import com.galaxyinternet.model.user.User;
import com.galaxyinternet.rili.dao.ScheduleDepartUnoDao;
import com.galaxyinternet.rili.dao.ScheduleSharedDao;
import com.galaxyinternet.rili.model.ScheduleDepartUno;
import com.galaxyinternet.rili.model.ScheduleShared;
import com.galaxyinternet.rili.util.DeptNoUsers;
import com.galaxyinternet.rili.util.UtilService;


@Service("com.galaxyinternet.rili.service.ScheduleSharedService")
public class ScheduleSharedServiceImpl extends BaseServiceImpl<ScheduleShared> implements ScheduleSharedService{

	@Autowired
	private ScheduleSharedDao scheduleSharedDao;
	
	@Autowired
	private ScheduleDepartUnoDao scheduleDepartUnoDao;
	
	
	@Autowired
	private UtilService utilService;
	
	
	@Override
	protected BaseDao<ScheduleShared, Long> getBaseDao() {
		return this.scheduleSharedDao;
	}

	
	/**
	 * 选择查看成员，成员包括：自己
	 * 共享给自己的成员（默认为自己）
	 * @return {[createUid:111, createUname:"TEST"]}
	 */
	public List<ScheduleShared> querySharedUsers(Object objUser) {
		User user = (User)objUser;
		
		List<ScheduleShared> cusers = new ArrayList<ScheduleShared>();
		
		ScheduleShared my = new ScheduleShared();
		my.setCreateUid(user.getId());
		//my.setCreateUname(user.getRealName());	
		my.setCreateUname("我的日历");
		cusers.add(my);
		
		ScheduleShared query = new ScheduleShared();
		query.setToUid(user.getId());
		List<ScheduleShared> qList = scheduleSharedDao.selectList(query);
		
		if(qList!=null && !qList.isEmpty()){
			cusers.addAll(qList);
		}
		return cusers;
	}

	
	/**
	 * 自己共享的 共享人列表查询
	 * 共享人列表
	 * @param  toUname 查询
	 * @return {[toUid:111, toUname:"TEST",toDeptName:"DNAME"]}
	 */
	public List<ScheduleShared> queryMySharedUsers(Object objUser,String toUname) {
		User user = (User)objUser;
		
		ScheduleShared query = new ScheduleShared();
		query.setCreateUid(user.getId());
		if(toUname!=null){
			query.setToUname(toUname);
		}
		List<ScheduleShared> cusers = scheduleSharedDao.selectList(query);
		
		if(cusers!=null && !cusers.isEmpty()){
			List<Long> uids = new ArrayList<Long>();
			List<Long> depts = new ArrayList<Long>();
			for(ScheduleShared tempU : cusers){
				//uids.add(tempU.getToUid());
				depts.add(tempU.getDepartmentId());
			}
			
			//Map<Long, String> uidNmaeMap = utilService.queryUidNmaeMap(uids);
			Map<Long, String> deptIdNmaeMap = utilService.queryDeptIdNmaeMap(uids);
			
			for(ScheduleShared tempU : cusers){
				tempU.setToDeptName(deptIdNmaeMap.get(tempU.getDepartmentId()));
			}
		}
		
		return cusers;
	}


	/**
	 * 添加共享人
	 * @param  {deptNoUsers : [{deptId:111,userCount:10,userIds:[222,333]},{deptId:111, userCount:10,userIds:[222,333]}]}
	 */
	@Transactional
	public void saveSharedUsers(Object objUser, ScheduleShared comShareQ, ScheduleDepartUno dun, List<DeptNoUsers> deptNoUsers){
		User user = (User)objUser;
		
		//删除所有共享人
		scheduleSharedDao.delete(comShareQ);
		
		//删除所有部门共享人数量
		scheduleDepartUnoDao.delete(dun);
		
		List<ScheduleShared> toSaveShaerds = new ArrayList<ScheduleShared>();
		List<ScheduleDepartUno> toSaveDeptUnos = new ArrayList<ScheduleDepartUno>();
		
		if(deptNoUsers!=null && !deptNoUsers.isEmpty()){
			
			for(DeptNoUsers temp : deptNoUsers){
				
				List<Long> userIds = temp.getUserIds();
				if(userIds == null || userIds.isEmpty()) continue;
				
				Map<Long, String> uidNmaeMap = utilService.queryUidNmaeMap(userIds);
				
				
				// 部门-相关人数
				dun = new ScheduleDepartUno();
				dun.setRemarkType((byte) 1);
				dun.setDepartmentId(temp.getDeptId());
				dun.setUserCount(temp.getUserCount());
				dun.setCreatedId(user.getId());
				toSaveDeptUnos.add(dun);
				
				for(Long uidTemp : userIds){
					// 日程共享
					comShareQ = new ScheduleShared();
					
					comShareQ.setToUid(uidTemp);
					comShareQ.setToUname(uidNmaeMap.get(uidTemp));
					comShareQ.setDepartmentId(temp.getDeptId());
					comShareQ.setCreateUid(user.getId());
					comShareQ.setCreateUname(user.getRealName());
					
					toSaveShaerds.add(comShareQ);
				}
			}
			
			if(!toSaveShaerds.isEmpty()) scheduleSharedDao.insertInBatch(toSaveShaerds);
			if(!toSaveDeptUnos.isEmpty()) scheduleDepartUnoDao.insertInBatch(toSaveDeptUnos);
		}
	}


	
	/**
	 * 删除共享人
	 */
	@Transactional
	public void delSharedUser(Long id, ScheduleDepartUno queryDun) {
		
		scheduleDepartUnoDao.changeUno(queryDun);

		//删除共享人
		scheduleSharedDao.deleteById(id);
	}

	
	
	
}
