package com.galaxyinternet.project.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.galaxyinternet.bo.project.MeetingSchedulingBo;
import com.galaxyinternet.bo.project.ProjectBo;
import com.galaxyinternet.common.controller.BaseControllerImpl;
import com.galaxyinternet.common.enums.DictEnum;
import com.galaxyinternet.exception.PlatformException;
import com.galaxyinternet.framework.core.constants.UserConstant;
import com.galaxyinternet.framework.core.model.Page;
import com.galaxyinternet.framework.core.model.PageRequest;
import com.galaxyinternet.framework.core.model.ResponseData;
import com.galaxyinternet.framework.core.model.Result;
import com.galaxyinternet.framework.core.model.Result.Status;
import com.galaxyinternet.framework.core.service.BaseService;
import com.galaxyinternet.model.department.Department;
import com.galaxyinternet.model.dict.Dict;
import com.galaxyinternet.model.project.AppMeetScheduling;
import com.galaxyinternet.model.project.Project;
import com.galaxyinternet.model.user.User;
import com.galaxyinternet.service.AppProjectService;
import com.galaxyinternet.service.AppProjecttService;
import com.galaxyinternet.service.DepartmentService;
import com.galaxyinternet.service.DictService;
import com.galaxyinternet.service.MeetingSchedulingService;
import com.galaxyinternet.service.ProjectService;
import com.galaxyinternet.service.UserRoleService;
import com.galaxyinternet.vo.GeneralProjectVO;
import com.galaxyinternet.vo.GeneralProjecttVO;
import com.galaxyinternet.vo.ProjectVO;
/**
 * ios对接接口
 * @author gxc
 *
 */
@Controller
@RequestMapping("/galaxy/aproject")
public class AppProjectController extends BaseControllerImpl<Project, ProjectBo> {
	
	final Logger logger = LoggerFactory.getLogger(AppProjectController.class);
	@Autowired
	private AppProjectService appProjectService;
	@Autowired
	private AppProjecttService appProjecttService;
	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private MeetingSchedulingService meetingSchedulingService;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private UserRoleService userRoleService;
	@Autowired
	private DictService dictService;
	@Autowired
	com.galaxyinternet.framework.cache.Cache cache;
	
	@Override
	protected BaseService<Project> getBaseService() {
		return this.projectService;
	}	
	
	 /**
	  * 供app端根据角色不同查看不同的项目列表
	  * 版本（1.0-1.2）
	  * 
	  */
	   	@ResponseBody
		@RequestMapping(value = "/splapp", produces = MediaType.APPLICATION_JSON_VALUE)
		public ResponseData<Project> searchAppProjectList(HttpServletRequest request, @RequestBody ProjectBo project) {
			ResponseData<Project> responseBody = new ResponseData<Project>();
			User user = (User) getUserFromSession(request);
			List<Long> roleIdList = userRoleService.selectRoleIdByUserId(user.getId());
			if(project.getProjectProgress()!=null&&project.getProjectProgress().equals("guanbi")){
				project.setProjectStatus("meetingResult:3");
				project.setProjectProgress(null);
			}
			if (roleIdList.contains(UserConstant.HHR)){
				project.setProjectDepartid(user.getDepartmentId());
			}
			if(!roleIdList.contains(UserConstant.DSZ) && !roleIdList.contains(UserConstant.CEO)&&!roleIdList.contains(UserConstant.HHR)){
				project.setCreateUid(user.getId());
			}
				
			try {						
				Page<Project>  pageProject = null;
				if(project.getAscOrDes()!=null&&project.getCascOrDes()!=null){	
					if(project.getAscOrDes().equals("desc")){
						Sort sort = new Sort(Direction.DESC,project.getCascOrDes());
						 pageProject = projectService.queryPageList(project,new PageRequest(project.getPageNum(), project.getPageSize(),sort));						
					}else if(project.getAscOrDes().equals("asc")){
						Sort sort = new Sort(Direction.ASC,project.getCascOrDes());
						pageProject= projectService.queryPageList(project,new PageRequest(project.getPageNum(), project.getPageSize(),sort));	
					}													
				}else{
					pageProject= projectService.queryPageList(project,new PageRequest(project.getPageNum(), project.getPageSize()));				
				}	
				if(pageProject!=null){
				    if(pageProject.getContent().isEmpty())	{
				    	List<Project> p=new ArrayList<Project>();
				    	pageProject.setContent(p);
				    	pageProject.setTotal((long)0);
				   }else{
					   for(int i=0;i<pageProject.getContent().size();i++){
			    			Project p=pageProject.getContent().get(i);
							Department Department=new Department();
							Department.setId(p.getProjectDepartid());
							Department queryOne = departmentService.queryOne(Department);
							if(queryOne!=null){
								p.setProjectCareerline(queryOne.getName());
							}else{
								p.setProjectCareerline("");
							}
					   }
				  }
				}
				responseBody.setPageList(pageProject);
				responseBody.setResult(new Result(Status.OK, ""));
				return responseBody;
			} catch (PlatformException e) {
				responseBody.setResult(new Result(Status.ERROR, "queryUserList faild"));
				if (logger.isErrorEnabled()) {
					logger.error("queryUserList ", e);
				}
			}
			return responseBody;
		}
	   	
	   	/**
	   	 * 移动端查询项目列表<br>
	   	 * 查询条件：<br>
	   	 * &nbsp;1.项目状态 projectStatus<br>
	   	 * &nbsp;2.当前页号pageNum<br>
	   	 * &nbsp;3.每页记录数pageSize<br>
	   	 * &nbsp;4.其它字段项目表字段，暂不列出
	   	 *  版本（1.3）<br>
	   	 * @param request
	   	 * @param project
	   	 * @return
	   	 */
		@ResponseBody
		@RequestMapping(value = "/getProjectList",  method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
		public ResponseData<GeneralProjectVO> queryProjectListByParam(HttpServletRequest request, @RequestBody ProjectBo projectBo) {		
			User user = (User) getUserFromSession(request);			
			ResponseData<GeneralProjectVO> responseBody = new ResponseData<GeneralProjectVO>();
			GeneralProjectVO genProjectBean = new GeneralProjectVO();
			try{
				if(user==null){
					responseBody.setResult(new Result(Status.ERROR, "User用户信息在Session中不存在，无法执行项目列表查询！"));
					return responseBody;
				}
				List<Long> roleIdList = userRoleService.selectRoleIdByUserId(user.getId());
				if(roleIdList==null || roleIdList.size()==0){
					responseBody.setResult(new Result(Status.ERROR, "当前用户未配置任何角色，将不执行项目列表查询功能！"));
					return responseBody;
				}
				//根据登录人的角色，设置不同的待查询项（备注：董事长和CEO全查，获取全部）
				if(roleIdList.contains(UserConstant.TZJL)){//投资经理
					projectBo.setCreateUid(user.getId()); //项目创建者
				}else if (roleIdList.contains(UserConstant.HHR)){//合伙人
					projectBo.setProjectDepartid(user.getDepartmentId());//所属部门（事业线）ID
				}				
				/* =====================================
				 * 【临时】变更1.3的需求逻辑，待新版本时，可注释以下代码
				 * =====================================
				 */
				
				//===================================GJZ Count
				ProjectBo gjzPb = new ProjectBo();
				gjzPb.setProjectStatus(DictEnum.projectStatus.GJZ.getCode());
				if(projectBo.getCreateUid()!=null){
					gjzPb.setCreateUid(projectBo.getCreateUid());
				}
				if (projectBo.getProjectDepartid()!=null){
					gjzPb.setProjectDepartid(projectBo.getProjectDepartid());
				}
				if(StringUtils.isNotBlank(projectBo.getFinanceStatus())){
					gjzPb.setFinanceStatus(projectBo.getFinanceStatus());
				}
				if(StringUtils.isNotBlank(projectBo.getProjectType())){
					gjzPb.setProjectType(projectBo.getProjectType());
				}
				if(StringUtils.isNotBlank(projectBo.getProjectProgress())){
					if(projectBo.getProjectProgress().equals(DictEnum.projectProgress.投后运营.getCode())){
						gjzPb.setProjectProgress(null);
					}else{
						gjzPb.setProjectProgress(projectBo.getProjectProgress());
					}	
				}
				long gjzNum = appProjectService.queryCountProjectByParam(gjzPb);
				//===================================GJZ Count
				
				//===================================YFJ Count
				ProjectBo yfjPb = new ProjectBo();
				yfjPb.setProjectStatus(DictEnum.projectStatus.YFJ.getCode());
				if(projectBo.getCreateUid()!=null){
					yfjPb.setCreateUid(projectBo.getCreateUid());
				}
				if (projectBo.getProjectDepartid()!=null){
					yfjPb.setProjectDepartid(projectBo.getProjectDepartid());
				}
				if(StringUtils.isNotBlank(projectBo.getFinanceStatus())){
					yfjPb.setFinanceStatus(projectBo.getFinanceStatus());
				}
				if(StringUtils.isNotBlank(projectBo.getProjectType())){
					yfjPb.setProjectType(projectBo.getProjectType());
				}
				if(StringUtils.isNotBlank(projectBo.getProjectProgress())){
					if(projectBo.getProjectProgress().equals(DictEnum.projectProgress.投后运营.getCode())){
						yfjPb.setProjectProgress(null);
					}else{
						yfjPb.setProjectProgress(projectBo.getProjectProgress());
					}				
				}
				long yfjNum = appProjectService.queryCountProjectByParam(yfjPb);
				//===================================YFJ Count
				
				//===================================thyy Count
				ProjectBo thyyPb = new ProjectBo();
//				thyyPb.setProjectStatus(DictEnum.projectStatus.THYY.getCode());
				if(projectBo.getCreateUid()!=null){
					thyyPb.setCreateUid(projectBo.getCreateUid());
				}
				if (projectBo.getProjectDepartid()!=null){
					thyyPb.setProjectDepartid(projectBo.getProjectDepartid());
				}
				if(StringUtils.isNotBlank(projectBo.getFinanceStatus())){
					thyyPb.setFinanceStatus(projectBo.getFinanceStatus());
				}
				if(StringUtils.isNotBlank(projectBo.getProjectType())){
					thyyPb.setProjectType(projectBo.getProjectType());
				}
				thyyPb.setProjectProgress(DictEnum.projectProgress.投后运营.getCode());				
				long thyyNum = appProjectService.queryCountProjectByParam(thyyPb);
				//===================================thyy Count  
				
//			if (!StringUtils.isNotBlank(projectBo.getProjectProgress())
//					&& StringUtils.isNotBlank(projectBo.getProjectStatus())
//					&& projectBo.getProjectStatus().equals(DictEnum.projectStatus.GJZ.getCode())) {
//				
//				if( gjzNum!=0 && gjzNum>thyyNum ){
//					gjzNum -= thyyNum;
//				}
//				
//			}
				
				if ( StringUtils.isNotBlank(projectBo.getProjectStatus()) && projectBo.getProjectStatus().equals(DictEnum.projectStatus.YFJ.getCode()) ){
					
					if( StringUtils.isNotBlank(projectBo.getProjectProgress())  &&  projectBo.getProjectProgress().equals(DictEnum.projectProgress.投后运营.getCode()) ){				
						if( gjzNum!=0 && gjzNum>=thyyNum ){
							gjzNum -= thyyNum;
						}	
					}else if (StringUtils.isBlank(projectBo.getProjectProgress())  ){
						if( gjzNum!=0 && gjzNum>=thyyNum ){
							gjzNum -= thyyNum;
						}	
					}	
				} else if ( StringUtils.isNotBlank(projectBo.getProjectStatus())  && projectBo.getProjectStatus().equals(DictEnum.projectStatus.GJZ.getCode()) ){
					   
						if( StringUtils.isNotBlank(projectBo.getProjectProgress())  &&  projectBo.getProjectProgress().equals(DictEnum.projectProgress.投后运营.getCode()) ){				
							if( gjzNum!=0 && gjzNum>=thyyNum ){
								gjzNum -= thyyNum;
							}	
						}else if (StringUtils.isBlank(projectBo.getProjectProgress())  ){
							if( gjzNum!=0 && gjzNum>=thyyNum ){
								gjzNum -= thyyNum;
							}	
						}
						
				}else if ( StringUtils.isNotBlank(projectBo.getThyyFlag()) && projectBo.getThyyFlag().equals("1") ){					
					if( StringUtils.isNotBlank(projectBo.getProjectProgress())  &&  projectBo.getProjectProgress().equals(DictEnum.projectProgress.投后运营.getCode()) ){
						if( gjzNum!=0 && gjzNum>=thyyNum ){
							gjzNum -= thyyNum;
						}
					}else if (StringUtils.isBlank(projectBo.getProjectProgress())  ){
						if( gjzNum!=0 && gjzNum>=thyyNum ){
							gjzNum -= thyyNum;
						}	
					}
					 
				}
				
				/*=====================================
				 * =====================================
				 */              
//				projectBo.setSorting("project_type ASC,project_progress DESC,created_time DESC,updated_time DESC ");	
				if(StringUtils.isNotBlank(projectBo.getThyyFlag()) && projectBo.getThyyFlag().equals("1")){
					projectBo.setProjectProgress(DictEnum.projectProgress.投后运营.getCode());
				}
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(new Order(Direction.DESC, "updated_time"));
				orderList.add(new Order(Direction.DESC, "created_time"));			
//				orderList.add(new Order(Direction.ASC, "project_type"));
//				orderList.add(new Order(Direction.DESC, "project_progress"));
				Sort sort = new Sort(orderList);	
				genProjectBean = appProjectService.queryPagingProjectList(projectBo, new PageRequest(projectBo.getPageNum(), projectBo.getPageSize() , sort));
				
				if(genProjectBean.getPvPage().getContent()==null || genProjectBean.getPvPage().getContent().size()==0){
					genProjectBean.getPvPage().setContent(new ArrayList<ProjectVO>());
					genProjectBean.getPvPage().setTotal(0L);
				}else{
					Page<ProjectVO> page = genProjectBean.getPvPage();
					List<ProjectVO> filterList = page.getContent();
                    
				   List<ProjectVO> filterIndex = new ArrayList<ProjectVO>();
					 for(int i=0;i<filterList.size();i++){
			    			ProjectVO probean = filterList.get(i);
			    			/* =============================================
			    			 * 临时增加针对1.3特别逻辑处理的功能，待新版本时，可注释或删除以下代码
			    			 * =============================================
			    			 */
			    			 if(StringUtils.isNotBlank(probean.getProjectProgress()) && StringUtils.isNotBlank(projectBo.getProjectStatus()) ){
									if (projectBo.getProjectStatus().equals(DictEnum.projectStatus.GJZ.getCode())
											&& probean.getProjectProgress().equals(DictEnum.projectProgress.投后运营.getCode())) {
										
										filterIndex .add(probean) ;										
									}
			    			 }		    			
			    			/* =============================================
			    			 * =============================================
			    			 * =============================================
			    			 */
			    			
							Department Department=new Department();
							if(StringUtils.isNotBlank(probean.getProjectDepartid().toString())){
								Department.setId(probean.getProjectDepartid());
								Department queryOne = departmentService.queryOne(Department);
								if(queryOne!=null){
									probean.setProjectCareerline(queryOne.getName());
								}else{
									probean.setProjectCareerline("");
								}
							}												
							Department dt=new Department();
							if(StringUtils.isNotBlank(probean.getIndustryOwn().toString())){
								dt.setId(probean.getIndustryOwn());
								Department queryDep = departmentService.queryOne(dt);
								if(queryDep!=null){
									probean.setIndustry(queryDep.getName());
								}else{
									probean.setIndustry("");
								}
							}
						    /*
						     * #project_valuations 初始估值 #final_valuations 实际估值 #project_contribution 初始投资额 
						     * #final_contribution 实际投资额  #project_share_ratio 所占股份百分比  #final_share_ratio 实际所占股份百分比
						     * 新项目保存时，上述字段值数字（未有公式运算或转换处理），全是直接存储
						     */
							if(StringUtils.isNotBlank(probean.getProjectType())){
								probean.setProjectTypeName(DictEnum.projectType.getNameByCode(probean.getProjectType()));//项目类型名称
							}
							if(StringUtils.isNotBlank(probean.getProjectProgress())){
								probean.setProjectProgressName(DictEnum.projectProgress.getNameByCode(probean.getProjectProgress()));//项目进度名称
							}	
							if(StringUtils.isNotBlank(probean.getFinanceStatus())){
								probean.setFinanceStatusName(DictEnum.financeStatus.getNameByCode(probean.getFinanceStatus()));//融资状态名称
							}
							if(StringUtils.isNotBlank(probean.getProjectStatus())){
								probean.setProjectStatusName(DictEnum.projectStatus.getNameByCode(probean.getProjectStatus()));//项目状态编码
							}
					 }	
					 if(filterIndex!=null && filterIndex.size()>0){
						 
						 for(int j=0; j<filterIndex.size(); j++){
							 filterList.remove( filterIndex.get(j) );
						 }
						
					 }
					 page.setContent(filterList);
					 genProjectBean.setPvPage(page);
				}
				//1.3原版暂时注释，待新版本时，释放=======================
//				projectBo.setProjectStatus(DictEnum.projectStatus.GJZ.getCode());
//				long gjzNum = appProjectService.queryCountProjectByParam(projectBo);
//				projectBo.setProjectStatus(DictEnum.projectStatus.THYY.getCode());
//				long thyyNum = appProjectService.queryCountProjectByParam(projectBo);	
//				projectBo.setProjectStatus(DictEnum.projectStatus.YFJ.getCode());
//				long yfjNum = appProjectService.queryCountProjectByParam(projectBo);
				//1.3原版========================================
				
				genProjectBean.setGjzCount(gjzNum);
				genProjectBean.setThyyCount(thyyNum);
				genProjectBean.setYfjCount(yfjNum);
				
			}catch(Exception ex){
				logger.error("移动端后台查询项目列表异常", ex);
				responseBody.setResult(new Result(Status.ERROR, "","移动端-查询项目列表后台异常"));
				return responseBody;
			}
			responseBody.setEntity(genProjectBean);
			responseBody.setResult(new Result(Status.OK, ""));
			return responseBody;
		}
		//
		@ResponseBody
		@RequestMapping(value = "/countThyySum",  method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
		public ResponseData<GeneralProjectVO> queryCountThyyProjectListByParam(HttpServletRequest request, @RequestBody ProjectBo projectBo){
			User user = (User) getUserFromSession(request);			
			ResponseData<GeneralProjectVO> responseBody = new ResponseData<GeneralProjectVO>();
			GeneralProjectVO genProjectBean = new GeneralProjectVO();
			try{
				System.out.println("#############项目名称:  " + projectBo.getProjectName());
//     			System.out.println("###################项目进度编码:  "+projectBo.getProjectProgress());
//				System.out.println("###################项目类型:  "+projectBo.getProjectType() );		
				System.out.println("###################融资状态:  "+projectBo.getFinanceStatus() );			
			}catch(Exception ex){
				logger.error("移动端后台查询项目列表异常", ex);
				responseBody.setResult(new Result(Status.ERROR, "","移动端-查询项目列表后台异常"));
				return responseBody;
			}
			responseBody.setEntity(genProjectBean);
			responseBody.setResult(new Result(Status.OK, ""));
			return responseBody;
		}

		/**
		 * 测试项目列表
		 * @param request
		 * @param projectBo
		 * @return
		 */
			@ResponseBody
			@RequestMapping(value = "/queryProjectListByTdj",  method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
			public ResponseData<GeneralProjecttVO> queryProjectListByTdj(HttpServletRequest request, @RequestBody ProjectBo projectBo) {		
				User user = (User) getUserFromSession(request);			
				ResponseData<GeneralProjecttVO> responseBody = new ResponseData<GeneralProjecttVO>();
				GeneralProjecttVO genProjectBean = new GeneralProjecttVO();
				
				try{
					if(user==null){
						responseBody.setResult(new Result(Status.ERROR, "User用户信息在Session中不存在，无法执行项目列表查询！"));
						return responseBody;
					}
					List<Long> roleIdList = userRoleService.selectRoleIdByUserId(user.getId());
					if(roleIdList==null || roleIdList.size()==0){
						responseBody.setResult(new Result(Status.ERROR, "当前用户未配置任何角色，将不执行项目列表查询功能！"));
						return responseBody;
					}
					//根据登录人的角色，设置不同的待查询项（备注：董事长和CEO全查，获取全部）
					if(roleIdList.contains(UserConstant.TZJL)){//投资经理
						projectBo.setCreateUid(user.getId()); //项目创建者
					}else if (roleIdList.contains(UserConstant.HHR)){//合伙人
						projectBo.setProjectDepartid(user.getDepartmentId());//所属部门（事业线）ID
					}		
					List<Order> orderList = new ArrayList<Order>();
					orderList.add(new Order(Direction.DESC, "updated_time"));
					orderList.add(new Order(Direction.DESC, "created_time"));			
		
					Sort sort = new Sort(orderList);
					if(projectBo.getSflag()==1){
						//跟进中
						genProjectBean = appProjecttService.querygjzProjectList(projectBo, new PageRequest(projectBo.getPageNum(), projectBo.getPageSize() , sort));
					}
					if(projectBo.getSflag()==2){
						//投后运营
						genProjectBean = appProjecttService.querythyyList(projectBo, new PageRequest(projectBo.getPageNum(), projectBo.getPageSize() , sort));
					}
					if(projectBo.getSflag()==3){
						//否决
						genProjectBean = appProjecttService.queryfjList(projectBo, new PageRequest(projectBo.getPageNum(), projectBo.getPageSize() , sort));
					}
					if(projectBo.getSflag()==4){
						if(projectBo.getKeyword()!=null){
							projectBo.setCeeword(projectBo.getKeyword().toUpperCase());
						
						}
						genProjectBean = appProjecttService.queryPageList(projectBo,  new PageRequest(projectBo.getPageNum(), projectBo.getPageSize(),sort));
					}
					if(genProjectBean.getPvPage().getContent()==null || genProjectBean.getPvPage().getContent().size()==0){
						genProjectBean.getPvPage().setContent(new ArrayList<Project>());
						genProjectBean.getPvPage().setTotal(0L);
					}else{
						Page<Project> page = genProjectBean.getPvPage();
						List<Project> filterList = page.getContent();
	                    
					 
						 for(int i=0;i<filterList.size();i++){
							 Project probean = filterList.get(i);				    			
								Department Department=new Department();
								if(StringUtils.isNotBlank(probean.getProjectDepartid().toString())){
									Department.setId(probean.getProjectDepartid());
									Department queryOne = departmentService.queryOne(Department);
									if(queryOne!=null){
										probean.setProjectCareerline(queryOne.getName());
									}else{
										probean.setProjectCareerline("");
									}
								}
								
								//financeStatus
/*								Department dt=new Department();
								if(StringUtils.isNotBlank(probean.getIndustryOwn().toString())){
									dt.setId(probean.getIndustryOwn());
									Department queryDep = departmentService.queryOne(dt);
									if(queryDep!=null){
										probean.setIndustry(queryDep.getName());//行业归属名称
									}else{
										probean.setIndustry("");
									}
								}
								*/
								//2016/12/13修改行业归属								
								if(StringUtils.isNotBlank(probean.getIndustryOwn().toString())){
								//	dt.setId(probean.getIndustryOwn());
								/*	Dict dict = new Dict();
									dict.setParentCode("industryOwn");*/
								//	dict.setCode(code);
									String queryDep = DictEnum.industryOwn.getNameByCode(probean.getIndustryOwn().toString());
								//	String queryDep=dictService.selectbypd(dict)
									if(queryDep!=null){
										probean.setIndustry(queryDep);//行业归属名称
									}else{
										probean.setIndustry("");
									}
								}
							    /*
							     * #project_valuations 初始估值 #final_valuations 实际估值 #project_contribution 初始投资额 
							     * #final_contribution 实际投资额  #project_share_ratio 所占股份百分比  #final_share_ratio 实际所占股份百分比
							     * 新项目保存时，上述字段值数字（未有公式运算或转换处理），全是直接存储
							     */
								if(StringUtils.isNotBlank(probean.getProjectType())){
									probean.setProjectTypeName(DictEnum.projectType.getNameByCode(probean.getProjectType()));//项目类型名称
								}
								if(StringUtils.isNotBlank(probean.getProjectProgress())){
									probean.setProjectProgressName(DictEnum.projectProgress.getNameByCode(probean.getProjectProgress()));//项目进度名称
								}	
								if(StringUtils.isNotBlank(probean.getFinanceStatus())){
									probean.setFinanceStatusName(DictEnum.financeStatus.getNameByCode(probean.getFinanceStatus()));//融资状态名称
								}
								if(StringUtils.isNotBlank(probean.getProjectStatus())){
									probean.setProjectStatusName(DictEnum.projectStatus.getNameByCode(probean.getProjectStatus()));//项目状态编码
								}
						 }	
						 
						 page.setContent(filterList);
						 genProjectBean.setPvPage(page);
					}
					Long gjzNum = appProjecttService.queryProjectgjzCount(projectBo);
					
					Long thyyNum = appProjecttService.queryProjectthyyCount(projectBo);
					
					Long yfjNum = appProjecttService.queryProjectfjCount(projectBo);
					
					genProjectBean.setGjzCount(gjzNum);
					genProjectBean.setThyyCount(thyyNum);
					genProjectBean.setYfjCount(yfjNum);
					
				}catch(Exception ex){
					logger.error("移动端后台查询项目列表异常", ex);
					responseBody.setResult(new Result(Status.ERROR, "","移动端-查询项目列表后台异常"));
					return responseBody;
				}
				
				responseBody.setEntity(genProjectBean);
				responseBody.setResult(new Result(Status.OK, ""));
				return responseBody;
			
			}
			//TODO	
			/**
			 * 查询会议排期的日历页面
			 * @param request
			 * @param query
			 * @return
			 */
			@ResponseBody
			@RequestMapping(value = "/queryMeetSchedulingrl",  method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
			public ResponseData<MeetingSchedulingBo> queryMeetSchedulingrl(HttpServletRequest request, @RequestBody MeetingSchedulingBo query) {		
				ResponseData<MeetingSchedulingBo> responseBody = new ResponseData<MeetingSchedulingBo>();
				User user = (User) getUserFromSession(request);
				SimpleDateFormat ss = new SimpleDateFormat("yyyy-MM-dd");
	
					List<Long> roleIdList = userRoleService.selectRoleIdByUserId(user
						.getId());
					
					if(query.getYear()!=null&&query.getMonth()!=null){
						//获取传过来的年月的第一天
						Date d = getFirstDayOfMonth(Integer.valueOf(query.getYear()),Integer.valueOf(query.getMonth())-1);
						query.setStartTime(ss.format(d));
						//获取传过来的年月的最后一天
						Date f = getLastDayOfMonth(Integer.valueOf(query.getYear()),Integer.valueOf(query.getMonth())-1);
						query.setEndTime(ss.format(f));		
					}
				if (roleIdList.contains(UserConstant.TZJL)) {
					query.setUid(user.getId());					
				} else if (roleIdList.contains(UserConstant.HHR)) {
					query.setProjectDepartid(user.getDepartmentId());					
				} else if (roleIdList.contains(UserConstant.CEOMS)||roleIdList.contains(UserConstant.DMS)) {
				
					//需要进新接口  数目未排期的数目
					Long iu = meetingSchedulingService.selectdpqCount(query);
					responseBody.setId(iu);
				}
				
				try{
					
					
					Map<String, Object> depmap = new HashMap<String, Object>();
					List<MeetingSchedulingBo> listmb = meetingSchedulingService.selectMonthScheduling(query);
					for(MeetingSchedulingBo mo :listmb){						 						
						 String aa = ss.format(mo.getReserveTimeStart());
						 if(!depmap.containsKey(aa)){
							 AppMeetScheduling ams =new AppMeetScheduling();
							 query.setDayTime(aa);
							 query.setMeetingType(DictEnum.meetingType.立项会.getCode());							 
							 Long y = meetingSchedulingService.selectMonthSchedulingCount(query);
							 ams.setLxh(DictEnum.meetingType.立项会.getCode());
							 ams.setLxhCount(y);
							 query.setMeetingType(DictEnum.meetingType.投决会.getCode());
							 Long k = meetingSchedulingService.selectMonthSchedulingCount(query);
							 ams.setTjh(DictEnum.meetingType.投决会.getCode());
							 ams.setTjhCount(k);
							 query.setMeetingType(DictEnum.meetingType.CEO评审.getCode());
							 Long g = meetingSchedulingService.selectMonthSchedulingCount(query);
							 ams.setCeops(DictEnum.meetingType.CEO评审.getCode());
							 ams.setCeopsCount(g);
							 depmap.put(aa, ams);
						 }
					}
					
					//获取初始的当日事项
					MeetingSchedulingBo bop = new MeetingSchedulingBo();
					String bb = ss.format(new Date());
					bop.setDateTime(bb);
					if(query.getUid()!=null){
						bop.setUid(query.getUid());
					}
					if(query.getProjectDepartid()!=null){
						bop.setProjectDepartid(query.getProjectDepartid());
					}
					List<MeetingSchedulingBo> lisb = meetingSchedulingService.selectDayScheduling(bop);
					
					responseBody.setUserData(depmap);
					/*responseBody.setEntityList(listmb);*/
					responseBody.setEntityList(lisb);
					
				} catch (PlatformException e) {
					responseBody.setResult(new Result(Status.ERROR, null,
							"queryUserList faild"));
					if (logger.isErrorEnabled()) {
						logger.error("queryUserList ", e);
					}
				}
				return responseBody;
			
			}
			

			
			/**
			 * 排期日历的当日事项
			 * @param request
			 * @param query
			 * @return 2016/7/26 传入的是当日时间的string类型 dateTime "yyyy-MM-dd"
			 */
			@ResponseBody
			@RequestMapping(value = "/selectDayScheduling",  method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
			public ResponseData<MeetingSchedulingBo> selectDayScheduling(HttpServletRequest request, @RequestBody MeetingSchedulingBo query) {		
				ResponseData<MeetingSchedulingBo> responseBody = new ResponseData<MeetingSchedulingBo>();
				User user = (User) getUserFromSession(request);

				
				List<Long> roleIdList = userRoleService.selectRoleIdByUserId(user
						.getId());

				if (roleIdList.contains(UserConstant.TZJL)) {
					query.setUid(user.getId());					
				} else if (roleIdList.contains(UserConstant.HHR)) {
					query.setProjectDepartid(user.getDepartmentId());					
				} 
				
				try{
														

					List<MeetingSchedulingBo> lisb = meetingSchedulingService.selectDayScheduling(query);
					

					responseBody.setEntityList(lisb);
					
				} catch (PlatformException e) {
					responseBody.setResult(new Result(Status.ERROR, null,
							"selectDayScheduling faild"));
					if (logger.isErrorEnabled()) {
						logger.error("selectDayScheduling ", e);
					}
				}
				return responseBody;
			
			}
			
			
			
			//获取指定年月的第一天
			 public static Date getFirstDayOfMonth(Integer year, Integer month) {
			        Calendar calendar = Calendar.getInstance();
			        if (year == null) {
			            year = calendar.get(Calendar.YEAR);
			        }
			        if (month == null) {
			            month = calendar.get(Calendar.MONTH);
			        }
			        calendar.set(year, month, 1);
			        return calendar.getTime();
			    }
			//获取指定年月的最后一天
			 public static Date getLastDayOfMonth(Integer year, Integer month) {
			        Calendar calendar = Calendar.getInstance();
			        if (year == null) {
			            year = calendar.get(Calendar.YEAR);
			        }
			        if (month == null) {
			            month = calendar.get(Calendar.MONTH);
			        }
			        calendar.set(year, month, 1);
			        calendar.roll(Calendar.DATE, -1);
			        return calendar.getTime();
			    }

			
			
			 
			 
			 //为2016/10/14版本做准备
			 /**
				 * 测试项目列表
				 * @param request
				 * @param projectBo
				 * @return
				 */
					@ResponseBody
					@RequestMapping(value = "/selectProjectList",  method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
					public ResponseData<GeneralProjecttVO> selectProjectList(HttpServletRequest request, @RequestBody ProjectBo projectBo) {		
						User user = (User) getUserFromSession(request);			
						ResponseData<GeneralProjecttVO> responseBody = new ResponseData<GeneralProjecttVO>();
						GeneralProjecttVO genProjectBean = new GeneralProjecttVO();
						
						try{
							if(user==null){
								responseBody.setResult(new Result(Status.ERROR, "User用户信息在Session中不存在，无法执行项目列表查询！"));
								return responseBody;
							}
							List<Long> roleIdList = userRoleService.selectRoleIdByUserId(user.getId());
							if(roleIdList==null || roleIdList.size()==0){
								responseBody.setResult(new Result(Status.ERROR, "当前用户未配置任何角色，将不执行项目列表查询功能！"));
								return responseBody;
							}
							//根据登录人的角色，设置不同的待查询项（备注：董事长和CEO全查，获取全部）
/*							if(roleIdList.contains(UserConstant.TZJL)){//投资经理
								projectBo.setCreateUid(user.getId()); //项目创建者
							}else if (roleIdList.contains(UserConstant.HHR)){//合伙人
								projectBo.setProjectDepartid(user.getDepartmentId());//所属部门（事业线）ID
							}	*/	
							if(roleIdList.contains(UserConstant.TZJL)&&(projectBo.getProjectDepartid()==null)&&(projectBo.getCreateUid()==null)&&(projectBo.getQuanbu()==null)&&(projectBo.getDeptIdList()==null)){//投资经理
								projectBo.setCreateUid(user.getId()); //项目创建者
							}/*else if (roleIdList.contains(UserConstant.HHR)&&(projectBo.getProjectDepartid()==null)){// 合伙人 新增加条件 合伙人可查询别的事业线的项目
								projectBo.setProjectDepartid(user.getDepartmentId());//所属部门（事业线）ID
							}*//*else if(roleIdList.contains(UserConstant.TZJL)&&(projectBo.getCreateUid()!=null)){//投资经理  我的项目的时候
								
							}else if(roleIdList.contains(UserConstant.TZJL)&&(projectBo.getQuanbu()!=null)){//投资经理  全部的时候
								
							}else if(roleIdList.contains(UserConstant.TZJL)&&(projectBo.getProjectDepartid()!=null)){//投资经理  本事业部的时候
								
							}*/
							
							List<Order> orderList = new ArrayList<Order>();
							orderList.add(new Order(Direction.DESC, "updated_time"));
							orderList.add(new Order(Direction.DESC, "created_time"));			
				
							Sort sort = new Sort(orderList);
							if(projectBo.getSflag()==1){
								//跟进中
								genProjectBean = appProjecttService.querygjzProjectList(projectBo, new PageRequest(projectBo.getPageNum(), projectBo.getPageSize() , sort));
							}
							if(projectBo.getSflag()==2){
								//投后运营
								genProjectBean = appProjecttService.querythyyList(projectBo, new PageRequest(projectBo.getPageNum(), projectBo.getPageSize() , sort));
							}
							if(projectBo.getSflag()==3){
								//否决
								genProjectBean = appProjecttService.queryfjList(projectBo, new PageRequest(projectBo.getPageNum(), projectBo.getPageSize() , sort));
							}
							if(projectBo.getSflag()==4){
								if(projectBo.getKeyword()!=null){
									projectBo.setCeeword(projectBo.getKeyword().toUpperCase());
								
								}
								genProjectBean = appProjecttService.queryPageList(projectBo,  new PageRequest(projectBo.getPageNum(), projectBo.getPageSize(),sort));
							}
							if(genProjectBean.getPvPage().getContent()==null || genProjectBean.getPvPage().getContent().size()==0){
								genProjectBean.getPvPage().setContent(new ArrayList<Project>());
								genProjectBean.getPvPage().setTotal(0L);
							}else{
								Page<Project> page = genProjectBean.getPvPage();
								List<Project> filterList = page.getContent();
			                    
							 
								 for(int i=0;i<filterList.size();i++){
									 Project probean = filterList.get(i);				    			
										Department Department=new Department();
										if(StringUtils.isNotBlank(probean.getProjectDepartid().toString())){
											Department.setId(probean.getProjectDepartid());
											Department queryOne = departmentService.queryOne(Department);
											if(queryOne!=null){
												probean.setProjectCareerline(queryOne.getName());
											}else{
												probean.setProjectCareerline("");
											}
										}
										
										//financeStatus
										Department dt=new Department();
										if(StringUtils.isNotBlank(probean.getIndustryOwn().toString())){
											dt.setId(probean.getIndustryOwn());
											Department queryDep = departmentService.queryOne(dt);
											if(queryDep!=null){
												probean.setIndustry(queryDep.getName());//行业归属名称
											}else{
												probean.setIndustry("");
											}
										}
										
									    /*
									     * #project_valuations 初始估值 #final_valuations 实际估值 #project_contribution 初始投资额 
									     * #final_contribution 实际投资额  #project_share_ratio 所占股份百分比  #final_share_ratio 实际所占股份百分比
									     * 新项目保存时，上述字段值数字（未有公式运算或转换处理），全是直接存储
									     */
										if(StringUtils.isNotBlank(probean.getProjectType())){
											probean.setProjectTypeName(DictEnum.projectType.getNameByCode(probean.getProjectType()));//项目类型名称
										}
										if(StringUtils.isNotBlank(probean.getProjectProgress())){
											probean.setProjectProgressName(DictEnum.projectProgress.getNameByCode(probean.getProjectProgress()));//项目进度名称
										}	
										if(StringUtils.isNotBlank(probean.getFinanceStatus())){
											probean.setFinanceStatusName(DictEnum.financeStatus.getNameByCode(probean.getFinanceStatus()));//融资状态名称
										}
										if(StringUtils.isNotBlank(probean.getProjectStatus())){
											probean.setProjectStatusName(DictEnum.projectStatus.getNameByCode(probean.getProjectStatus()));//项目状态编码
										}
								 }	
								 
								 page.setContent(filterList);
								 genProjectBean.setPvPage(page);
							}
							Long gjzNum = appProjecttService.queryProjectgjzCount(projectBo);
							
							Long thyyNum = appProjecttService.queryProjectthyyCount(projectBo);
							
							Long yfjNum = appProjecttService.queryProjectfjCount(projectBo);
							
							genProjectBean.setGjzCount(gjzNum);
							genProjectBean.setThyyCount(thyyNum);
							genProjectBean.setYfjCount(yfjNum);
							
						}catch(Exception ex){
							logger.error("移动端后台查询项目列表异常", ex);
							responseBody.setResult(new Result(Status.ERROR, "","移动端-查询项目列表后台异常"));
							return responseBody;
						}
						
						responseBody.setEntity(genProjectBean);
						responseBody.setResult(new Result(Status.OK, ""));
						return responseBody;
					
					}
			
			
}
