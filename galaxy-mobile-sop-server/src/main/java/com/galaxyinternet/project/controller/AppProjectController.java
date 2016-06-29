package com.galaxyinternet.project.controller;

import java.util.ArrayList;
import java.util.List;
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
import com.galaxyinternet.model.project.Project;
import com.galaxyinternet.model.user.User;
import com.galaxyinternet.service.AppProjectService;
import com.galaxyinternet.service.DepartmentService;
import com.galaxyinternet.service.ProjectService;
import com.galaxyinternet.service.UserRoleService;
import com.galaxyinternet.vo.GeneralProjectVO;
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
	private DepartmentService departmentService;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private UserRoleService userRoleService;	
	@Autowired
	com.galaxyinternet.framework.cache.Cache cache;
	
	@Override
	protected BaseService<Project> getBaseService() {
		return this.projectService;
	}	
	
	 /**
	  * 
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
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(new Order(Direction.DESC, "updated_time"));
				orderList.add(new Order(Direction.DESC, "created_time"));			
//				orderList.add(new Order(Direction.ASC, "project_type"));
//				orderList.add(new Order(Direction.DESC, "project_progress"));
				Sort sort = new Sort(orderList);
//				projectBo.setSorting("project_type ASC,project_progress DESC,created_time DESC,updated_time DESC ");		
				genProjectBean = appProjectService.queryPagingProjectList(projectBo, new PageRequest(projectBo.getPageNum(), projectBo.getPageSize() , sort));
				
				if(genProjectBean.getPvPage().getContent()==null || genProjectBean.getPvPage().getContent().size()==0){
					genProjectBean.getPvPage().setContent(new ArrayList<ProjectVO>());
					genProjectBean.getPvPage().setTotal(0L);
				}else{
					Page<ProjectVO> page = genProjectBean.getPvPage();
					List<ProjectVO> filterList = page.getContent();
                    
				   List<Integer> filterIndex = new ArrayList<Integer>();
					 for(int i=0;i<filterList.size();i++){
			    			ProjectVO probean = filterList.get(i);
			    			/* =============================================
			    			 * 临时增加针对1.3特别逻辑处理的功能，待新版本时，可注释或删除以下代码
			    			 * =============================================
			    			 */
			    			 if(StringUtils.isNotBlank(probean.getProjectProgress()) && StringUtils.isNotBlank(projectBo.getProjectStatus()) ){
									if (projectBo.getProjectStatus().equals(DictEnum.projectStatus.GJZ.getCode())
											&& probean.getProjectProgress().equals(DictEnum.projectProgress.投后运营.getCode())) {
//										filterList.remove(i);
										filterIndex .add(i) ;
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
					 if(filterIndex!=null && filterIndex.size()>0){
						 
						 for(int j=0;j<filterIndex.size();j++){
							 filterList.remove(j);
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
				
				/* =====================================
				 * 【临时】变更1.3的需求逻辑，待新版本时，可注释以下代码
				 * =====================================
				 */
				
				projectBo.setProjectStatus(DictEnum.projectStatus.GJZ.getCode());
				if(StringUtils.isNotBlank(projectBo.getProjectProgress())  && projectBo.getProjectProgress().equals(DictEnum.projectProgress.投后运营.getCode())){
					projectBo.setProjectProgress(null);
				}
				long gjzNum = appProjectService.queryCountProjectByParam(projectBo);
								
				long thyyNum = 0L;
//				if(StringUtils.isNotBlank(projectBo.getThyyFlag()) && projectBo.getThyyFlag().equals("1") ){
					
				ProjectBo pb = new ProjectBo();				
				if(roleIdList.contains(UserConstant.TZJL)){//投资经理
					pb.setCreateUid(projectBo.getCreateUid());//项目创建者
				}else if (roleIdList.contains(UserConstant.HHR)){//合伙人
					pb.setProjectDepartid(projectBo.getProjectDepartid()); //所属部门（事业线）ID
				}
				pb.setProjectProgress(DictEnum.projectProgress.投后运营.getCode());
				thyyNum = appProjectService.queryCountProjectByParam(pb);
					
//				}else{
//	
//				}

				gjzNum-=thyyNum;
							
				projectBo.setProjectStatus(DictEnum.projectStatus.YFJ.getCode());
				if(StringUtils.isNotBlank(projectBo.getProjectProgress())  && projectBo.getProjectProgress().equals(DictEnum.projectProgress.投后运营.getCode())){
					projectBo.setProjectProgress(null);
				}
				long yfjNum = appProjectService.queryCountProjectByParam(projectBo);
				
				/* =====================================
				 * =====================================
				 * =====================================
				 */
				
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
