package com.galaxyinternet.model.feedback;

import java.util.Date;

import com.galaxyinternet.framework.core.model.BaseEntity;
/**
 * 意见反馈
 * @author zhangchunyuan
 *
 */
public class Feedback extends BaseEntity {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 6491596460298721092L;

	/**
	 * 反馈内容
	 */
	private String content;
	
	/**
	 * 创建人id
	 */
	private Long createdUid;
	
	/**
	 * 创建时间
	 */
	private  Date createdDate;


	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Long getCreatedUid() {
		return createdUid;
	}

	public void setCreatedUid(Long createdUid) {
		this.createdUid = createdUid;
	}


	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

}
