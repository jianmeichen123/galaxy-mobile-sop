package com.galaxyinternet.model.project;

import com.galaxyinternet.framework.core.model.PagableEntity;

public class ProjectShares extends PagableEntity {
	private static final long serialVersionUID = 1L;

    private Long projectId;

    private String sharesType;

    private String sharesOwner;

    private Double sharesRatio;

    private String gainMode;

    private String remark;

    //2016/11/28新增股权结构的币种
    private Integer financeUnit;
    //2016/11/28新增股权结构的融资金额
    private Double financeAmount;

    //新增string 类型 
    private String sharesRatiofrom;
    public String getSharesType() {
        return sharesType;
    }

    public void setSharesType(String sharesType) {
        this.sharesType = sharesType == null ? null : sharesType.trim();
    }

    public String getSharesOwner() {
        return sharesOwner;
    }

    public void setSharesOwner(String sharesOwner) {
        this.sharesOwner = sharesOwner == null ? null : sharesOwner.trim();
    }

    public Double getSharesRatio() {
		return sharesRatio;
	}

	public void setSharesRatio(Double sharesRatio) {
		this.sharesRatio = sharesRatio;
	}

	public String getGainMode() {
        return gainMode;
    }

    public void setGainMode(String gainMode) {
        this.gainMode = gainMode == null ? null : gainMode.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Integer getFinanceUnit() {
		return financeUnit;
	}

	public void setFinanceUnit(Integer financeUnit) {
		this.financeUnit = financeUnit;
	}

	public Double getFinanceAmount() {
		return financeAmount;
	}

	public void setFinanceAmount(Double financeAmount) {
		this.financeAmount = financeAmount;
	}

	public String getSharesRatiofrom() {
		return sharesRatiofrom;
	}

	public void setSharesRatiofrom(String sharesRatiofrom) {
		this.sharesRatiofrom = sharesRatiofrom;
	}
    
    
	
}