package com.galaxyinternet.model.project;

import com.galaxyinternet.framework.core.model.PagableEntity;

public class AppMeetScheduling extends PagableEntity{
	private static final long serialVersionUID = 1L;
	
	private String tjh;
	private String lxh;
	private String ceops;
	
	private Long tjhCount;
	private Long lxhCount;
	private Long ceopsCount;
	public String getTjh() {
		return tjh;
	}
	public void setTjh(String tjh) {
		this.tjh = tjh;
	}
	public String getLxh() {
		return lxh;
	}
	public void setLxh(String lxh) {
		this.lxh = lxh;
	}
	public String getCeops() {
		return ceops;
	}
	public void setCeops(String ceops) {
		this.ceops = ceops;
	}
	public Long getTjhCount() {
		return tjhCount;
	}
	public void setTjhCount(Long tjhCount) {
		this.tjhCount = tjhCount;
	}
	public Long getLxhCount() {
		return lxhCount;
	}
	public void setLxhCount(Long lxhCount) {
		this.lxhCount = lxhCount;
	}
	public Long getCeopsCount() {
		return ceopsCount;
	}
	public void setCeopsCount(Long ceopsCount) {
		this.ceopsCount = ceopsCount;
	}
	
	
    
}