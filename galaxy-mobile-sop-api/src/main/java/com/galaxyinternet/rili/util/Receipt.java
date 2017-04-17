package com.galaxyinternet.rili.util;

import java.util.List;

import com.galaxyinternet.framework.core.model.PagableEntity;
import com.galaxyinternet.rili.model.ScheduleMettingUsers;

/**
 * 回执
 */
public class Receipt extends PagableEntity{

	private static final long serialVersionUID = 1L;

	private Integer code;       //回执对应参会人表的code
	
	List<ScheduleMettingUsers> schlist;    // 回执的每块的参会人
	
	private Integer receiptCount; //回执的数量
	
	private String name;       //回执的名称

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public List<ScheduleMettingUsers> getSchlist() {
		return schlist;
	}

	public void setSchlist(List<ScheduleMettingUsers> schlist) {
		this.schlist = schlist;
	}

	public Integer getReceiptCount() {
		return receiptCount;
	}

	public void setReceiptCount(Integer receiptCount) {
		this.receiptCount = receiptCount;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
	
}