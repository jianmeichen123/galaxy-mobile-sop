package com.galaxyinternet.vo;

import java.math.BigDecimal;

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
	private String projectProgress; //项目阶段编码
	private String projectProgressName; //项目阶段名称
	private String projectCareerline; //项目所属投资事业线名称（部门名称）
    private Long projectDepartid;//项目所属部门id
//	private String projectStatus;//项目状态编码  
    
    //###############################

    private String projectCode ;
    private Long ideaId ;
    private Integer stockTransfer ; //是否涉及股权转让，0表示不涉及，1表示涉及'
       
    private Long industryOwn ;// '行业归属ID'
	private String industryOwnStr ; //行业归属名称
    
    private BigDecimal  projectValuations ; // '初始估值',
    private BigDecimal  finalValuations ; // '实际估值',
    private String financeStatus; // '融资状态',
  
    private BigDecimal finalContribution; // '实际投资额',
    private Integer  currencyUnit ; // '资金单位，0表示人民币，1表示美元，2表示英镑，3表示欧元',
    private BigDecimal projectShareRatio ; // '所占股份百分比',
    private BigDecimal finalShareRatio ;//'实际所占股份百分比',
    private String projectCompany ; // COMMENT '公司名称',
    private Long formationDate; // '公司成立时间',
    private String companyLegal ;// '公司法人',
    private String projectCompanyCode ; // '组织机构代码',
    private Long createUid ; // '项目创建者的用户ID',
    private String createUname; // '项目创建者名',
    private String createUposition ; // '字典 项目创建者的职位',

    private String projectStatus; // '0:跟进中,1:投后运营,2:已否决,3:已退出',
    //private  project_describe` text COLLATE utf8_bin COMMENT '项目的概述',
   // private project_business_model` text COLLATE utf8_bin COMMENT '项目的商业模式描述',
   // private company_location` text COLLATE utf8_bin COMMENT '公司的发展定位',
   // private user_portrait` text COLLATE utf8_bin COMMENT '用户画像，即服务客户的描述信息',
   // private prospect_analysis` text COLLATE utf8_bin COMMENT '尽情分析，即项目前景分析',
  //  private next_financing_source` text COLLATE utf8_bin COMMENT '下一轮融资路径',
 //   private industry_analysis` text COLLATE utf8_bin COMMENT '行业分析',
  //  private operational_data` text COLLATE utf8_bin COMMENT '运营数据',
    private Long updatedTime ; // '项目的最近一次修改日期',
    private Long createdTime ; // '项目的创建日期'

    //###############################
    public String getIndustryOwnStr() {
		return industryOwnStr;
	}
	public void setIndustryOwnStr(String industryOwnStr) {
		this.industryOwnStr = industryOwnStr;
	}
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
	public String getProjectCode() {
		return projectCode;
	}
	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}
	public Long getIdeaId() {
		return ideaId;
	}
	public void setIdeaId(Long ideaId) {
		this.ideaId = ideaId;
	}
	public Integer getStockTransfer() {
		return stockTransfer;
	}
	public void setStockTransfer(Integer stockTransfer) {
		this.stockTransfer = stockTransfer;
	}
	public Long getIndustryOwn() {
		return industryOwn;
	}
	public void setIndustryOwn(Long industryOwn) {
		this.industryOwn = industryOwn;
	}
	public BigDecimal getProjectValuations() {
		return projectValuations;
	}
	public void setProjectValuations(BigDecimal projectValuations) {
		this.projectValuations = projectValuations;
	}
	public BigDecimal getFinalValuations() {
		return finalValuations;
	}
	public void setFinalValuations(BigDecimal finalValuations) {
		this.finalValuations = finalValuations;
	}
	public String getFinanceStatus() {
		return financeStatus;
	}
	public void setFinanceStatus(String financeStatus) {
		this.financeStatus = financeStatus;
	}
	public BigDecimal getFinalContribution() {
		return finalContribution;
	}
	public void setFinalContribution(BigDecimal finalContribution) {
		this.finalContribution = finalContribution;
	}
	public Integer getCurrencyUnit() {
		return currencyUnit;
	}
	public void setCurrencyUnit(Integer currencyUnit) {
		this.currencyUnit = currencyUnit;
	}
	public BigDecimal getProjectShareRatio() {
		return projectShareRatio;
	}
	public void setProjectShareRatio(BigDecimal projectShareRatio) {
		this.projectShareRatio = projectShareRatio;
	}
	public BigDecimal getFinalShareRatio() {
		return finalShareRatio;
	}
	public void setFinalShareRatio(BigDecimal finalShareRatio) {
		this.finalShareRatio = finalShareRatio;
	}
	public String getProjectCompany() {
		return projectCompany;
	}
	public void setProjectCompany(String projectCompany) {
		this.projectCompany = projectCompany;
	}
	public Long getFormationDate() {
		return formationDate;
	}
	public void setFormationDate(Long formationDate) {
		this.formationDate = formationDate;
	}
	public String getCompanyLegal() {
		return companyLegal;
	}
	public void setCompanyLegal(String companyLegal) {
		this.companyLegal = companyLegal;
	}
	public String getProjectCompanyCode() {
		return projectCompanyCode;
	}
	public void setProjectCompanyCode(String projectCompanyCode) {
		this.projectCompanyCode = projectCompanyCode;
	}
	public Long getCreateUid() {
		return createUid;
	}
	public void setCreateUid(Long createUid) {
		this.createUid = createUid;
	}
	public String getCreateUname() {
		return createUname;
	}
	public void setCreateUname(String createUname) {
		this.createUname = createUname;
	}
	public String getCreateUposition() {
		return createUposition;
	}
	public void setCreateUposition(String createUposition) {
		this.createUposition = createUposition;
	}
	public String getProjectStatus() {
		return projectStatus;
	}
	public void setProjectStatus(String projectStatus) {
		this.projectStatus = projectStatus;
	}
	public Long getUpdatedTime() {
		return updatedTime;
	}
	public void setUpdatedTime(Long updatedTime) {
		this.updatedTime = updatedTime;
	}
	public Long getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Long createdTime) {
		this.createdTime = createdTime;
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
