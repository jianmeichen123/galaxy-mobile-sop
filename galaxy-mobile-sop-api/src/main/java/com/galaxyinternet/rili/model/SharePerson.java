package com.galaxyinternet.rili.model;

import com.galaxyinternet.framework.core.model.PagableEntity;

public class SharePerson extends PagableEntity  {

    /**
	 * 拜访计划-拜访人
	 */
	private static final long serialVersionUID = 1L;

	

	private String userId;            //用户id
	
	private String userName;          //用户名称

	private String departName;   //所属事业部的名称
	private String departId;       //所属事业部的id
	
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getDepartName() {
		return departName;
	}
	public void setDepartName(String departName) {
		this.departName = departName;
	}
	public String getDepartId() {
		return departId;
	}
	public void setDepartId(String departId) {
		this.departId = departId;
	}
	

   
}