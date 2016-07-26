package com.galaxyinternet.model.project;

import com.galaxyinternet.framework.core.model.PagableEntity;

public class AppMeetScheduling extends PagableEntity{
	private static final long serialVersionUID = 1L;
	
	private String tjh; //投决会
	private String lxh; //立项会
	private String ceops; //ceo评审
	
	private Long tjhCount; //投决会数量
	private Long lxhCount; //立项会数量
	private Long ceopsCount; //ceo评审数量
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