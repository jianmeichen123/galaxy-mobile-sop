package com.galaxyinternet.rili.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.galaxyinternet.framework.core.dao.BaseDao;
import com.galaxyinternet.framework.core.service.impl.BaseServiceImpl;
import com.galaxyinternet.model.department.Department;
import com.galaxyinternet.model.user.User;
import com.galaxyinternet.rili.dao.ScheduleDepartUnoDao;
import com.galaxyinternet.rili.dao.ScheduleMettingUsersDao;
import com.galaxyinternet.rili.dao.ScheduleSharedDao;
import com.galaxyinternet.rili.model.ScheduleDepartUno;
import com.galaxyinternet.rili.model.ScheduleMettingUsers;
import com.galaxyinternet.rili.model.ScheduleShared;
import com.galaxyinternet.rili.util.UtilUser;
import com.galaxyinternet.service.DepartmentService;
import com.galaxyinternet.service.UserService;

@Service("ScheduleDepartUnoService")
public class ScheduleDepartUnoServiceImpl  extends BaseServiceImpl<ScheduleDepartUno> implements ScheduleDepartUnoService{

	@Autowired
	private ScheduleDepartUnoDao scheduleDepartUnoDao;
	@Autowired
	private ScheduleSharedDao scheduleSharedDao;
	@Autowired
	private ScheduleMettingUsersDao ScheduleMettingUsersDao;
	
	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private UserService userService;
	
	@Override
	protected BaseDao<ScheduleDepartUno, Long> getBaseDao() {
		return this.scheduleDepartUnoDao;
	}


	
	/**
	 * 查询所有部门信息 id-name
	 * 封装  默认部门      人员  和   已经选择的人员
	 * 封装  部门 - 部门用户数量
	 * @param query : {remarkType:0 不要remarkId/1, remarkId:schedule_id}  
	 * 				   remarkType 记录类型 0:日程（会议） 1:共享
	*/
	public List<ScheduleDepartUno> queryDeptUinfo(Object objUser, ScheduleDepartUno query) {
		User user = (User) objUser;
		
		List<ScheduleDepartUno> resultList = new ArrayList<ScheduleDepartUno>();
		
		byte remarkType = query.getRemarkType();
		Long userDdptId = user.getDepartmentId();
		//boolean isCheckedAll = false;
		
		//部门用户表   ：   部门id - 各部门所选人数num
/*		Map<Long,Integer> deptIdUnum = new HashMap<Long,Integer>();
		query.setCreatedId(user.getId());
		List<ScheduleDepartUno> queryDunList = scheduleDepartUnoDao.selectList(query);
		if(queryDunList!=null && !queryDunList.isEmpty()){
			for(ScheduleDepartUno temp : queryDunList){
				deptIdUnum.put(temp.getDepartmentId(), temp.getUserCount());
			}
		}*/
		
		//部门表中的数据   所有部门
		Department deptquery = new Department();
		deptquery.setType(1);
		//List<Department> deptList = departmentService.queryAll();
		
		//
		List<Department> deptList = departmentService.queryList(deptquery);
		User uq = null;  //查询部门下人  条件封装
		ScheduleMettingUsers muQ = null;  //查询   日程（会议）- 部门下人数    条件封装
		ScheduleShared comShareQ = null;  //查询  共享                     - 部门下人数    条件封装
		ScheduleDepartUno dun = null;     //返回结果  封装
		
		List<UtilUser> deptUsers = null;  // 部门下的人员 并标记是否已选择
		List<Long> deptCheckedUid = null; // 各部门下的 已经选择的人员id
		
		for(Department deptTemp : deptList){
			deptUsers = new ArrayList<UtilUser>();
			deptCheckedUid = new ArrayList<Long>();
			
			//部门下所有人
			uq = new User();
			uq.setDepartmentId(deptTemp.getId());
			List<User> deptUs = userService.queryList(uq);
			
			//部门已经选择的人
			if(remarkType == 0){ //日程（会议）
				muQ = new ScheduleMettingUsers();
				muQ.setScheduleId(query.getRemarkId());
				muQ.setDepartmentId(deptTemp.getId());
				muQ.setCreatedId(user.getId());
				
				List<ScheduleMettingUsers> queryMus = ScheduleMettingUsersDao.selectList(muQ);
				if(queryMus != null && !queryMus.isEmpty()){
					for(ScheduleMettingUsers temp : queryMus){
						deptCheckedUid.add(temp.getUserId());
					}
				}
			}else if(remarkType == 1){ //共享
				comShareQ = new ScheduleShared();
				comShareQ.setDepartmentId(deptTemp.getId());
				comShareQ.setCreateUid(user.getId());
				
				List<ScheduleShared> queryShareds = scheduleSharedDao.selectList(comShareQ);
				if(queryShareds != null && !queryShareds.isEmpty()){
					for(ScheduleShared temp : queryShareds){
						deptCheckedUid.add(temp.getToUid());
					}
				}
			}
			
			//部门下所有人中 对已经选择的人标记
			for(User tuser : deptUs){
				UtilUser au = new UtilUser();
				au.setId(tuser.getId());
				au.setName(tuser.getRealName());
				if(deptCheckedUid.contains(tuser.getId().longValue())){
					au.setIsChecked(true);
				}
				deptUsers.add(au);
			}
			
			//是否全选
			/*if(deptCheckedUid.size() == deptUs.size()){
				isCheckedAll = true;
			}else{
				isCheckedAll = false;
			}*/
			
			//结果返回
			dun = new ScheduleDepartUno();
			dun.setIsCheckedAllUser(deptCheckedUid.size() == deptUs.size());
			dun.setDeptUsers(deptUsers);
			if(deptTemp.getId().longValue() == userDdptId){
				dun.setToChecked(true);
			}
			
			dun.setRemarkType(remarkType);
			dun.setRemarkId(query.getRemarkId());
			dun.setDepartmentId(deptTemp.getId());
			dun.setDeptName(deptTemp.getName());
			//dun.setUserCount(deptIdUnum.get(deptTemp.getId()));
			
			resultList.add(dun);
		}
		
		return resultList;
	}
	
	/*
	public List<ScheduleDepartUno> queryDeptUinfo(Object objUser, ScheduleDepartUno query) {
		User user = (User) objUser;
		
		List<ScheduleDepartUno> resultList = new ArrayList<ScheduleDepartUno>();
		
		
		byte remarkType = query.getRemarkType();
		Long userDdptId = user.getDepartmentId();
		boolean isCheckedAll = false;
		
		
		//部门用户表   ：   部门id - 各部门所选人数num
		Map<Long,Integer> deptIdUnum = new HashMap<Long,Integer>();
		query.setCreatedId(user.getId());
		List<ScheduleDepartUno> queryDunList = scheduleDepartUnoDao.selectList(query);
		if(queryDunList!=null && !queryDunList.isEmpty()){
			for(ScheduleDepartUno temp : queryDunList){
				deptIdUnum.put(temp.getDepartmentId(), temp.getUserCount());
			}
		}
		
		
		//部门下的人员  和   已经选择的人员
		List<UtilUser> deptUsers = new ArrayList<UtilUser>();
		List<Long> deptCheckedUid = new ArrayList<Long>();
		if(userDdptId!=null){
			//部门下所有人
		//	User uq = new User();
		//	uq.setDepartmentId(userDdptId);
		//	List<User> deptUs = userService.queryList(uq);
			List<User> deptUs = userService.queryAll();
			//部门已经选择的人
			if(remarkType == 0){ //日程（会议）
				ScheduleMettingUsers muQ = new ScheduleMettingUsers();
				muQ.setScheduleId(query.getRemarkId());
				muQ.setDepartmentId(userDdptId);
				muQ.setCreatedId(user.getId());
				
				List<ScheduleMettingUsers> queryMus = ScheduleMettingUsersDao.selectList(muQ);
				if(queryMus != null && !queryMus.isEmpty()){
					for(ScheduleMettingUsers temp : queryMus){
						deptCheckedUid.add(temp.getUserId());
					}
				}
			}else if(remarkType == 1){ //共享
				ScheduleShared comShareQ = new ScheduleShared();
				comShareQ.setDepartmentId(userDdptId);
				comShareQ.setCreateUid(user.getId());
				
				List<ScheduleShared> queryShareds = scheduleSharedDao.selectList(comShareQ);
				if(queryShareds != null && !queryShareds.isEmpty()){
					for(ScheduleShared temp : queryShareds){
						deptCheckedUid.add(temp.getToUid());
					}
				}
			}
			//部门下所有人中 对已经选择的人标记
			for(User tuser : deptUs){
				UtilUser au = new UtilUser();
				au.setId(tuser.getId());
				au.setName(tuser.getRealName());
				if(deptCheckedUid.contains(tuser.getId().longValue())){
					au.setIsChecked(true);
				}
				deptUsers.add(au);
			}
			//是否全选
			if(deptCheckedUid.size() == deptUs.size()){
				isCheckedAll = true;
			}
		}
		
		//部门表中的数据   所有部门(查询事业部的)
		Department deptquery = new Department();
		deptquery.setType(1);
		
		//List<Department> deptList = departmentService.queryAll();
		List<Department> deptList = departmentService.queryList(deptquery);
		
		for(Department deptTemp : deptList){
			ScheduleDepartUno dun = new ScheduleDepartUno();
			if(deptTemp.getId().longValue() == userDdptId){
				dun.setToChecked(true);
				dun.setIsCheckedAllUser(isCheckedAll);
				dun.setDeptUsers(deptUsers);
			}
			dun.setRemarkType(remarkType);
			dun.setRemarkId(query.getRemarkId());
			dun.setDepartmentId(deptTemp.getId());
			dun.setDeptName(deptTemp.getName());
			dun.setUserCount(deptIdUnum.get(deptTemp.getId()));
			
			resultList.add(dun);
		}
		
		return resultList;
	}*/


	@Override
	public ScheduleDepartUno queryDeptUinfoByDid(Object objUser, ScheduleDepartUno query) {
		ScheduleDepartUno result = new ScheduleDepartUno();
		
		User user = (User) objUser;
		
		Byte remarkType = query.getRemarkType();
		Long checkDdptId = query.getDepartmentId();
		boolean isCheckedAll = false;
		Integer userCount = null;
		
		
		//根据部门id  ：   部门id - 部门所选人数 userCount
		query.setCreatedId(user.getId());
		List<ScheduleDepartUno> queryDunList = scheduleDepartUnoDao.selectList(query);
		if(queryDunList!=null && !queryDunList.isEmpty()){
			userCount = queryDunList.get(0).getUserCount();
		}
		
		
		//部门下的人员  和   已经选择的人员
		List<UtilUser> deptUsers = new ArrayList<UtilUser>();
		List<Long> deptCheckedUid = new ArrayList<Long>();
		//部门下所有人
		User uq = new User();
		uq.setDepartmentId(checkDdptId);
		List<User> deptUs = userService.queryList(uq);
		//部门已经选择的人
		if(remarkType.intValue() == 0){ //日程（会议）
			ScheduleMettingUsers muQ = new ScheduleMettingUsers();
			muQ.setScheduleId(query.getRemarkId());
			muQ.setDepartmentId(checkDdptId);
			muQ.setCreatedId(user.getId());
			
			List<ScheduleMettingUsers> queryMus = ScheduleMettingUsersDao.selectList(muQ);
			if(queryMus != null && !queryMus.isEmpty()){
				for(ScheduleMettingUsers temp : queryMus){
					deptCheckedUid.add(temp.getUserId());
				}
			}
		}else if(remarkType.intValue() == 1){ //共享
			ScheduleShared comShareQ = new ScheduleShared();
			comShareQ.setDepartmentId(checkDdptId);
			comShareQ.setCreateUid(user.getId());
			
			List<ScheduleShared> queryShareds = scheduleSharedDao.selectList(comShareQ);
			if(queryShareds != null && !queryShareds.isEmpty()){
				for(ScheduleShared temp : queryShareds){
					deptCheckedUid.add(temp.getToUid());
				}
			}
		}
		//部门下所有人中 对已经选择的人标记
		for(User tuser : deptUs){
			UtilUser au = new UtilUser();
			au.setId(tuser.getId());
			au.setName(tuser.getRealName());
			if(deptCheckedUid.contains(tuser.getId().longValue())){
				au.setIsChecked(true);
			}
			deptUsers.add(au);
		}
		//是否全选
		if(deptCheckedUid.size() == deptUs.size()){
			isCheckedAll = true;
		}
		
		//结果封装
		result.setRemarkType(remarkType);
		result.setRemarkId(query.getRemarkId());
		result.setDepartmentId(checkDdptId);
	//	result.setUserCount(userCount);
		result.setToChecked(true);
		result.setIsCheckedAllUser(isCheckedAll);
		result.setDeptUsers(deptUsers);
		
		
		return result;
	}


	/**
	 * 通过人名模糊查询
	 */
	@Override
	public ScheduleDepartUno queryPerson(Object objUser, ScheduleDepartUno query) {
		ScheduleDepartUno result = new ScheduleDepartUno();
			//部门下所有人通过姓名模糊查询
		List<UtilUser> deptUsers = new ArrayList<UtilUser>();
			User uq = new User();
			uq.setRealName(query.getUname());
			List<User> deptUs = userService.queryList(uq);
			//部门下所有人中 对已经选择的人标记
			for(User tuser : deptUs){
				UtilUser au = new UtilUser();
				au.setId(tuser.getId());
				au.setName(tuser.getRealName());				
				Department department =departmentService.queryById(tuser.getDepartmentId());
				au.setDepartId(tuser.getDepartmentId());
				au.setDepartName(department.getName());
				deptUsers.add(au);
			}		
		
		result.setDeptUsers(deptUsers);		
		return result;
	}


	
	
	
	
	//查询出全部部门及部门中的人及人是否已经共享的部门是否 默认及
	public ScheduleDepartUno queryPerswwon(Object objUser, ScheduleDepartUno query) {
		Map<String,List<Object>> deptIdAndUser = new HashMap<String,List<Object>>();
		
		List<Object>  ss = deptIdAndUser.get("");
		for(Object object:ss){
			//if(scheduleDepartUno.getDeptUsers()!=null && scheduleDepartUno.getDeptUsers().size()>0){
				
			}
		//}
		
		List<ScheduleDepartUno> sdu = new ArrayList<ScheduleDepartUno>();
		
		for(ScheduleDepartUno scheduleDepartUno:sdu){
			
			if(scheduleDepartUno.getDeptUsers()!=null && scheduleDepartUno.getDeptUsers().size()>0){
				
				for(UtilUser duser:scheduleDepartUno.getDeptUsers()){
					
					
					
					
				}
				
			}
			
			
		}
		
		
		
		
		
		
		
		
		
	
		return null;
	
	}
}
