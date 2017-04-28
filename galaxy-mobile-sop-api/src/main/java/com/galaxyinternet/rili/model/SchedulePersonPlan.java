package com.galaxyinternet.rili.model;

import com.galaxyinternet.framework.core.model.PagableEntity;

public class SchedulePersonPlan extends PagableEntity  {

    /**
	 * 拜访计划-拜访人
	 */
	private static final long serialVersionUID = 1L;

	

    private Long scheduleId;

    private Long contactsId;



    private Long createdId;

  
    private Long updatedId;


    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Long getContactsId() {
        return contactsId;
    }

    public void setContactsId(Long contactsId) {
        this.contactsId = contactsId;
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
}