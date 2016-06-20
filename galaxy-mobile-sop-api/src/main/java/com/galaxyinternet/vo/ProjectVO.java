package com.galaxyinternet.vo;

import com.galaxyinternet.framework.core.model.PagableEntity;
/**
 * 移动端项目列表的视图Bean
 * @author LZJ
 * @ClassName  : ProjectVO  
 * @Version  版本   
 * @ModifiedBy修改人  
 * @Copyright  Galaxyinternet  
 * @date  2016年6月16日 下午5:05:51
 */
public class ProjectVO extends PagableEntity {

	
	private static final long serialVersionUID = -173701740149276113L;

	private String projectName;//项目名称
	private Double projectContribution; //初始投资额(融资金额)
	private String projectType;//项目类型编码 分内部、外部项目
	private String projectTypeName;//项目类型名称
	private Double projectShareRatio;//所占股份百分比(出让份额)
	private String projectProgress; //项目阶段编码
	private String projectProgressName; //项目阶段名称
	private String projectCareerline; //项目所属投资事业线名称（部门名称）
    private Long projectDepartid;//项目所属部门id
//	private String projectStatus;//项目状态编码  
    
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public Double getProjectContribution() {
		return projectContribution;
	}
	public void setProjectContribution(Double projectContribution) {
		this.projectContribution = projectContribution;
	}
	public String getProjectType() {
		return projectType;
	}
	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}
	public String getProjectTypeName() {
		return projectTypeName;
	}
	public void setProjectTypeName(String projectTypeName) {
		this.projectTypeName = projectTypeName;
	}
	public Double getProjectShareRatio() {
		return projectShareRatio;
	}
	public void setProjectShareRatio(Double projectShareRatio) {
		this.projectShareRatio = projectShareRatio;
	}
	public String getProjectProgress() {
		return projectProgress;
	}
	public void setProjectProgress(String projectProgress) {
		this.projectProgress = projectProgress;
	}
	public String getProjectProgressName() {	
		return projectProgressName;
	}
	public void setProjectProgressName(String projectProgressName) {
		this.projectProgressName = projectProgressName;
	}
	public Long getProjectDepartid() {
		return projectDepartid;
	}
	public void setProjectDepartid(Long projectDepartid) {
		this.projectDepartid = projectDepartid;
	}
	public String getProjectCareerline() {
		return projectCareerline;
	}
	public void setProjectCareerline(String projectCareerline) {
		this.projectCareerline = projectCareerline;
	}
   
}
