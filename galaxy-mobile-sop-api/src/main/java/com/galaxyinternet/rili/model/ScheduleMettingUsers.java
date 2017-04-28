package com.galaxyinternet.rili.model;

import com.galaxyinternet.framework.core.model.PagableEntity;

public class ScheduleMettingUsers  extends PagableEntity {
	
    /**
	 * 日程(会议)-参会人
	 */
	private static final long serialVersionUID = 1L;

	

    private Long scheduleId;

    private Long userId;

    private String userName;
    
    private Long departmentId;

    private Byte messageState;

    private String rejectCause;

    private Long createdId;

    

    private Long updatedId;

    

    

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Byte getMessageState() {
        return messageState;
    }

    public void setMessageState(Byte messageState) {
        this.messageState = messageState;
    }

    public String getRejectCause() {
        return rejectCause;
    }

    public void setRejectCause(String rejectCause) {
        this.rejectCause = rejectCause == null ? null : rejectCause.trim();
    }

    public Long getCreatedId() {
        return createdId;
    }

    public void setCreatedId(Long createdId) {
        this.createdId = createdId;
    }

    public Long getUpdatedId() {
        return updatedId;
    }

    public void setUpdatedId(Long updatedId) {
        this.updatedId = updatedId;
    }

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

    
   
}