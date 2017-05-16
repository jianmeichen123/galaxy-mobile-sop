package com.galaxyinternet.rili.model;


import java.util.List;

import com.galaxyinternet.framework.core.model.PagableEntity;
import com.galaxyinternet.rili.util.DeptNoUsers;


public class ScheduleInfo extends PagableEntity{

	private static final long serialVersionUID = 1L;
	
	
	private Long parentId;  // 关联于他人(会议发起人)日程

	private Byte type;      //日程类型 1:会议 2:拜访 3:其它

	private Long nameId;    // 日程名称关联字典表id
	
    private String name;

    private Byte projectType;   //关联项目类型  1:项目 2:线索
    
    private Long projectId;

    private String startTime;   // 日程开始时间

    private String endTime;     // 日程结束时间

    private Byte isAllday;      // 是否全天 0:否 1:是

    private Long wakeupId;      // 提醒时间:日程字典表id

    private String remark;      // 备注

    private Long createdId;

    private Long updatedId;

    
	private Integer year;
	private Integer month;
	private Integer day;
	
	private Long idIsNotEq;      // id != idIsNotEq
	private String bqStartTime;  // 日程开始  起始时间     startTime  >   bqStartTime
    private String bqEndTime;    // 日程开始  结束时间     startTime  <   bqEndTime
    private String eqStartTime;  // 日程结束  起始时间     endTime >  eqStartTime
	
    private String sbTimeForAllday;    // 日程开始  起始时间     startTime  >   sbTimeForAllday
    private String seTimeForAllday;    // 日程开始  结束时间     startTime  <   seTimeForAllday
    private Boolean queryForMounth;    //xml 查询月视图 特标识
    
    private String userName;     // 日历创建人姓名

    private String projectName;  // 项目的名称

    private List<ScheduleMettingUsers> scheduleMettingUsersList; // 新增的为了 新增会议添加的 邀请与会人的list

    List<DeptNoUsers> deptNoUsers;  // 与会人的list集合

    private List<SchedulePersonPlan> schedulePersonPlanList;     // 拜访人-拜访日程

    private String remind;   // 提醒时间
    
    private List<ScheduleContacts> scheduleContactsList;//拜访人集合
    
    private String lastMouthDay;//当月最后一天
    
    private String schedulePerson;//拜访人名称

    
    //private String messageType; //为了 其他工程调用更新的推送时写的参数
    
    private String visitType;  //用来对应消息列表的type(拜访1.4)
    private Integer isDel;  //逻辑删除的字段(是否删除字段0:未删除,1:已删除 )
    private Long callonPerson;//拜访 联系人 关联字段
    
    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public Long getNameId() {
		return nameId;
	}

	public void setNameId(Long nameId) {
		this.nameId = nameId;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Byte getProjectType() {
		return projectType;
	}

	public void setProjectType(Byte projectType) {
		this.projectType = projectType;
	}

	public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
    
    public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime == null ? null : startTime.trim();
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime == null ? null : endTime.trim();
	}

	public Byte getIsAllday() {
        return isAllday;
    }

    public void setIsAllday(Byte isAllday) {
        this.isAllday = isAllday;
    }

    public Long getWakeupId() {
        return wakeupId;
    }

    public void setWakeupId(Long wakeupId) {
        this.wakeupId = wakeupId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
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

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

	public String getBqStartTime() {
		return bqStartTime;
	}

	public void setBqStartTime(String bqStartTime) {
		this.bqStartTime = bqStartTime;
	}

	public String getBqEndTime() {
		return bqEndTime;
	}

	public void setBqEndTime(String bqEndTime) {
		this.bqEndTime = bqEndTime;
	}

	public String getEqStartTime() {
		return eqStartTime;
	}

	public void setEqStartTime(String eqStartTime) {
		this.eqStartTime = eqStartTime;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public List<ScheduleMettingUsers> getScheduleMettingUsersList() {
		return scheduleMettingUsersList;
	}

	public void setScheduleMettingUsersList(List<ScheduleMettingUsers> scheduleMettingUsersList) {
		this.scheduleMettingUsersList = scheduleMettingUsersList;
	}

	public List<DeptNoUsers> getDeptNoUsers() {
		return deptNoUsers;
	}

	public void setDeptNoUsers(List<DeptNoUsers> deptNoUsers) {
		this.deptNoUsers = deptNoUsers;
	}

	public List<SchedulePersonPlan> getSchedulePersonPlanList() {
		return schedulePersonPlanList;
	}

	public void setSchedulePersonPlanList(List<SchedulePersonPlan> schedulePersonPlanList) {
		this.schedulePersonPlanList = schedulePersonPlanList;
	}

	public String getRemind() {
		return remind;
	}

	public void setRemind(String remind) {
		this.remind = remind;
	}

	public List<ScheduleContacts> getScheduleContactsList() {
		return scheduleContactsList;
	}

	public void setScheduleContactsList(List<ScheduleContacts> scheduleContactsList) {
		this.scheduleContactsList = scheduleContactsList;
	}

	public String getLastMouthDay() {
		return lastMouthDay;
	}

	public void setLastMouthDay(String lastMouthDay) {
		this.lastMouthDay = lastMouthDay;
	}

	public String getSbTimeForAllday() {
		return sbTimeForAllday;
	}

	public void setSbTimeForAllday(String sbTimeForAllday) {
		this.sbTimeForAllday = sbTimeForAllday;
	}

	public String getSeTimeForAllday() {
		return seTimeForAllday;
	}

	public void setSeTimeForAllday(String seTimeForAllday) {
		this.seTimeForAllday = seTimeForAllday;
	}

	public Long getIdIsNotEq() {
		return idIsNotEq;
	}

	public void setIdIsNotEq(Long idIsNotEq) {
		this.idIsNotEq = idIsNotEq;
	}

	public Boolean getQueryForMounth() {
		return queryForMounth;
	}

	public void setQueryForMounth(Boolean queryForMounth) {
		this.queryForMounth = queryForMounth;
	}

	public String getSchedulePerson() {
		return schedulePerson;
	}

	public void setSchedulePerson(String schedulePerson) {
		this.schedulePerson = schedulePerson;
	}

	
	public Integer getIsDel() {
		return isDel;
	}

	public void setIsDel(Integer isDel) {
		this.isDel = isDel;
	}

	public String getVisitType() {
		return visitType;
	}

	public void setVisitType(String visitType) {
		this.visitType = visitType;
	}

	public Long getCallonPerson() {
		return callonPerson;
	}

	public void setCallonPerson(Long callonPerson) {
		this.callonPerson = callonPerson;
	}

	

}