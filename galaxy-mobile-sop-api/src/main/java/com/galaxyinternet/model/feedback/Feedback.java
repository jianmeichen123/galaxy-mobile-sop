package com.galaxyinternet.model.feedback;

import java.util.Date;

import com.galaxyinternet.framework.core.model.BaseEntity;
import com.galaxyinternet.framework.core.model.Pagable;
/**
 * 意见反馈
 * @author zhangchunyuan
 *
 */
public class Feedback extends BaseEntity  implements Pagable{
	
	
	

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
	 * 反馈人
	 */
	private String username;
	
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


	protected Integer pageSize;
	protected Integer pageNum;

	@Override
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	public Integer getPageSize() {
		return this.pageSize;
	}

	@Override
	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;

	}

	@Override
	public Integer getPageNum() {
		return this.pageNum;
	}

	@Override
	public void setDirection(String direction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getDirection() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setProperty(String property) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
