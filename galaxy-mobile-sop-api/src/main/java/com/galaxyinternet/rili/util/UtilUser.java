package com.galaxyinternet.rili.util;

import java.io.Serializable;


public class UtilUser implements Serializable{

	private static final long serialVersionUID = 1L;

	private Long id;
	
	private String name;

	Boolean isChecked; //该人员是否已选择  true:已选
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getIsChecked() {
		return isChecked;
	}

	public void setIsChecked(Boolean isChecked) {
		this.isChecked = isChecked;
	} 
	
	
}
