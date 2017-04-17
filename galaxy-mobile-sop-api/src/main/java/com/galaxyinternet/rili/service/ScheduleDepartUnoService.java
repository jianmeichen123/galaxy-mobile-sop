package com.galaxyinternet.rili.service;

import java.util.List;

import com.galaxyinternet.framework.core.service.BaseService;
import com.galaxyinternet.rili.model.ScheduleDepartUno;

public interface ScheduleDepartUnoService extends BaseService<ScheduleDepartUno>
{

	/**
	 * 查询所有部门信息 id-name
	 * 封装  默认部门      人员  和   已经选择的人员
	 * 封装  部门 - 部门用户数量
	 * @param query : {remarkType:0/1, remarkId:schedule_id}  
	 * 				   remarkType 记录类型 0:日程（会议）  1:共享,不要remarkId
	 * @return {[id:1, toUid:111, toUname:"TEST",toDeptName:"DNAME"]}
	*/
	List<ScheduleDepartUno> queryDeptUinfo(Object objUser, ScheduleDepartUno query);

	
	/**
	 * 查询所选部门下   人员  和   已经选择的人员
	 * 封装  部门 - 部门用户数量
	 * @param query : {remarkType:0/1, remarkId:schedule_id,departmentId:1212}  
	 * 				   remarkType 记录类型 0:日程（会议）  1:共享,不要remarkId
	 * @return {[id:1, toUid:111, toUname:"TEST",toDeptName:"DNAME"]}
	*/
	ScheduleDepartUno queryDeptUinfoByDid(Object objUser, ScheduleDepartUno query);
	
}
