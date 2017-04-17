package com.galaxyinternet.rili.model;

import java.util.List;

import com.galaxyinternet.framework.core.model.PagableEntity;
import com.galaxyinternet.rili.util.DeptNoUsers;

/**
 * 共享日程
 */
public class ScheduleShared extends PagableEntity {

	private static final long serialVersionUID = 1L;

	private Long toUid;  //被共享人id
	private String toUname;  //被共享人

	private Long departmentId;
	private String toDeptName;

	private Long createUid;
	
	private String createUname;
	
	
	List<DeptNoUsers> deptNoUsers;   
	
	
	public Long getToUid() {
		return toUid;
	}

	public void setToUid(Long toUid) {
		this.toUid = toUid;
	}

	public String getToUname() {
		return toUname;
	}

	public void setToUname(String toUname) {
		this.toUname = toUname;
	}

	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

	public String getToDeptName() {
		return toDeptName;
	}

	public void setToDeptName(String toDeptName) {
		this.toDeptName = toDeptName;
	}

	public Long getCreateUid() {
		return createUid;
	}

	public void setCreateUid(Long createUid) {
		this.createUid = createUid;
	}

	public String getCreateUname() {
		return createUname;
	}

	public void setCreateUname(String createUname) {
		this.createUname = createUname;
	}

	public List<DeptNoUsers> getDeptNoUsers() {
		return deptNoUsers;
	}

	public void setDeptNoUsers(List<DeptNoUsers> deptNoUsers) {
		this.deptNoUsers = deptNoUsers;
	}

	
	
}