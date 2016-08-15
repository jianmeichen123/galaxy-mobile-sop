package com.galaxyinternet.bo;

import com.galaxyinternet.model.idea.Idea;

public class IdeaBo extends Idea {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	 
	private String abReason;
	
	private Long giveUpId;
	
	/**
	 * 新需求创意列表数目查询
	 */
	private Long cydCount;  //待认领
	private Long cyyCount;  //已认领

	public String getAbReason() {
		return abReason;
	}

	public void setAbReason(String abReason) {
		this.abReason = abReason;
	}

	public Long getGiveUpId() {
		return giveUpId;
	}

	public void setGiveUpId(Long giveUpId) {
		this.giveUpId = giveUpId;
	}

	public Long getCydCount() {
		return cydCount;
	}

	public void setCydCount(Long cydCount) {
		this.cydCount = cydCount;
	}

	public Long getCyyCount() {
		return cyyCount;
	}

	public void setCyyCount(Long cyyCount) {
		this.cyyCount = cyyCount;
	}

	
	
	


}
