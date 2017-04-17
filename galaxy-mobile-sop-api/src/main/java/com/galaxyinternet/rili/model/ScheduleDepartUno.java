package com.galaxyinternet.rili.model;

import java.util.List;

import com.galaxyinternet.framework.core.model.PagableEntity;
import com.galaxyinternet.rili.util.UtilUser;


/**
 * 部门-相关人数
 */
public class ScheduleDepartUno  extends PagableEntity {
	
	private static final long serialVersionUID = 1L;


    private Byte remarkType;  // 记录类型 0:日程 1:共享

    private Long remarkId;

    private Long departmentId;

    private String deptName;

    private Integer userCount;

    private Long createdId;

    private Long updatedId;

    
    Boolean toChecked;        //选择该条记录， 高亮选中
    Boolean isCheckedAllUser; //是否部门人员全选  true:全选
    List<UtilUser> deptUsers; //部门下人员
    

    public Byte getRemarkType() {
        return remarkType;
    }

    public void setRemarkType(Byte remarkType) {
        this.remarkType = remarkType;
    }

    public Long getRemarkId() {
        return remarkId;
    }

    public void setRemarkId(Long remarkId) {
        this.remarkId = remarkId;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName == null ? null : deptName.trim();
    }

    public Integer getUserCount() {
        return userCount;
    }

    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
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

	public Boolean getToChecked() {
		return toChecked;
	}

	public void setToChecked(Boolean toChecked) {
		this.toChecked = toChecked;
	}

	public Boolean getIsCheckedAllUser() {
		return isCheckedAllUser;
	}

	public void setIsCheckedAllUser(Boolean isCheckedAllUser) {
		this.isCheckedAllUser = isCheckedAllUser;
	}

	public List<UtilUser> getDeptUsers() {
		return deptUsers;
	}

	public void setDeptUsers(List<UtilUser> deptUsers) {
		this.deptUsers = deptUsers;
	}

    
}
