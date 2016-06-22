package com.galaxyinternet.bo.project;

import com.galaxyinternet.model.project.MeetingScheduling;

public class MeetingSchedulingBo extends MeetingScheduling {
	private static final long serialVersionUID = 1L;
	private String extendFiled;// 业务对象中扩展的字段
	
	
	
	private String nameLike;//模糊查询条件匹配

	//新增的字段用于app端进行待排期和已排期的数字统计
	private Integer CountscheduleStatusy;
	private Integer CountscheduleStatusd;
	
	
	public Integer getCountscheduleStatusy() {
		return CountscheduleStatusy;
	}

	public void setCountscheduleStatusy(Integer countscheduleStatusy) {
		CountscheduleStatusy = countscheduleStatusy;
	}

	public Integer getCountscheduleStatusd() {
		return CountscheduleStatusd;
	}

	public void setCountscheduleStatusd(Integer countscheduleStatusd) {
		CountscheduleStatusd = countscheduleStatusd;
	}

	public String getNameLike() {
		return nameLike;
	}

	public void setNameLike(String nameLike) {
		this.nameLike = nameLike;
	}

	public String getExtendFiled() {
		return extendFiled;
	}

	public void setExtendFiled(String extendFiled) {
		this.extendFiled = extendFiled;
	}


	
	
}