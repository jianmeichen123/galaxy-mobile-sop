package com.galaxyinternet.model.project;

import com.galaxyinternet.framework.core.model.PagableEntity;

public class AppDelete extends PagableEntity{
	private static final long serialVersionUID = 1L;
	
		//用户id
		private String userId;
		//消息id
		private String messageId;
		//是否已读
		private Integer isDelete;
		//更新时间
		private String updateTime;
		
		public String getUserId() {
			return userId;
		}
		public void setUserId(String userId) {
			this.userId = userId;
		}
		public String getMessageId() {
			return messageId;
		}
		public void setMessageId(String messageId) {
			this.messageId = messageId;
		}
		public Integer getIsDelete() {
			return isDelete;
		}
		public void setIsDelete(Integer isDelete) {
			this.isDelete = isDelete;
		}
		public String getUpdateTime() {
			return updateTime;
		}
		public void setUpdateTime(String updateTime) {
			this.updateTime = updateTime;
		}
		
		
		
}