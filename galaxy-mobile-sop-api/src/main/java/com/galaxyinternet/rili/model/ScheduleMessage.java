package com.galaxyinternet.rili.model;

import com.galaxyinternet.framework.core.model.PagableEntity;

/**
 * 消息内容
 */
public class ScheduleMessage extends PagableEntity {

	private static final long serialVersionUID = 1L;

	private byte category; //0:操作消息 1:系统消息

	private Byte type;   //消息类型 日程(1:会议 2:拜访 3:其它)

	private String content; // 消息内容

	private String remark;  // 消息备注(拒绝原因、)

	private Long sendTime;  // 消息发送时间

	private byte status;   // 0:可用 1:禁用
	
	private Long createdUid;

	private String createdUname;

	
	public Byte getCategory() {
		return category;
	}

	public void setCategory(byte category) {
		this.category = category;
	}

	public byte getType() {
		return type;
	}

	public void setType(Byte type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content == null ? null : content.trim();
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark == null ? null : remark.trim();
	}

	public Long getSendTime() {
		return sendTime;
	}

	public void setSendTime(Long sendTime) {
		this.sendTime = sendTime;
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public Long getCreatedUid() {
		return createdUid;
	}

	public void setCreatedUid(Long createdUid) {
		this.createdUid = createdUid;
	}

	public String getCreatedUname() {
		return createdUname;
	}

	public void setCreatedUname(String createdUname) {
		this.createdUname = createdUname == null ? null : createdUname.trim();
	}

}