package com.galaxyinternet.project.controller;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.galaxyinternet.bo.PassRateBo;
import com.galaxyinternet.bo.SopTaskBo;
import com.galaxyinternet.bo.project.MeetingRecordBo;
import com.galaxyinternet.bo.project.MeetingSchedulingBo;
import com.galaxyinternet.bo.project.PersonPoolBo;
import com.galaxyinternet.bo.project.ProjectBo;
import com.galaxyinternet.bo.touhou.DeliveryBo;
import com.galaxyinternet.bo.touhou.ProjectHealthBo;
import com.galaxyinternet.common.SopResult;
import com.galaxyinternet.common.annotation.LogType;
import com.galaxyinternet.common.annotation.RecordType;
import com.galaxyinternet.common.constants.SopConstant;
import com.galaxyinternet.common.controller.BaseControllerImpl;
import com.galaxyinternet.common.enums.DictEnum;
import com.galaxyinternet.common.enums.EnumUtil;
import com.galaxyinternet.common.query.ProjectQuery;
import com.galaxyinternet.common.utils.ControllerUtils;
import com.galaxyinternet.exception.PlatformException;
import com.galaxyinternet.framework.core.config.PlaceholderConfigurer;
import com.galaxyinternet.framework.core.constants.Constants;
import com.galaxyinternet.framework.core.constants.UserConstant;
import com.galaxyinternet.framework.core.file.OSSHelper;
import com.galaxyinternet.framework.core.file.UploadFileResult;
import com.galaxyinternet.framework.core.form.Token;
import com.galaxyinternet.framework.core.id.IdGenerator;
import com.galaxyinternet.framework.core.model.Page;
import com.galaxyinternet.framework.core.model.PageRequest;
import com.galaxyinternet.framework.core.model.ResponseData;
import com.galaxyinternet.framework.core.model.Result;
import com.galaxyinternet.framework.core.model.Result.Status;
import com.galaxyinternet.framework.core.service.BaseService;
import com.galaxyinternet.framework.core.utils.DateUtil;
import com.galaxyinternet.framework.core.utils.GSONUtil;
import com.galaxyinternet.framework.core.utils.JSONUtils;
import com.galaxyinternet.framework.core.utils.mail.MailTemplateUtils;
import com.galaxyinternet.framework.core.utils.mail.SimpleMailSender;
import com.galaxyinternet.model.common.Config;
import com.galaxyinternet.model.department.Department;
import com.galaxyinternet.model.dict.Dict;
import com.galaxyinternet.model.operationLog.UrlNumber;
import com.galaxyinternet.model.project.FinanceHistory;
import com.galaxyinternet.model.project.FormatData;
import com.galaxyinternet.model.project.InterviewRecord;
import com.galaxyinternet.model.project.MeetingRecord;
import com.galaxyinternet.model.project.MeetingScheduling;
import com.galaxyinternet.model.project.PersonPool;
import com.galaxyinternet.model.project.Project;
import com.galaxyinternet.model.project.ProjectPerson;
import com.galaxyinternet.model.project.ProjectShares;
import com.galaxyinternet.model.project.ProjectTransfer;
import com.galaxyinternet.model.sopfile.SopFile;
import com.galaxyinternet.model.sopfile.SopVoucherFile;
import com.galaxyinternet.model.soptask.SopTask;
import com.galaxyinternet.model.timer.PassRate;
import com.galaxyinternet.model.touhou.ProjectHealth;
import com.galaxyinternet.model.user.User;
import com.galaxyinternet.model.user.UserRole;
import com.galaxyinternet.operationMessage.handler.StageChangeHandler;
import com.galaxyinternet.project.service.HandlerManager;
import com.galaxyinternet.project.service.handler.Handler;
import com.galaxyinternet.service.ConfigService;
import com.galaxyinternet.service.DeliveryService;
import com.galaxyinternet.service.DepartmentService;
import com.galaxyinternet.service.DictService;
import com.galaxyinternet.service.FinanceHistoryService;
import com.galaxyinternet.service.InterviewRecordService;
import com.galaxyinternet.service.MeetingRecordService;
import com.galaxyinternet.service.MeetingSchedulingService;
import com.galaxyinternet.service.PassRateService;
import com.galaxyinternet.service.PersonPoolService;
import com.galaxyinternet.service.ProjectHealthService;
import com.galaxyinternet.service.ProjectPersonService;
import com.galaxyinternet.service.ProjectService;
import com.galaxyinternet.service.ProjectSharesService;
import com.galaxyinternet.service.ProjectTransferService;
import com.galaxyinternet.service.SopFileService;
import com.galaxyinternet.service.SopTaskService;
import com.galaxyinternet.service.SopVoucherFileService;
import com.galaxyinternet.service.UserRoleService;
import com.galaxyinternet.service.UserService;

@Controller
@RequestMapping("/galaxy/project")
public class ProjectController extends BaseControllerImpl<Project, ProjectBo> {

	final Logger logger = LoggerFactory.getLogger(ProjectController.class);

	@Autowired
	private MeetingRecordService meetingService;
	@Autowired
	private DictService dictService;
	@Autowired
	private DeliveryService deliveryService;
	@Autowired
	private ProjectSharesService projectSharesService;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private UserRoleService userRoleService;
	@Autowired
	private PersonPoolService personPoolService;
	@Autowired
	private ProjectPersonService projectPersonService;
	@Autowired
	private UserService userService;
	@Autowired
	private ConfigService configService;
	@Autowired
	private MeetingRecordService meetingRecordService;
	@Autowired
	private InterviewRecordService interviewRecordService;
	@Autowired
	private SopFileService sopFileService;
	@Autowired
	private SopVoucherFileService sopVoucherFileService;
	@Autowired
	private MeetingSchedulingService meetingSchedulingService;
	@Autowired
	private HandlerManager handlerManager;

	@Autowired
	private ProjectHealthService projectHealthService;
	
	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private PassRateService passRateService;
	
	@Autowired
	private SopTaskService sopTaskService;

	@Autowired
	com.galaxyinternet.framework.cache.Cache cache;
	
	@Autowired
	private ProjectTransferService projectTransferService;

	
	//2016/11/21 增加新的service   为了 新增的历史融资 计划 项目详情要显示
	@Autowired
	private FinanceHistoryService financeHistoryService;
	
	
	
	
	private String tempfilePath;

	public String getTempfilePath() {
		return tempfilePath;
	}

	@Value("${sop.oss.tempfile.path}")
	public void setTempfilePath(String tempfilePath) {
		this.tempfilePath = tempfilePath;
	}

	@Override
	protected BaseService<Project> getBaseService() {
		return this.projectService;
	}

	/**
	 * 新建项目接口
	 * 
	 * @author yangshuhua
	 * @return 2016/6/13 修改过
	 */
	@Token
	@com.galaxyinternet.common.annotation.Logger(operationScope = LogType.MESSAGE)
	@ResponseBody
	@RequestMapping(value = "/ap", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<Project> addProject(@RequestBody Project project,
			HttpServletRequest request) {
		ResponseData<Project> responseBody = new ResponseData<Project>();
		if (project == null || project.getProjectName() == null
				|| "".equals(project.getProjectName().trim())
				|| project.getProjectType() == null
				|| "".equals(project.getProjectType().trim())
				|| project.getCreateDate() == null
				|| "".equals(project.getCreateDate().trim())
				|| project.getIndustryOwn() == null) {
			responseBody.setResult(new Result(Status.ERROR, null, "必要的参数丢失!"));
			return responseBody;
		}
		Object code = request.getSession().getAttribute(
				Constants.SESSION_PROJECT_CODE);
		if (code == null) {
			responseBody.setResult(new Result(Status.ERROR, null, "项目编码丢失!"));
			return responseBody;
		}
		Project obj = new Project();
		obj.setProjectName(project.getProjectName());
		List<Project> projectList = projectService.queryList(obj);
		
		/* * Integer count = 0 ; for (Project p: projectList) { count ++; }
		 * if(count>0){
		 */
		if (null != projectList && projectList.size() > 0) {
			responseBody.setResult(new Result(Status.ERROR, null, "项目名称重复!"));
			return responseBody;
		}
		User user = (User) getUserFromSession(request);
		// 判断当前用户是否为投资经理
		List<Long> roleIdList = userRoleService.selectRoleIdByUserId(user
				.getId());
		if (!roleIdList.contains(UserConstant.HHR)
				&& !roleIdList.contains(UserConstant.TZJL)) {
			responseBody.setResult(new Result(Status.ERROR, null, "没有权限添加项目!"));
			return responseBody;
		}

		project.setProjectCode(String.valueOf(code));
		if (project.getProjectValuations() == null) {
			if (project.getProjectShareRatio() != null
					&& project.getProjectShareRatio() > 0
					&& project.getProjectContribution() != null
					&& project.getProjectContribution() > 0) {
				project.setProjectValuations(project.getProjectContribution()
						* 100 / project.getProjectShareRatio());
			}
		}

		project.setStockTransfer(0);
		project.setCreateUid(user.getId());
		project.setCreateUname(user.getRealName());
		project.setProjectProgress(DictEnum.projectProgress.接触访谈.getCode());
		project.setProjectStatus(DictEnum.projectStatus.GJZ.getCode());
		// 获取当前登录人的部门信息
		Long did = user.getDepartmentId();
		project.setProjectDepartid(did);
		project.setUpdatedTime(new Date().getTime());
		//修改 项目时间 
		project.setProjectTime(new Date().getTime());
		try {
			project.setCreatedTime(DateUtil.convertStringToDate(
					project.getCreateDate().trim(), "yyyy-MM-dd").getTime());
			long id = projectService.newProject(project);
			if (id > 0) {
				responseBody.setResult(new Result(Status.OK, null, "项目添加成功!"));
				responseBody.setId(id);
				ControllerUtils.setRequestParamsForMessageTip(request,project.getProjectName(), project.getId(),StageChangeHandler._6_1_);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseBody;
	}

	/**
	 * 新建项目接口
	 * @version 2016-06-21
	 * 2016/8/30 考过了的
	 * @author yangshuhua
	 */
	@Token
	@com.galaxyinternet.common.annotation.Logger(operationScope = LogType.MESSAGE)
	@ResponseBody
	@RequestMapping(value = "/cjxiangmu", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<Project> cjxiangMu(@RequestBody Project project,
			HttpServletRequest request) {
		ResponseData<Project> responseBody = new ResponseData<Project>();
		if (project == null || project.getProjectName() == null
				|| "".equals(project.getProjectName().trim())
				|| project.getProjectType() == null
				|| "".equals(project.getProjectType().trim())
				|| project.getCreateDate() == null
				|| "".equals(project.getCreateDate().trim())
				|| project.getIndustryOwn() == null) {
			responseBody.setResult(new Result(Status.ERROR,"csds" , "必要的参数丢失!"));
			return responseBody;
		}
		try {
			User user = (User) getUserFromSession(request);
			// 判断当前用户是否为投资经理
			List<Long> roleIdList = userRoleService.selectRoleIdByUserId(user.getId());
			if (!roleIdList.contains(UserConstant.TZJL)) {
				responseBody.setResult(new Result(Status.ERROR, "myqx", "没有权限添加项目!"));
				return responseBody;
			}
			//验证项目名是否重复
			Project obj = new Project();
			obj.setProjectName(project.getProjectName());
			List<Project> projectList = projectService.queryList(obj);
			if (null != projectList && projectList.size() > 0) {
				responseBody.setResult(new Result(Status.ERROR, "mccf", "项目名重复!"));
				return responseBody;
			}
			//创建项目编码
			Config config = configService.createCode();
			NumberFormat nf = NumberFormat.getInstance();
			nf.setGroupingUsed(false);
			nf.setMaximumIntegerDigits(6);
			nf.setMinimumIntegerDigits(6);
			Long did = user.getDepartmentId();
			if (did != null) {
				int code = EnumUtil.getCodeByCareerline(did.longValue());
				String projectCode = String.valueOf(code) + nf.format(Integer.parseInt(config.getValue()));
				project.setProjectCode(String.valueOf(projectCode));
				
				if (project.getProjectValuations() == null) {
					if (project.getProjectShareRatio() != null
							&& project.getProjectShareRatio() > 0
							&& project.getProjectContribution() != null
							&& project.getProjectContribution() > 0) {
						project.setProjectValuations(project.getProjectContribution() * 100 / project.getProjectShareRatio());
					}
				}
				project.setCurrencyUnit(0);
				//默认不涉及股权转让
				project.setStockTransfer(0);
				project.setCreateUid(user.getId());
				project.setCreateUname(user.getRealName());
				project.setProjectDepartid(did);
				project.setProjectProgress(DictEnum.projectProgress.接触访谈.getCode());
				project.setProjectStatus(DictEnum.projectStatus.GJZ.getCode());
				project.setUpdatedTime(new Date().getTime());
				//修改 项目时间  2017/2/21
				project.setProjectTime(new Date().getTime());
				project.setCreatedTime(DateUtil.convertStringToDate(project.getCreateDate().trim(), "yyyy-MM-dd").getTime());
				long id = projectService.newProject(project);
				if (id > 0) {
					responseBody.setResult(new Result(Status.OK, "success", "项目添加成功!"));
					responseBody.setId(id);										
					ControllerUtils.setRequestParamsForMessageTip(request,project.getProjectName(), project.getId(),StageChangeHandler._6_1_);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseBody.setResult(new Result(Status.ERROR, "ERROR", "填写错误,请检查填写内容"));
		}

		return responseBody;
	}
	
	/**
	 * 修改项目信息接口
	 * 
	 * @author yangshuhua
	 * @return
	 * @throws ParseException
	 */
	@com.galaxyinternet.common.annotation.Logger(operationScope = LogType.MESSAGE)
	@ResponseBody
	@RequestMapping(value = "/up", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<Project> resetProject(@RequestBody Project project,
			HttpServletRequest request) throws ParseException {
		ResponseData<Project> responseBody = new ResponseData<Project>();
		if (project == null || project.getId() == null) {
			responseBody.setResult(new Result(Status.ERROR, null, "必要的参数丢失!"));
			return responseBody;
		}

		// 执行转换
		project.getProjectContribution();
		project.getProjectValuations();
		project.getCurrencyUnit();
		project.getProjectShareRatio();

		User user = (User) getUserFromSession(request);
		if (project.getProjectValuations() == null) {
			if (project.getProjectShareRatio() != null
					&& project.getProjectShareRatio() > 0
					&& project.getProjectContribution() != null
					&& project.getProjectContribution() > 0) {
				project.setProjectValuations(project.getProjectContribution()
						* 100 / project.getProjectShareRatio());
			}
		}
		if(null!=project.getIndustryOwn()&&project.getIndustryOwn().longValue()==0){
			project.setIndustryOwn(null);
		}

		Project p = projectService.queryById(project.getId());
		if (p == null) {
			responseBody
					.setResult(new Result(Status.ERROR, null, "未找到相应的项目信息!"));
			return responseBody;
		}
		// 项目创建者用户ID与当前登录人ID是否一样
		if (user.getId().longValue() != p.getCreateUid().longValue()) {
			responseBody
					.setResult(new Result(Status.ERROR, null, "没有权限修改该项目!"));
			return responseBody;
		}
		project.setUpdatedTime(System.currentTimeMillis());
		project.setCreatedTime(DateUtil.convertStringToDate(
				p.getCreateDate().trim(), "yyyy-MM-dd").getTime());

		int num = projectService.updateById(project);
		if (num > 0) {
			responseBody.setResult(new Result(Status.OK, null, "项目修改成功!"));
			ControllerUtils.setRequestParamsForMessageTip(request,
					project.getProjectName(), project.getId(),"2");
		}
		return responseBody;
	}

	/**
	 * 为了消息提醒修改了 编辑项目的接口 2016/7/18修改
	 * 修改项目信息接口
	 * 
	 * @author yangshuhua
	 * @return
	 * @throws ParseException
	 */
	@com.galaxyinternet.common.annotation.Logger(operationScope = LogType.MESSAGE)
	@ResponseBody
	@RequestMapping(value = "/editProject", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<Project> editProject(@RequestBody Project project,
			HttpServletRequest request) throws ParseException {
		ResponseData<Project> responseBody = new ResponseData<Project>();
		if (project == null || project.getId() == null) {
			responseBody.setResult(new Result(Status.ERROR, null, "必要的参数丢失!"));
			return responseBody;
		}

		// 执行转换
		project.getProjectContribution();
		project.getProjectValuations();
		project.getCurrencyUnit();
		project.getProjectShareRatio();

		User user = (User) getUserFromSession(request);
		
		if (project.getProjectValuations() == null) {
			if (project.getProjectShareRatio() != null
					&& project.getProjectShareRatio() > 0
					&& project.getProjectContribution() != null
					&& project.getProjectContribution() > 0) {
				project.setProjectValuations(project.getProjectContribution()
						* 100 / project.getProjectShareRatio());
			}
		}
		if (project.getServiceCharge() == null) {
			project.setServiceCharge(0.0000);
		}
		if(null!=project.getIndustryOwn()&&project.getIndustryOwn().longValue()==0){
			project.setIndustryOwn(null);
		}

		Project p = projectService.queryById(project.getId());
		if (p == null) {
			responseBody
					.setResult(new Result(Status.ERROR, null, "未找到相应的项目信息!"));
			return responseBody;
		}
		// 项目创建者用户ID与当前登录人ID是否一样
		if (user.getId().longValue() != p.getCreateUid().longValue()) {
			responseBody
					.setResult(new Result(Status.ERROR, null, "没有权限修改该项目!"));
			return responseBody;
		}
		project.setUpdatedTime(System.currentTimeMillis());
		project.setCreatedTime(DateUtil.convertStringToDate(
				p.getCreateDate().trim(), "yyyy-MM-dd").getTime());

		int num = projectService.updateById(project);
		if (num > 0) {
			responseBody.setResult(new Result(Status.OK, null, "项目修改成功!"));
			ControllerUtils.setRequestParamsForMessageTip(request,
					project.getProjectName(), project.getId(),"2");
		}
		return responseBody;
	}
	/**
	 * 查询指定的项目信息接口
	 * @author gxc
	 * @return 2016/6/13修改
	 */
	@com.galaxyinternet.common.annotation.Logger
	@ResponseBody
	@RequestMapping(value = "/sp/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<Project> selectProject(@PathVariable("pid") String pid,
			HttpServletRequest request) {
		ResponseData<Project> responseBody = new ResponseData<Project>();
		
		SopFile query = new SopFile();
		

		Project project = projectService.queryById(Long.parseLong(pid));
		//新增的融资历史 2016/11/21
		FinanceHistory financeHistory= new FinanceHistory();
		financeHistory.setProjectId(Long.parseLong(pid));
		List<FinanceHistory> queryList = financeHistoryService.queryList(financeHistory);
		if(queryList!=null && queryList.size()>0){
			project.setFinanceHistory(queryList.get(0));
		}
		
		if (project != null) {
			
			List<ProjectTransfer> ss = projectTransferService.applyTransferData(project.getId());
			
			if(null == ss || ss.size() ==0 ){
				project.setProjectYjz("1");  //1 标识 项目不处于移交中
			}else{
				project.setProjectYjz("0");  //0 标识 项目处于移交中 
			}
			
			Department Department = new Department();//
			Department.setId(project.getProjectDepartid());
			Department queryOne = departmentService.queryOne(Department);
			Long deptId = null;
			if (queryOne != null) {
				project.setProjectCareerline(queryOne.getName());
				deptId = queryOne.getManagerId();
				if (null != deptId && deptId.longValue() > 0L) {
					User queryById = userService.queryById(queryOne
							.getManagerId());
					if (queryById != null) {
						project.setHhrName(queryById.getRealName());
					}
				}
			}
			
/*			if(project.getIndustryOwn()!=null){
				Department Dt= new Department();
				Dt.setId(project.getIndustryOwn());
				Department queryTwo = departmentService.queryOne(Dt);
				if (queryTwo != null) {
					project.setIndustryOwnDs(queryTwo.getName());				
				}
			}*/
			//新修改的项目行业归属 2016/12/5
			if(project.getIndustryOwn()!=null){
                String name=DictEnum.industryOwn.getNameByCode(
        		 project.getIndustryOwn().toString());
			if (name != null) {
					project.setIndustryOwnDs(name);				
				}else{
					project.setIndustryOwnDs(null);
				}
			}		
			//商业计划书 的 暂无 
			if(StringUtils.isBlank(pid)){
				responseBody.setResult(new Result(Status.ERROR,"传入的projectId为空"));
				return responseBody;
			}
			
				query.setProjectId(Long.parseLong(pid));
				query.setFileWorktype(DictEnum.fileWorktype.商业计划.getCode());
				PageRequest pageRequest = new PageRequest(0, 3, Direction.DESC,
						"created_time");
				Page<SopFile> sopFilePage = sopFileService.queryFileList(query, pageRequest);
				if(sopFilePage.getContent().size()<=0){
					project.setBusinessPlanFilezw(0);
				}else{
					project.setBusinessPlanFilezw(1);
				}
				//团队成员的暂无
				PersonPoolBo personPoolBo = new PersonPoolBo();
				personPoolBo.setProjectId(Long.parseLong(pid));
				personPoolBo.setPageNum(0);
				personPoolBo.setPageSize(10);
				Page<PersonPool> pagepersonList = personPoolService.queryPageListByPid(
						personPoolBo, new PageRequest(personPoolBo.getPageNum(),
								personPoolBo.getPageSize()));
				if(pagepersonList==null){
					project.setTeamPersonzw(0);
				}else if(pagepersonList!=null&&pagepersonList.getContent().size()==0){
					project.setTeamPersonzw(0);
				}else{
					project.setTeamPersonzw(1);
				}
				//股权结构的暂无
				ProjectShares projectShares = new ProjectShares();
				projectShares.setPageNum(0);
				projectShares.setPageSize(10);
				projectShares.setProjectId(Long.parseLong(pid));
				Page<ProjectShares> pageshareList = projectSharesService.queryPageList(projectShares, new PageRequest(projectShares.getPageNum(), projectShares.getPageSize()));
				if(pageshareList==null){
					project.setShareszw(0);
				}else if(pageshareList!=null&&pageshareList.getContent().size()==0){
					project.setShareszw(0);
				}else{
					project.setShareszw(1);
				}
			/**
			 * 2016/8/18 增加暂无标识 
			 */
				//交割前事项的暂无
				
				DeliveryBo de = new DeliveryBo();
				de.setProjectId(Long.parseLong(pid));
				Integer pageNum = de.getPageNum() != null ? de.getPageNum() : 0;
				Integer pageSize = de.getPageSize() != null ? de.getPageSize() : 10;
				
				Page<DeliveryBo> deliverypage =  deliveryService.queryDeliveryPageList(de, new PageRequest(pageNum,pageSize));
				if(deliverypage==null){
					project.setJgqsxzw(0);
				}else if(deliverypage!=null&&deliverypage.getContent().size()==0){
					project.setJgqsxzw(0);
				}else{
					project.setJgqsxzw(1);
				}
				//项目 文档的暂无 
				
				SopFile sopFile = new SopFile();
				List<String> fileStatusList = new ArrayList<String>();
				fileStatusList.add(DictEnum.fileStatus.已上传.getCode());
				fileStatusList.add(DictEnum.fileStatus.已签署.getCode());
				sopFile.setFileStatusList(fileStatusList);
			  //	sopFile.setFileValid(1);
				sopFile.setFileWorktypeNullFilter("true");
				sopFile.setProjectId(Long.parseLong(pid));
				Integer pageNumm = sopFile.getPageNum() != null ? sopFile.getPageNum() : 0;
				Integer pageSizee = sopFile.getPageSize() != null ? sopFile.getPageSize() : 10;
		
				Page<SopFile> pageSopFile = sopFileService.queryappFileList(sopFile, new PageRequest(pageNumm,pageSizee));
				
				if(pageSopFile==null){
					project.setXmwdzw(0);
				}else if(pageSopFile!=null && pageSopFile.getContent().size()==0){
					project.setXmwdzw(0);
				}else{
					project.setXmwdzw(1);
				}
			//TODO
				
				//运营分析暂无
				MeetingRecordBo mes = new MeetingRecordBo();
				mes.setProjectId(Long.parseLong(pid));
				Integer pageNummm = mes.getPageNum() != null ? mes.getPageNum() : 0;
				Integer pageSizeee = mes.getPageSize() != null ? mes.getPageSize() : 10;
				//运营分析 类型投后运营会议
				mes.setRecordType(RecordType.OPERATION_MEETING.getType());
				List<String> meetingTypeList = new ArrayList<String>();
				List<Dict> dictList = dictService.selectByParentCode("postMeetingType");
				for(Dict dict : dictList){
					meetingTypeList.add(dict.getCode());
				}
				mes.setMeetingTypeList(meetingTypeList);
				Page<MeetingRecord> mrpageList = meetingService.queryPageList(mes,new PageRequest(pageNummm,pageSizeee));
				
				ProjectHealthBo healthQuery = new ProjectHealthBo();
				healthQuery.setProjectId(Long.parseLong(pid));
				PageRequest healthPageable = new PageRequest(0,1, new Sort(Direction.DESC,"created_time"));
				List<ProjectHealth> healthList = projectHealthService.queryList(healthQuery, healthPageable);
 
				if(mrpageList==null && healthList==null){
					project.setYyfxzw(0);
				}else if(mrpageList!=null && mrpageList.getContent().size()==0 && healthList!=null && healthList.size()==0){
					project.setYyfxzw(0);
				}else{
					project.setYyfxzw(1);
				}
				
				
				
				
				
			//1.添加项目描述的暂无标识
			if(project.getProjectDescribe()!=null&& !project.getProjectDescribe().equals("")){
				project.setProjectDescribezw(1);
			}else{
				project.setProjectDescribezw(0);
			}
			//2.添加公司定位的暂无标识
			if(project.getCompanyLocation()!=null&& !project.getCompanyLocation().equals("")){
				project.setCompanyLocationzw(1);
			}else{
				project.setCompanyLocationzw(0);
			}
			//3.用户画像的暂无标识
			if(project.getUserPortrait()!=null && !project.getUserPortrait().equals("")){
				project.setUserPortraitzw(1);
			}else{
				
				project.setUserPortraitzw(0);
			}
			//4.产品服务的暂无标识
			if(project.getProjectBusinessModel()!=null && !project.getProjectBusinessModel().equals("")){
				project.setProjectBusinessModelzw(1);
			}else{
				project.setProjectBusinessModelzw(0);
			}
			//5.竟情分析的暂无标识
			if(project.getProspectAnalysis()!=null && !project.getProspectAnalysis().equals("")){
				project.setProspectAnalysiszw(1);
			}else{
				project.setProspectAnalysiszw(0);
			}
			//6.运营数据的暂无标识
			if(project.getOperationalData()!=null && !project.getOperationalData().equals("")){
				project.setOperationalDatazw(1);
			}else{
				project.setOperationalDatazw(0);
			}
			//7.行业分析的暂无标识
			if(project.getIndustryAnalysis()!=null && !project.getIndustryAnalysis().equals("")){
				project.setIndustryAnalysiszw(1);
			}else{
				project.setIndustryAnalysiszw(0);
			}
			//8.下一轮融资路径的暂无标识
			if(project.getNextFinancingSource()!=null && !project.getNextFinancingSource().equals("")){
				project.setNextFinancingSourcezw(1);
			}else{
				project.setNextFinancingSourcezw(0);
			}	
			//9.项目的要点的暂无标识
			if(project.getProjectDescribeFinancing()!=null &&!project.getProjectDescribeFinancing().equals("")){
				project.setProjectDescribeFinancingZW(1);
			}else{
				project.setProjectDescribeFinancingZW(0);
			}
			
		} else {
			responseBody
					.setResult(new Result(Status.ERROR, null, "未查找到指定项目信息!"));
			return responseBody;
		}
		responseBody.setEntity(project);
		ControllerUtils.setRequestParamsForMessageTip(request,
				project.getProjectName(), project.getId());
		return responseBody;
	}

	/**
	 * 获取所有事业线 判断选中登录人事业线
	 */
	@ResponseBody
	@RequestMapping(value = "/queryCheckLine", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<Department> queryCheckLine(HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute(
				Constants.SESSION_USER_KEY);
		ResponseData<Department> responseBody = new ResponseData<Department>();
		try {
			List<Long> roleIdList = userRoleService.selectRoleIdByUserId(user
					.getId());
			List<Department> syxList = null;
			if (roleIdList.contains(UserConstant.DSZ)
					|| roleIdList.contains(UserConstant.CEO)) {
				Department syxType = new Department();
				syxType.setType(1);
				syxList = departmentService.queryList(syxType);// 获取所有事业线

				responseBody.setResult(new Result(Status.OK, null, ""));
				responseBody.setEntityList(syxList);
			} else {
				responseBody.setResult(new Result(Status.OK, null, "notg"));
			}
		} catch (Exception e) {
			responseBody.setResult(new Result(Status.ERROR, null, "查询事业线失败"));
			if (logger.isErrorEnabled()) {
				logger.error("queryCheckLine 查询事业线失败 ", e);
			}
		}
		return responseBody;
	}

	/**
	 * 获取项目列表(高管)
	 * 
	 * @param
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/queryAllProjects", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<Project> queryAllProjects(HttpServletRequest request,
			@RequestBody ProjectBo project) {
		ResponseData<Project> responseBody = new ResponseData<Project>();
		User user = (User) getUserFromSession(request);
		
		Direction direction = Direction.DESC;
		String property = "updated_time";
		if(!StringUtils.isEmpty(project.getProperty())){
			if("desc".equals(project.getDirection())){
				direction = Direction.DESC;
			}else{
				direction = Direction.ASC;
			}
			property = "created_time";
		}
		
		List<Long> roleIdList = userRoleService.selectRoleIdByUserId(user
				.getId());
		if (project.getProjectProgress() != null
				&& project.getProjectProgress().equals("guanbi")) {
			project.setProjectStatus(DictEnum.projectStatus.YFJ.getCode());
			project.setProjectProgress(null);
		}
		try {
			if (roleIdList.contains(UserConstant.DSZ)
					|| roleIdList.contains(UserConstant.CEO)) {
				/*
				 * Page<Project> pageProject =
				 * projectService.queryPageList(project,new
				 * PageRequest(project.getPageNum(), project.getPageSize()));
				 * responseBody.setPageList(pageProject);
				 * responseBody.setResult(new Result(Status.OK, ""));
				 */
			}
			if (roleIdList.contains(UserConstant.HHR)) {
				project.setProjectDepartid(user.getDepartmentId());
			}

			Page<Project> pageProject = projectService
					.queryPageList(
							project,
							new PageRequest(project.getPageNum(), project
									.getPageSize(),direction,property));
			FormatData format = new FormatData();
			if (!pageProject.getContent().isEmpty()) {
				format = setFormatData(pageProject.getContent());
				if (null != format.getIds() && format.getIds().size() > 0) {
					List<Department> queryListdepById = departmentService
							.queryListById(format.getIds());
					List<String> ids = new ArrayList<String>();
					Map<String, Object> depmap = new HashMap<String, Object>();
					if (!queryListdepById.isEmpty()) {
						depmap = setFormatdepeentDate(queryListdepById)
								.getMap();
						Long deptId = null;
						for (Department depentment : queryListdepById) {
							deptId = depentment.getManagerId();
							if (null != deptId && deptId.longValue() > 0L) {
								ids.add(String.valueOf(deptId));
							}
						}
					}
					FormatData usermapForat = new FormatData();
					Map<String, Object> usermap = new HashMap<String, Object>();
					if (!ids.isEmpty()) {
						List<User> queryListByDepId = userService
								.queryListById(ids);
						if (!queryListByDepId.isEmpty()) {
							usermapForat = setFormatUserDate(queryListByDepId);
							usermap = usermapForat.getMap();
						}
					}
					if (usermap != null || depmap != null) {
						for (Project proje : pageProject.getContent()) {
							String depid = proje.getProjectDepartid()
									.toString();
							if (usermap != null) {
								User u = (User) usermap.get(depid);
								proje.setHhrName(u == null ? "" : u
										.getRealName());
							}
							if (depmap != null) {
								Department dep = (Department) depmap.get(depid);
								proje.setProjectCareerline(dep == null ? ""
										: dep.getName());
							}
						}
					}
					if (null != usermap) {
						usermap.clear();
						usermap = null;
					}
					if (null != depmap) {
						depmap.clear();
						depmap = null;
					}
				}
			}
			responseBody.setPageList(pageProject);
			responseBody.setResult(new Result(Status.OK, ""));
			return responseBody;
		} catch (PlatformException e) {
			responseBody.setResult(new Result(Status.ERROR, null,
					"queryUserList faild"));
			if (logger.isErrorEnabled()) {
				logger.error("queryUserList ", e);
			}
		}
		return responseBody;
	}

	/*
	 * @ResponseBody
	 * 
	 * @RequestMapping(value = "/spl", produces =
	 * MediaType.APPLICATION_JSON_VALUE) public ResponseData<Project>
	 * searchProjectList(HttpServletRequest request, @RequestBody ProjectBo
	 * project) { ResponseData<Project> responseBody = new
	 * ResponseData<Project>(); User user = (User) getUserFromSession(request);
	 * project.setCreateUid(user.getId()); project.setrComplany("11");
	 * project.setbComplany(1000d); project.setaComplany(100d);
	 * project.setCascOrDes("created_time"); project.setAscOrDes("asc"); try {
	 * Page<Project> pageProject=null;
	 * if(project.getAscOrDes()!=null&&project.getCascOrDes()!=null){
	 * if(project.getAscOrDes().equals("desc")){ Sort sort = new
	 * Sort(Direction.DESC,project.getCascOrDes()); pageProject =
	 * projectService.queryPageList(project,new
	 * PageRequest(project.getPageNum(), project.getPageSize(),sort));
	 * 
	 * }else if(project.getAscOrDes().equals("asc")){ Sort sort = new
	 * Sort(Direction.ASC,project.getCascOrDes()); pageProject=
	 * projectService.queryPageList(project,new
	 * PageRequest(project.getPageNum(), project.getPageSize(),sort)); } }else{
	 * pageProject= projectService.queryPageList(project,new
	 * PageRequest(project.getPageNum(), project.getPageSize()));
	 * if(project.getProjectProgress
	 * ()!=null&&project.getProjectProgress().equals("guanbi")){
	 * project.setProjectStatus("meetingResult:3");
	 * project.setProjectProgress(null); } }
	 * responseBody.setPageList(pageProject); responseBody.setResult(new
	 * Result(Status.OK, "")); return responseBody; } catch (PlatformException
	 * e) { responseBody.setResult(new Result(Status.ERROR,
	 * "queryUserList faild")); if (logger.isErrorEnabled()) {
	 * logger.error("queryUserList ", e); } } return responseBody; }
	 */
	/**
	 * 获取项目列表(投资经理)
	 * 
	 * @param
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/spl", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<Project> searchProjectList(HttpServletRequest request,
			@RequestBody ProjectBo project) {
		ResponseData<Project> responseBody = new ResponseData<Project>();
		User user = (User) getUserFromSession(request);
		Direction direction = Direction.DESC;
		String property = "updated_time";
		if(!StringUtils.isEmpty(project.getProperty())){
			if("desc".equals(project.getDirection())){
				direction = Direction.DESC;
			}else{
				direction = Direction.ASC;
			}
			property = "created_time";
		}
		
		try {
			if (project.getProjectProgress() != null
					&& project.getProjectProgress().equals("guanbi")) {
				project.setProjectStatus(DictEnum.projectStatus.YFJ.getCode());
				project.setProjectProgress(null);
			}
			if (project.getProType() != null
					&& "2".equals(project.getProType())) {
				project.setProjectDepartid(user.getDepartmentId());
			} else {
				project.setCreateUid(user.getId());
			}

			Page<Project> pageProject = projectService
					.queryPageList(
							project,
							new PageRequest(project.getPageNum(), project
									.getPageSize(),direction,
									property));

			responseBody.setPageList(pageProject);
			responseBody.setResult(new Result(Status.OK, ""));
			return responseBody;
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
	 * 添加团队成员
	 * 2016/12/24 修改
	 * @author yangshuhua
	 */
	@com.galaxyinternet.common.annotation.Logger
	@ResponseBody
	@RequestMapping(value = "/insertProjectPerson", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<PersonPoolBo> insertProjectPerson(
			@RequestBody PersonPoolBo pool, HttpServletRequest request) {
		ResponseData<PersonPoolBo> responseBody = new ResponseData<PersonPoolBo>();
		if (pool.getProjectId() == null || pool.getProjectId() <= 0
				|| pool.getPersonName() == null) {
			responseBody.setResult(new Result(Status.ERROR, null, "必要的参数丢失!"));
			return responseBody;
		}
		User user = (User) getUserFromSession(request);
		Project p = projectService.queryById(pool.getProjectId());
		// 项目创建者用户ID与当前登录人ID是否一样
		if (p != null
				&& user.getId().doubleValue() != p.getCreateUid().doubleValue()) {
			responseBody.setResult(new Result(Status.ERROR, null,
					"没有权限为该项目添加团队成员!"));
			return responseBody;
		}
		try {
			if(pool.getPersonBirthdayStr() != null){
				Date date = DateUtil.convertStringToDate(pool.getPersonBirthdayStr()+"-01-01 00:00:00");
				pool.setPersonBirthday(date);
			}	
			if( pool.getPersonTelephone() == null){
				pool.setPersonTelephone("");
			}
			pool.setCreatedTime(System.currentTimeMillis());
			Long id = personPoolService.addProjectPerson(pool);
			if (id > 0) {
				responseBody
						.setResult(new Result(Status.OK, null, "团队成员添加成功!"));
				responseBody.setEntity(pool);
				ControllerUtils.setRequestParamsForMessageTip(request,
						p.getProjectName(), p.getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseBody;
	}

	/**
	 * 修改团队成员
	 * 
	 * @author yangshuhua
	 */
	@com.galaxyinternet.common.annotation.Logger
	@ResponseBody
	@RequestMapping(value = "/resetProjectPerson", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<PersonPoolBo> resetProjectPerson(
			@RequestBody PersonPoolBo pool, HttpServletRequest request) {
		ResponseData<PersonPoolBo> responseBody = new ResponseData<PersonPoolBo>();
		if (pool == null || pool.getId() == null || pool.getProjectId() == null) {
			responseBody.setResult(new Result(Status.ERROR, null, "必要的参数丢失!"));
			return responseBody;
		}
		User user = (User) getUserFromSession(request);
		Project p = projectService.queryById(pool.getProjectId());
		// 项目创建者用户ID与当前登录人ID是否一样
		if (p != null
				&& user.getId().doubleValue() != p.getCreateUid().doubleValue()) {
			responseBody.setResult(new Result(Status.ERROR, null,
					"没有权限修改该项目的团队成员信息!"));
			return responseBody;
		}
		
		if(pool.getPersonBirthdayStr() != null){
			try {
				Date date = DateUtil.convertStringToDate(pool.getPersonBirthdayStr()+"-01-01 00:00:00");
				pool.setPersonBirthday(date);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if( pool.getPersonTelephone() == null){
			pool.setPersonTelephone("");
		}
		int num = personPoolService.updateById(pool);
		if (num > 0) {
			responseBody.setResult(new Result(Status.OK, null, "团队成员信息修改成功!"));
			ControllerUtils.setRequestParamsForMessageTip(request,
					p.getProjectName(), p.getId());
		}
		return responseBody;
	}

	/**
	 * 删除团队成员
	 * 
	 * @author yangshuhua
	 */
	@com.galaxyinternet.common.annotation.Logger
	@ResponseBody
	@RequestMapping(value = "/dpp/{id}/{projectId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<PersonPoolBo> deleteProjectPerson(
			@PathVariable("id") Long id,
			@PathVariable("projectId") Long projectId,
			HttpServletRequest request) {
		ResponseData<PersonPoolBo> responseBody = new ResponseData<PersonPoolBo>();
		if (projectId == null) {
			responseBody.setResult(new Result(Status.ERROR, null, "必要的参数丢失!"));
			return responseBody;
		}
		User user = (User) getUserFromSession(request);
		Project p = projectService.queryById(projectId);
		// 项目创建者用户ID与当前登录人ID是否一样
		if (p != null
				&& user.getId().doubleValue() != p.getCreateUid().doubleValue()) {
			responseBody.setResult(new Result(Status.ERROR, null,
					"没有权限删除该项目的团队成员!"));
			return responseBody;
		}
		ProjectPerson pp = new ProjectPerson();
		pp.setPersonId(id);
		pp.setProjectId(projectId);
		int num = projectPersonService.delete(pp);

		int mump = personPoolService.deleteById(id);

		if (num > 0 && mump > 0) {
			responseBody.setResult(new Result(Status.OK, null, "团队成员删除成功!"));
			ControllerUtils.setRequestParamsForMessageTip(request,
					p.getProjectName(), p.getId());
		}
		return responseBody;
	}

	/**
	 * 查询团队成员列表
	 * 
	 * @author yangshuhua
	 */
	@ResponseBody
	@RequestMapping(value = "/queryProjectPerson", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<PersonPool> queryProjectPerson(
			HttpServletRequest request, @RequestBody PersonPoolBo personPoolBo) {
		ResponseData<PersonPool> responseBody = new ResponseData<PersonPool>();
		try {
			Page<PersonPool> pageList = personPoolService.queryPageListByPid(
					personPoolBo, new PageRequest(personPoolBo.getPageNum(),
							personPoolBo.getPageSize()));
			responseBody.setPageList(pageList);
			responseBody.setResult(new Result(Status.OK, ""));
			return responseBody;
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
	 * 查询完善简历任务所属的人员列表
	 * 
	 * @author yangshuhua
	 */
	@ResponseBody
	@RequestMapping(value = "/queryPersonListToTask", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<PersonPool> queryPersonListToTask(
			HttpServletRequest request, @RequestBody PersonPoolBo personPoolBo) {
		ResponseData<PersonPool> responseBody = new ResponseData<PersonPool>();
		if (personPoolBo.getTid() == null
				|| personPoolBo.getProjectId() == null) {
			responseBody.setResult(new Result(Status.ERROR, null, "缺失必要的参数!"));
			return responseBody;
		}
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("pid", personPoolBo.getProjectId());
			params.put("tid", personPoolBo.getTid());
			List<PersonPool> list = personPoolService.selectNoToTask(params);
			if (list != null && !list.isEmpty()) {
				responseBody.setEntityList(list);
				responseBody.setResult(new Result(Status.OK, null, "查询成功!"));
			}
		} catch (PlatformException e) {
			responseBody.setResult(new Result(Status.ERROR, null, "异常，请重试!"));
			if (logger.isErrorEnabled()) {
				logger.error("queryUserList ", e);
			}
		}
		return responseBody;
	}

	/**
	 * 创建项目编码
	 * 
	 * @author yangshuhua
	 */
	@ResponseBody
	@RequestMapping(value = "/cpc", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<Config> createProjectCode(HttpServletRequest request) {
		ResponseData<Config> responseBody = new ResponseData<Config>();

		User user = (User) getUserFromSession(request);
		List<Long> roleIdList = userRoleService.selectRoleIdByUserId(user
				.getId());
		if (!roleIdList.contains(UserConstant.HHR)
				&& !roleIdList.contains(UserConstant.TZJL)) {
			responseBody.setResult(new Result(Status.ERROR, null, "没有权限!"));
			return responseBody;
		}

		try {
			Config config = configService.createCode();
			NumberFormat nf = NumberFormat.getInstance();
			nf.setGroupingUsed(false);
			nf.setMaximumIntegerDigits(6);
			nf.setMinimumIntegerDigits(6);
			Long did = user.getDepartmentId();
			if (did != null) {
				int code = EnumUtil.getCodeByCareerline(did.longValue());
				String projectCode = String.valueOf(code)
						+ nf.format(Integer.parseInt(config.getValue()));
				request.getSession().setAttribute(
						Constants.SESSION_PROJECT_CODE, projectCode);
				config.setPcode(projectCode);
				responseBody.setEntity(config);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseBody;
	}

	/***
	 * 获取项目信息
	 * 
	 * @param pid
	 * @param request
	 * @return
	 */
	@com.galaxyinternet.common.annotation.Logger
	@ResponseBody
	@RequestMapping(value = "/getProjectInfo/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<ProjectBo> projectInfo(@PathVariable("pid") String pid,
			HttpServletRequest request) {

		ProjectBo projectBo = new ProjectBo();
		// 查询项目信息
		ResponseData<ProjectBo> responseBody = new ResponseData<ProjectBo>();
		Project project = projectService.queryById(Long.parseLong(pid));
		if (project == null) {
			responseBody
					.setResult(new Result(Status.ERROR, null, "未查找到指定项目信息!"));
			return responseBody;
		}
		// 项目合伙人
		/**
		 * ProjectPerson person=new ProjectPerson();
		 * person.setProjectId(project.getId()); ProjectPerson
		 * pp=projectPersonService.queryOne(person);
		 * 
		 * if(pp == null ){ responseBody.setResult(new Result(Status.ERROR,
		 * "未查找到指定项目关联合伙人信息!")); return responseBody; } //人资信息 PersonPool
		 * pool=personPoolService.queryById(pp.getPersonId()); if(pool == null){
		 * responseBody.setResult(new Result(Status.ERROR, "未查找到指定项目合伙人信息!"));
		 * return responseBody; }
		 ***/

		projectBo.setProjectName(project.getProjectName());
		projectBo.setProjectCode(project.getProjectCode());
		projectBo.setProjectDescribe(project.getProjectDescribe());
		projectBo.setProjectType(project.getProjectType());
		// projectBo.setPartnerName(pool.getPersonName());
		projectBo.setCreateUname(project.getCreateUname());
		projectBo.setProjectCareerline(project.getProjectCareerline());
		responseBody.setEntity(projectBo);
		ControllerUtils.setRequestParamsForMessageTip(request,
				project.getProjectName(), project.getId());

		return responseBody;
	}

	/**
	 * 跳转到修改项目页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/updatePro/{id}", method = RequestMethod.GET)
	public String updateProject(@PathVariable("id") Long id,
			HttpServletRequest request) {

		PersonPool person = personPoolService.queryById(id);
		if (person == null) {
			return "未查找到指定信息!";
		}
		request.setAttribute("person", person);
		return "project/updatePerson";
	}

	/**
	 * 项目阶段中的文档上传 该项目对应的创建人操作
	 * 
	 * @author yangshuhua voucherType
	 */
	@com.galaxyinternet.common.annotation.Logger(operationScope = { LogType.LOG, LogType.MESSAGE })
	@ResponseBody
	@RequestMapping(value = "/stageChange", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<ProjectQuery> stageChange(ProjectQuery p,
			HttpServletRequest request) {
		ResponseData<ProjectQuery> responseBody = new ResponseData<ProjectQuery>();
		// 解析文件上传的非file表单值
		if (p.getPid() == null) {
			String json = JSONUtils.getBodyString(request);
			p = GSONUtil.fromJson(json, ProjectQuery.class);
		}
		/**
		 * 1.参数校验
		 */
		// 所有都必须附带pid和stage
		if (p.getPid() == null
				|| p.getStage() == null
				|| !SopConstant._progress_pattern_.matcher(p.getStage())
						.matches() || p.getParseDate() == null) {
			responseBody.setResult(new Result(Status.ERROR, null, "必要的参数丢失!"));
			return responseBody;
		}
		// 如果是内评会、CEO评审会、立项会、投决会,则会议类型和会议结论不能缺少
		if (p.getStage().equals(DictEnum.projectProgress.内部评审.getCode())
				|| p.getStage()
						.equals(DictEnum.projectProgress.CEO评审.getCode())
				|| p.getStage().equals(DictEnum.projectProgress.立项会.getCode())
				|| p.getStage()
						.equals(DictEnum.projectProgress.投资决策会.getCode())) {
			if (p.getMeetingType() == null
					|| !SopConstant._meeting_type_pattern_.matcher(
							p.getMeetingType()).matches()
					|| p.getResult() == null
					|| !SopConstant._meeting_result_pattern_.matcher(
							p.getResult()).matches()) {
				responseBody.setResult(new Result(Status.ERROR, null,
						"必要的参数丢失!"));
				return responseBody;
			}
			// 已有通过的会议，不能再添加会议纪要
			MeetingRecord mrQuery = new MeetingRecord();
			mrQuery.setProjectId(p.getPid());
			mrQuery.setMeetingType(p.getMeetingType());
			mrQuery.setMeetingResult(DictEnum.meetingResult.通过.getCode());
			Long mrCount = meetingRecordService.queryCount(mrQuery);
			if (mrCount != null && mrCount.longValue() > 0L) {
				responseBody.setResult(new Result(Status.ERROR, null,
						"已有通过的会议，不能再添加会议纪要!"));
				return responseBody;
			}

			// 排期池校验
			if (p.getMeetingType().equals(DictEnum.meetingType.立项会.getCode())
					|| p.getMeetingType().equals(
							DictEnum.meetingType.投决会.getCode())) {
				MeetingScheduling ms = new MeetingScheduling();
				ms.setProjectId(p.getPid());
				ms.setMeetingType(p.getMeetingType());
				ms.setStatus(DictEnum.meetingResult.待定.getCode());
				List<MeetingScheduling> mslist = meetingSchedulingService
						.queryList(ms);
				if (mslist == null || mslist.isEmpty()) {
					responseBody.setResult(new Result(Status.ERROR, "",
							"未在排期池中，不能添加会议记录!"));
					return responseBody;
				}
			}

		}
		Project project = projectService.queryById(p.getPid());
		if (project == null) {
			responseBody
					.setResult(new Result(Status.ERROR, null, "未找到相应的项目信息!"));
			return responseBody;
		}

		// 投资意向书、尽职调查及投资协议的文档上传只能在当前阶段才能进行
		if (p.getStage().equals(DictEnum.projectProgress.投资意向书.getCode())
				|| p.getStage().equals(DictEnum.projectProgress.尽职调查.getCode())
				|| p.getStage().equals(DictEnum.projectProgress.投资协议.getCode())) {
			if (p.getType() == null
					|| p.getFileType() == null
					|| !SopConstant._file_type_pattern_
							.matcher(p.getFileType()).matches()
					|| p.getFileWorktype() == null
					|| !SopConstant._file_worktype_pattern_.matcher(
							p.getFileWorktype()).matches()) {
				responseBody.setResult(new Result(Status.ERROR, null,
						"必要的参数丢失!"));
				return responseBody;
			}
			int in = Integer.parseInt(p.getStage().substring(
					p.getStage().length() - 1));
			int pin = Integer.parseInt(project.getProjectProgress().substring(
					project.getProjectProgress().length() - 1));
			if (in < pin) {
				responseBody
						.setResult(new Result(Status.ERROR, null, "该操作已过期!"));
				return responseBody;
			}
			// 如果是内部创建/未勾选"涉及股权转让"没有股权转让文档
			if (p.getFileWorktype().equals(
					DictEnum.fileWorktype.股权转让协议.getCode())) {
				if (project != null
						&& project.getProjectType() != null
						&& project.getProjectType().equals(
								DictEnum.projectType.内部创建.getCode())) {
					responseBody.setResult(new Result(Status.ERROR, null,
							"内部创建项目不需要股权转让协议!"));
					return responseBody;
				} else if (project.getStockTransfer() == null
						|| project.getStockTransfer() == 0) {
					responseBody.setResult(new Result(Status.ERROR, null,
							"项目未选择涉及股权转让!"));
					return responseBody;
				}
			}
			/**
			 * 上传签署凭证时要对相对应的文档是否已上传进行校验
			 */
			if (p.getVoucherType() != null
					&& p.getVoucherType().intValue() == 1) {
				SopFile fileQuery = null;
				if (p.getFileWorktype().equals(
						DictEnum.fileWorktype.投资意向书.getCode())) {
					// file表
					fileQuery = new SopFile();
					fileQuery.setProjectId(p.getPid());
					fileQuery.setFileWorktype(DictEnum.fileWorktype.投资意向书
							.getCode());
					fileQuery = sopFileService.queryOne(fileQuery);
					if (fileQuery.getFileKey() == null
							|| fileQuery.getBucketName() == null) {
						responseBody.setResult(new Result(Status.ERROR, null,
								"前置文件缺失!"));
						return responseBody;
					}
				} else if (p.getFileWorktype().equals(
						DictEnum.fileWorktype.投资协议.getCode())) {
					fileQuery = new SopFile();
					fileQuery.setProjectId(p.getPid());
					fileQuery.setFileWorktype(DictEnum.fileWorktype.投资协议
							.getCode());
					fileQuery = sopFileService.queryOne(fileQuery);
					if (fileQuery.getFileKey() == null
							|| fileQuery.getBucketName() == null) {
						responseBody.setResult(new Result(Status.ERROR, null,
								"前置文件缺失!"));
						return responseBody;
					}
				} else if (p.getFileWorktype().equals(
						DictEnum.fileWorktype.股权转让协议.getCode())) {
					fileQuery = new SopFile();
					fileQuery.setProjectId(p.getPid());
					fileQuery.setFileWorktype(DictEnum.fileWorktype.股权转让协议
							.getCode());
					fileQuery = sopFileService.queryOne(fileQuery);
					if (fileQuery.getFileKey() == null
							|| fileQuery.getBucketName() == null) {
						responseBody.setResult(new Result(Status.ERROR, null,
								"前置文件缺失!"));
						return responseBody;
					}

					// 验证投资协议签署证明是否已上传
					SopFile fq = new SopFile();
					fq.setProjectId(p.getPid());
					fq.setFileWorktype(DictEnum.fileWorktype.投资协议.getCode());
					fq = sopFileService.queryOne(fq);
					Long voucherId = fq.getVoucherId();
					if (voucherId == null) {
						responseBody.setResult(new Result(Status.ERROR, null,
								"数据异常!"));
						return responseBody;
					}
					SopVoucherFile f = sopVoucherFileService
							.queryById(voucherId);
					if (f.getFileKey() == null || f.getBucketName() == null) {
						responseBody.setResult(new Result(Status.ERROR, null,
								"缺失投资协议签署证明!"));
						return responseBody;
					}
				}
			}
		}

		User user = (User) getUserFromSession(request);
		// 项目创建者用户ID与当前登录人ID是否一样
		if (user.getId().longValue() != project.getCreateUid().longValue()) {
			responseBody
					.setResult(new Result(Status.ERROR, null, "没有权限修改该项目!"));
			return responseBody;
		}
		p.setCreatedUid(user.getId());
		p.setDepartmentId(user.getDepartmentId());
		/**
		 * 2.文件上传 这里都是上传，无更新，所以每次都生成一个新的fileKey
		 */
		String fileKey = String
				.valueOf(IdGenerator.generateId(OSSHelper.class));
		UploadFileResult result = uploadFileToOSS(request, fileKey,
				tempfilePath);

		// 验证是否文件是必须的
		if (!p.getStage().equals(DictEnum.projectProgress.接触访谈.getCode())
				&& !p.getStage()
						.equals(DictEnum.projectProgress.内部评审.getCode())
				&& !p.getStage().equals(
						DictEnum.projectProgress.CEO评审.getCode())
				&& !p.getStage().equals(DictEnum.projectProgress.立项会.getCode())
				&& !p.getStage().equals(
						DictEnum.projectProgress.投资决策会.getCode())) {
			if (result == null
					|| !result.getResult().getStatus().equals(Result.Status.OK)) {
				responseBody
						.setResult(new Result(Status.ERROR, null, "缺失相应文档!"));
				return responseBody;
			}
		}

		/**
		 * 3.处理业务
		 */
		try {
			if (result != null
					&& result.getResult().getStatus().equals(Result.Status.OK)) {
				p.setFileName(result.getFileName());
				p.setSuffix(result.getFileSuffix());
				p.setBucketName(result.getBucketName());
				p.setFileKey(fileKey);
				p.setFileSize(result.getContentLength());
			}
			if (handlerManager.getStageHandlers().containsKey(p.getStage())) {
				Handler handler = handlerManager.getStageHandlers().get(
						p.getStage());
				SopResult r = handler.handler(p, project);
				if (r != null && r.getStatus().equals(Result.Status.OK)) {
					responseBody.setResult(r);
					// 记录操作日志
					ControllerUtils.setRequestParamsForMessageTip(request,
							project.getProjectName(), project.getId(),
							r.getNumber());
				}
			}
		} catch (Exception e) {
			logger.error("操作失败", e);
			responseBody.getResult().addError("操作失败!");
		}

		return responseBody;
	}

	/**
	 * 是否涉及"股权转让"点击事件
	 */
	@ResponseBody
	@RequestMapping(value = "/store/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<Project> store(HttpServletRequest request,
			@PathVariable("pid") Long pid) {
		ResponseData<Project> responseBody = new ResponseData<Project>();
		if (pid == null) {
			responseBody.setResult(new Result(Status.ERROR, null, "必要的参数丢失!"));
			return responseBody;
		}
		Project project = projectService.queryById(pid);
		if (project == null) {
			responseBody.setResult(new Result(Status.ERROR, null, "未找到指定的项目!"));
			return responseBody;
		}

		int r = (project.getStockTransfer() == null || project
				.getStockTransfer().intValue() == 0) ? 1 : 0;
		if (logger.isInfoEnabled()) {
			logger.info("old stockTransfer:" + project.getStockTransfer()
					+ ", new stockTransfer:" + r);
		}
		project.setStockTransfer(r);
		projectService.updateById(project);
		responseBody.setEntity(project);
		responseBody.setResult(new Result(Status.OK, null, ""));
		return responseBody;
	}

	//
	private boolean validatePersonMessage(Project p){
		if(p != null){
			List<PersonPool> personList = personPoolService.selectPersonPoolByPID(p.getId());
			if(personList != null && personList.size() > 0){
				for(PersonPool pool : personList){

					if(pool.getPersonName() != null && !"".equals(pool.getPersonName().trim())
							&& pool.getPersonSex() != null
							&& pool.getPersonDuties() != null && !"".equals(pool.getPersonDuties().trim())
							&& pool.getPersonBirthday() != null
							&& (pool.getIsContacts()==1 || (pool.getIsContacts()==0 &&
							null!=pool.getPersonTelephone()&&!"".equals(pool.getPersonTelephone())))
							){
						return true;
					
					}
				}
			}
		}
		return false;
	}
	private boolean validateBusinessBook(Project p){
		if(p != null){
			SopFile query = new SopFile();
			query.setProjectId(p.getId());
			query.setFileWorktype(DictEnum.fileWorktype.商业计划.getCode());
			List<SopFile> fList = sopFileService.queryList(query);
			if(fList != null && fList.size() > 0){
				return true;
			}
		}
		return false;
	}
	private boolean validateInterviewRecord(Project p){
		if(p != null){
			InterviewRecord query = new InterviewRecord();
			query.setProjectId(p.getId());
			List<InterviewRecord> irList = interviewRecordService.queryList(query);
			if(irList != null && irList.size() > 0){
				return true;
			}
		}
		return false;
	}
	private boolean validateBasicData(Project p){
		if(p != null 
				//项目的几个大文本内容必填验证
				&& p.getProjectDescribe() != null && !"".equals(p.getProjectDescribe().trim())
				//项目商业模式
				&& p.getProjectDescribeFinancing() != null && !"".equals(p.getProjectDescribeFinancing().trim())
				&& p.getProjectBusinessModel() != null && !"".equals(p.getProjectBusinessModel().trim())
				&& p.getCompanyLocation() != null && !"".equals(p.getCompanyLocation().trim())
				&& p.getUserPortrait() != null && !"".equals(p.getUserPortrait().trim())
				&& p.getProjectBusinessModel() != null && !"".equals(p.getProjectBusinessModel().trim())
				&& p.getIndustryAnalysis() != null && !"".equals(p.getIndustryAnalysis().trim())
				&& p.getProspectAnalysis() != null && !"".equals(p.getProspectAnalysis())
				//融资计划不能为空
				&& p.getProjectContribution() != null && p.getProjectContribution().doubleValue() > 0
				&& p.getProjectShareRatio() != null && p.getProjectShareRatio().doubleValue() > 0
				&& p.getProjectValuations() != null && p.getProjectValuations().doubleValue() > 0
				){
			return true;
		}
		return false;
	}
	/**
	 * 接触访谈阶段: 启动内部评审
	 * 
	 * @author yangshuhua
	 */
	@com.galaxyinternet.common.annotation.Logger(operationScope = { LogType.LOG, LogType.MESSAGE })
	@ResponseBody
	@RequestMapping(value = "/startReview/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<Project> startReview(HttpServletRequest request,
			@PathVariable("pid") Long pid) {
		ResponseData<Project> responseBody = new ResponseData<Project>();
		User user = (User) getUserFromSession(request);
		Project project = projectService.queryById(pid);
		
		//新加的验证
		if(!validateBasicData(project) 
				|| !validateInterviewRecord(project)
				|| !validateBusinessBook(project)
				|| !validatePersonMessage(project)){
			responseBody.setResult(new Result(Status.ERROR, "401", "内容提交不完整，请登录PC端进行完善!"));
			return responseBody;
		}
		
		Result result = validate(DictEnum.projectProgress.接触访谈.getCode(),
				project, user);
		if (!result.getStatus().equals(Status.OK)) {
			responseBody.setResult(result);
			return responseBody;
		}
		InterviewRecord ir = new InterviewRecord();
		ir.setProjectId(pid);
		Long count = interviewRecordService.queryCount(ir);
		if (count != null && count.doubleValue() > 0) {
			try {
				project.setProjectProgress(DictEnum.projectProgress.内部评审
						.getCode()); // 字典 项目进度 内部评审
				project.setProjectStatus(DictEnum.projectStatus.GJZ.getCode()); // 字典
																				// 项目状态
																				// =
																				// 会议结论
																				// 待定
				projectService.updateById(project);
				responseBody.setResult(new Result(Status.OK, ""));
				responseBody.setId(project.getId());
				ControllerUtils.setRequestParamsForMessageTip(request, project.getProjectName(), project.getId(),StageChangeHandler._6_2_);
			} catch (Exception e) {
				responseBody.setResult(new Result(Status.ERROR, null,
						"异常，启动内部评审失败!"));
				if (logger.isErrorEnabled()) {
					logger.error("update project faild ", e);
				}
			}
		} else {
			responseBody.setResult(new Result(Status.ERROR, null,
					"不存在访谈记录，不允许启动内部评审!"));
		}
		return responseBody;
	}

	/**
	 * CEO评审阶段申请CEO评审排期
	 * 
	 * @author yangshuhua
	 */
	@ResponseBody
	@RequestMapping(value = "/incm/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<Project> inCeoMeetingPool(HttpServletRequest request,
			@PathVariable("pid") Long pid) {
		ResponseData<Project> responseBody = new ResponseData<Project>();
		User user = (User) getUserFromSession(request);
		Project project = projectService.queryById(pid);
		Result result = validate(DictEnum.projectProgress.CEO评审.getCode(),
				project, user);
		if (!result.getStatus().equals(Status.OK)) {
			responseBody.setResult(result);
			return responseBody;
		}
		try {
			MeetingScheduling m = new MeetingScheduling();
			m.setProjectId(project.getId());
			m.setMeetingType(DictEnum.meetingType.CEO评审.getCode());
			MeetingScheduling tm = meetingSchedulingService.queryOne(m);
			if (!tm.getStatus().equals(DictEnum.meetingResult.待定.getCode())) {
				tm.setStatus(DictEnum.meetingResult.待定.getCode());
				tm.setScheduleStatus(DictEnum.meetingSheduleResult.待排期.getCode());
				tm.setUpdatedTime((new Date()).getTime());
				tm.setApplyTime(new Timestamp(new Date().getTime()));
				meetingSchedulingService.updateById(tm);
				responseBody.setResult(new Result(Status.OK, ""));
				responseBody.setId(project.getId());
			} else {
				responseBody.setResult(new Result(Status.ERROR, null,
						"项目不能重复申请CEO评审排期!"));
			}
		} catch (Exception e) {
			responseBody.setResult(new Result(Status.ERROR, null,
					"异常，申请CEO评审排期失败!"));
			if (logger.isErrorEnabled()) {
				logger.error("update project faild ", e);
			}
		}
		return responseBody;
	}

	/**
	 * CEO评审阶段申请立项会排期
	 * 
	 * @author yangshuhua
	 */
	@com.galaxyinternet.common.annotation.Logger(operationScope = { LogType.LOG, LogType.MESSAGE })
	@ResponseBody
	@RequestMapping(value = "/ges/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<Project> ges(HttpServletRequest request,
			@PathVariable("pid") Long pid) {
		ResponseData<Project> responseBody = new ResponseData<Project>();
		User user = (User) getUserFromSession(request);
		Project project = projectService.queryById(pid);
		Result result = validate(DictEnum.projectProgress.CEO评审.getCode(),
				project, user);
		if (!result.getStatus().equals(Status.OK)) {
			responseBody.setResult(result);
			return responseBody;
		}
		// 必须又一次会议记录为通过
		MeetingRecord mr = new MeetingRecord();
		mr.setProjectId(pid);
		mr.setMeetingType(DictEnum.meetingType.CEO评审.getCode());
		mr.setMeetingResult(DictEnum.meetingResult.通过.getCode());
		Long count = meetingRecordService.queryCount(mr);
		if (count != null && count.doubleValue() > 0) {
			try {
				projectService.toEstablishStage(project);
				responseBody.setResult(new Result(Status.OK, ""));
				responseBody.setId(project.getId());
				ControllerUtils.setRequestParamsForMessageTip(request, project.getProjectName(), project.getId(), StageChangeHandler._6_4_);
			} catch (Exception e) {
				responseBody.setResult(new Result(Status.ERROR, null,
						"异常，申请立项会失败!"));
				if (logger.isErrorEnabled()) {
					logger.error("update project faild ", e);
				}
			}
		} else {
			responseBody.setResult(new Result(Status.ERROR, null,
					"不存在通过的会议记录，不能申请立项会!"));
		}
		return responseBody;
	}

	/**
	 * 立项会阶段申请立项会排期
	 * 
	 * @author yangshuhua
	 */
	@com.galaxyinternet.common.annotation.Logger
	@ResponseBody
	@RequestMapping(value = "/inlx/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<Project> inLxmeetingPool(HttpServletRequest request,
			@PathVariable("pid") Long pid) {
		ResponseData<Project> responseBody = new ResponseData<Project>();
		User user = (User) getUserFromSession(request);
		Project project = projectService.queryById(pid);
		Result result = validate(DictEnum.projectProgress.立项会.getCode(),
				project, user);
		if (!result.getStatus().equals(Status.OK)) {
			responseBody.setResult(result);
			return responseBody;
		}
		try {
			MeetingScheduling m = new MeetingScheduling();
			m.setProjectId(project.getId());
			m.setMeetingType(DictEnum.meetingType.立项会.getCode());
			MeetingScheduling tm = meetingSchedulingService.queryOne(m);
			if (!tm.getStatus().equals(DictEnum.meetingResult.待定.getCode())) {
				tm.setStatus(DictEnum.meetingResult.待定.getCode());
				tm.setScheduleStatus(DictEnum.meetingSheduleResult.待排期.getCode());
				tm.setUpdatedTime((new Date()).getTime());
				tm.setApplyTime(new Timestamp(new Date().getTime()));
				meetingSchedulingService.updateById(tm);
				responseBody.setResult(new Result(Status.OK, ""));
				responseBody.setId(project.getId());
			} else {
				responseBody.setResult(new Result(Status.ERROR, null,
						"项目不能重复申请立项会排期!"));
			}
		} catch (Exception e) {
			responseBody
					.setResult(new Result(Status.ERROR, null, "异常，申请立项会失败!"));
			if (logger.isErrorEnabled()) {
				logger.error("update project faild ", e);
			}
		}
		return responseBody;
	}

	/**
	 * 尽职调查阶段--申请投决会排期
	 * 
	 * @author yangshuhua
	 */
	@com.galaxyinternet.common.annotation.Logger(operationScope = { LogType.LOG, LogType.MESSAGE })
	@ResponseBody
	@RequestMapping(value = "/smp/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<Project> sureMeetingPool(HttpServletRequest request,
			@PathVariable("pid") Long pid) {
		ResponseData<Project> responseBody = new ResponseData<Project>();
		User user = (User) getUserFromSession(request);
		Project project = projectService.queryById(pid);
		Result result = validate(DictEnum.projectProgress.尽职调查.getCode(),
				project, user);
		if (!result.getStatus().equals(Status.OK)) {
			responseBody.setResult(result);
			return responseBody;
		}
		// 验证文档是否齐全
		SopFile file = new SopFile();
		file.setProjectId(pid);
		file.setFileValid(1);
		file.setProjectProgress(DictEnum.projectProgress.尽职调查.getCode());
		List<SopFile> files = sopFileService.queryList(file);
		if (files == null
				|| (project.getProjectType().equals(
						DictEnum.projectType.外部投资.getCode()) && files.size() < 4)
				|| (project.getProjectType().equals(
						DictEnum.projectType.内部创建.getCode()) && files.size() < 2)) {
			responseBody.setResult(new Result(Status.ERROR, null,
					"文档不齐全，不能申请投决会!"));
			return responseBody;
		}
		for (SopFile f : files) {
			if (f.getFileKey() == null || "".equals(f.getFileKey().trim())) {
				responseBody.setResult(new Result(Status.ERROR, null,
						"文档不齐全，不能申请投决会!"));
				return responseBody;
			}
		}
		try {
			projectService.toSureMeetingStage(project);
			responseBody.setResult(new Result(Status.OK, ""));
			responseBody.setId(project.getId());
			ControllerUtils.setRequestParamsForMessageTip(request, project.getProjectName(), project.getId(), StageChangeHandler._6_7_);
		} catch (Exception e) {
			responseBody
					.setResult(new Result(Status.ERROR, null, "异常，申请投决会失败!"));
			if (logger.isErrorEnabled()) {
				logger.error("update project faild ", e);
			}
		}
		return responseBody;
	}

	/**
	 * 投决会阶段--申请投决会排期
	 * 
	 * @author yangshuhua
	 */
	@ResponseBody
	@RequestMapping(value = "/intj/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<Project> inSureMeetingPool(HttpServletRequest request,
			@PathVariable("pid") Long pid) {
		ResponseData<Project> responseBody = new ResponseData<Project>();
		User user = (User) getUserFromSession(request);
		Project project = projectService.queryById(pid);
		Result result = validate(DictEnum.projectProgress.投资决策会.getCode(),
				project, user);
		if (!result.getStatus().equals(Status.OK)) {
			responseBody.setResult(result);
			return responseBody;
		}
		try {
			MeetingScheduling m = new MeetingScheduling();
			m.setProjectId(project.getId());
			m.setMeetingType(DictEnum.meetingType.投决会.getCode());
			MeetingScheduling tm = meetingSchedulingService.queryOne(m);
			if (!tm.getStatus().equals(DictEnum.meetingResult.待定.getCode())) {
				tm.setStatus(DictEnum.meetingResult.待定.getCode());
				tm.setScheduleStatus(DictEnum.meetingSheduleResult.待排期.getCode());
				tm.setUpdatedTime((new Date()).getTime());
				tm.setApplyTime(new Timestamp(new Date().getTime()));
				meetingSchedulingService.updateById(tm);
				responseBody.setResult(new Result(Status.OK, ""));
				responseBody.setId(project.getId());
			} else {
				responseBody.setResult(new Result(Status.ERROR, null,
						"项目不能重复申请立项会排期!"));
			}
		} catch (Exception e) {
			responseBody
					.setResult(new Result(Status.ERROR, null, "异常，申请投决会失败!"));
			if (logger.isErrorEnabled()) {
				logger.error("update project faild ", e);
			}
		}
		return responseBody;
	}

	/**
	 * 判断项目的操作是否合法
	 * 
	 * @author yangshuhua
	 */
	public Result validate(String progress, Project project, User user) {
		if (project == null) {
			return new Result(Status.ERROR, null, "未找到相应的项目信息!");
		}
		if (project.getProjectStatus().equals(
				DictEnum.meetingResult.否决.getCode())) {
			return new Result(Status.ERROR, null, "项目已关闭!");
		}

		if (user.getId().longValue() != project.getCreateUid().longValue()) {
			return new Result(Status.ERROR, null, "没有权限修改该项目!");
		}
		int in = Integer.parseInt(progress.substring(progress.length() - 1));
		int pin = Integer.parseInt(project.getProjectProgress().substring(
				project.getProjectProgress().length() - 1));
		if (in < pin) {
			return new Result(Status.ERROR, "501", "该操作已过期!");
		}
		return new Result(Status.OK, "200", null);
	}

	/**
	 * 关闭项目
	 * 
	 * @param pid
	 *            项目id
	 * @return
	 */
	@com.galaxyinternet.common.annotation.Logger
	@ResponseBody
	@RequestMapping(value = "/breakpro/{pid}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<Project> breakproject(@PathVariable Long pid,
			HttpServletRequest request) {
		ResponseData<Project> responseBody = new ResponseData<Project>();

		User user = (User) request.getSession().getAttribute(
				Constants.SESSION_USER_KEY);
		try {
			// project id 验证
			Project project = new Project();
			project = projectService.queryById(pid);
			// 项目关闭将会议记录修改为否决项目
			MeetingScheduling me = new MeetingScheduling();
			me.setProjectId(pid);
			List<MeetingScheduling> meetingList = meetingSchedulingService
					.queryList(me);
			if (!meetingList.isEmpty()) {
				for (MeetingScheduling meet : meetingList) {

					
					/**当否决项目时,将(当前)排期未排期的会议删掉 .注:已经通过排期的会议状态不进行更改**/
					//1.否决CEO评审
					if(DictEnum.projectProgress.CEO评审.getCode().equals(project.getProjectProgress())
							&& DictEnum.meetingType.CEO评审.getCode().equals(meet.getMeetingType())){
						if(DictEnum.meetingSheduleResult.待排期.getCode() == meet.getScheduleStatus()){
							meetingSchedulingService.deleteById(meet.getId());
						}
					}
					//2.否决内部评审
					if(DictEnum.projectProgress.内部评审.getCode().equals(project.getProjectProgress())
							&& DictEnum.meetingType.内评会.getCode().equals(meet.getMeetingType())){
						if(DictEnum.meetingSheduleResult.待排期.getCode() == meet.getScheduleStatus()){
							meetingSchedulingService.deleteById(meet.getId());
						}
					}
					//3.否决立项会
					if(DictEnum.projectProgress.立项会.getCode().equals(project.getProjectProgress())
							&& DictEnum.meetingType.立项会.getCode().equals(meet.getMeetingType())){
						if(DictEnum.meetingSheduleResult.待排期.getCode() == meet.getScheduleStatus()){
							meetingSchedulingService.deleteById(meet.getId());
						}
					}
					//4.否决投决会
					if(DictEnum.projectProgress.投资决策会.getCode().equals(project.getProjectProgress())
							&& DictEnum.meetingType.投决会.getCode().equals(meet.getMeetingType())){
						if(DictEnum.meetingSheduleResult.待排期.getCode() == meet.getScheduleStatus()){
							meetingSchedulingService.deleteById(meet.getId());
						}
					}					
				
				}
			}
			meetingSchedulingService.updateBatch(meetingList);
			if (project == null || project.getCreateUid() == null) {
				responseBody
						.setResult(new Result(Status.ERROR, null, "项目检索不到"));
				return responseBody;
			} else {
				if (!project.getCreateUid().equals(user.getId())) {
					responseBody.setResult(new Result(Status.ERROR, null,
							"无操作权限"));
					return responseBody;
				}
			}

			project.setProjectStatus(DictEnum.projectStatus.YFJ.getCode());
			int id = projectService.closeProject(project);
			if (id != 1) {
				responseBody.setResult(new Result(Status.ERROR, null, "更新失败"));
				return responseBody;
			}
			responseBody.setResult(new Result(Status.OK, ""));
			ControllerUtils.setRequestParamsForMessageTip(request,
					project.getProjectName(), project.getId());
		} catch (Exception e) {
			responseBody.setResult(new Result(Status.ERROR, null,
					"add meetingRecord faild"));

			if (logger.isErrorEnabled()) {
				logger.error("add meetingRecord faild ", e);
			}
		}

		return responseBody;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ResponseBody
	@RequestMapping(value = "/getSummary", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData getSummary(HttpServletRequest request) {
		ResponseData resp = new ResponseData();
		try {
			String userId = getUserId(request);
			Map<String, Object> summary = null;
			if (StringUtils.isNotEmpty(userId)) {
				summary = projectService.getSummary(Long.valueOf(userId));
			}
			resp.setUserData(summary);
		} catch (Exception e) {
			logger.error("获取数据快览失败", e);
			resp.getResult().addError("获取数据快览失败");
		}

		return resp;
	}

	public String getHHRNname(Project p) {
		String hhrname = "";
		UserRole userrole = new UserRole();
		userrole.setRoleId(UserConstant.HHR);
		List<UserRole> queryList = userRoleService.queryList(userrole);
		if (queryList != null && queryList.size() > 0) {
			for (UserRole ur : queryList) {
				Long userid = ur.getUserId();
				User queryById = userService.queryById(userid);
				if (queryById != null) {

					if (null == queryById.getDepartmentId()) {
						return "";
					}
					if (queryById.getDepartmentId().equals(
							p.getProjectDepartid())) {
						hhrname = queryById.getRealName();
					}
				}
			}
		}
		return hhrname;
	}

	/**
	 * Ajax判断项目名称，组织机构代码是否重复
	 */
	@RequestMapping(value = "checkProject")
	@ResponseBody
	public Map<String, Integer> checkProject(@RequestBody Project query) {
		// String projectCompanyCode = "";
		if (query != null && query.getProjectCompanyCode() != null) {
			// projectCompanyCode = query.getProjectCompanyCode();
			query.setProjectCompanyCode(null);
		}
		List<Project> projectList = projectService.queryList(query);
		// Integer count = 0 ;
		// if (!StringUtils.equals(projectCompanyCode,"")) {
		// for (Project project: projectList) {

		// if (project.getProjectCompanyCode()!= null &&
		// StringUtils.equals(projectCompanyCode,
		// project.getProjectCompanyCode())) {
		// count ++;
		// }
		// }
		// }
		Map<String, Integer> map = new HashMap<String, Integer>();
		if (null == projectList || projectList.size() < 1) {
			// 不存在重复
			map.put("count", 0);
			// else if (count > 0) {
			// 重复且相同组织机构数为count
			// map.put("companyCode", count);
			// map.put("count", projectList.size());
		} else {
			map.put("count", projectList.size());
		}
		return map;
	}

	/**
	 * 验证sop流程中按钮是否可用
	 */
	@RequestMapping(value = "/checkCanUse", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseData<MeetingRecord> checkCanUse(HttpServletRequest request,
			Integer index, Long projectId, String projectType) {
		ResponseData<MeetingRecord> responseBody = new ResponseData<MeetingRecord>();
		try {
			if (index == null || projectId == null) {
				responseBody.setResult(new Result(Status.ERROR, null, "入参失败"));
				return responseBody;
			}
			if (index == 1) { // 访谈纪要判断
				InterviewRecord view = new InterviewRecord();
				view.setProjectId(projectId);
				List<InterviewRecord> viewList = interviewRecordService
						.queryList(view);
				if (viewList == null || viewList.isEmpty()) {
					responseBody.setResult(new Result(Status.OK, null, false)); // 无记录
																				// false：启动内部评审按钮不可用
				} else {
					responseBody.setResult(new Result(Status.OK, null, true));
				}

			} else if (index == 3) { // CEO评审，会议通过，可以 启用 申请立项会排期按钮
				Result result = new Result(Status.OK, null, null);
				MeetingScheduling meetSchedu = new MeetingScheduling();
				meetSchedu.setProjectId(projectId);
				meetSchedu.setMeetingType(DictEnum.meetingType.CEO评审.getCode());
				meetSchedu.setStatus(DictEnum.meetingResult.待定.getCode());
				List<MeetingScheduling> meetScheduList = meetingSchedulingService
						.queryList(meetSchedu);
				if (meetScheduList == null || meetScheduList.isEmpty()) {
					result.setErrorCode("100");
				} else {
					result.setErrorCode("101");
				}

				MeetingRecord meet = new MeetingRecord();
				meet.setProjectId(projectId);
				meet.setMeetingType(DictEnum.meetingType.CEO评审.getCode());
				meet.setMeetingResult(DictEnum.meetingResult.通过.getCode());
				List<MeetingRecord> meetList = meetingRecordService
						.queryList(meet);
				if (meetList == null || meetList.isEmpty()) {
					result.setMessage(false);
				} else {
					result.setMessage(true);
				}
				responseBody.setResult(result);
				return responseBody;
			} else if (index == 4) { // 立项会阶段，在排期池中时， 申请立项会排期按钮 不可用
				MeetingScheduling meetSchedu = new MeetingScheduling();
				meetSchedu.setProjectId(projectId);
				meetSchedu.setMeetingType(DictEnum.meetingType.立项会.getCode());
				meetSchedu.setStatus(DictEnum.meetingResult.待定.getCode());
				List<MeetingScheduling> meetScheduList = meetingSchedulingService
						.queryList(meetSchedu);
				if (meetScheduList == null || meetScheduList.isEmpty()) {
					responseBody.setResult(new Result(Status.OK, null, true)); // 池中没有记录
				} else {
					responseBody.setResult(new Result(Status.OK, null, false));
				}
			} else if (index == 6) { // 尽调阶段，文档齐全后， 申请投决会排期按钮 可用
				if (projectType == null) {
					responseBody.setResult(new Result(Status.ERROR, null,
							"入参失败"));
					return responseBody;
				}
				// 验证文档是否齐全
				SopFile file = new SopFile();
				file.setProjectId(projectId);
				file.setFileValid(1);
				file.setProjectProgress(DictEnum.projectProgress.尽职调查.getCode());
				List<SopFile> files = sopFileService.queryList(file);

				boolean allHas = true;
				if (files != null
						&& ((projectType.equals(DictEnum.projectType.外部投资
								.getCode()) && files.size() == 4) || (projectType
								.equals(DictEnum.projectType.内部创建.getCode()) && files
								.size() == 2))) {
					for (SopFile f : files) {
						if (f.getFileKey() == null
								|| "".equals(f.getFileKey().trim())) {
							allHas = false;
							break;
						}
					}
				} else {
					allHas = false;
				}
				responseBody.setResult(new Result(Status.OK, null, allHas));

			} else if (index == 7) { // 投决会阶段，在排期池中时， 申请投决会排期按钮 不可用
				MeetingScheduling meetSchedu = new MeetingScheduling();
				meetSchedu.setProjectId(projectId);
				meetSchedu.setMeetingType(DictEnum.meetingType.投决会.getCode());
				meetSchedu.setStatus(DictEnum.meetingResult.待定.getCode());
				List<MeetingScheduling> meetScheduList = meetingSchedulingService
						.queryList(meetSchedu);
				if (meetScheduList == null || meetScheduList.isEmpty()) {
					responseBody.setResult(new Result(Status.OK, null, true)); // 池中没有记录
				} else {
					responseBody.setResult(new Result(Status.OK, null, false));
				}
			}

		} catch (Exception e) {
			responseBody.setResult(new Result(Status.ERROR, null, "验证失败"));
			if (logger.isErrorEnabled()) {
				logger.error("checkCanUse 验证失败", e);
			}
		}

		return responseBody;
	}

	/**
	 * 排期池中是否存在 状态为待定
	 * 
	 * CEO评审("CEO评审","meetingType:2"), 立项会("立项会","meetingType:3"),
	 * 投决会("投决会","meetingType:4");
	 */
	@RequestMapping(value = "checkHasPool")
	@ResponseBody
	public ResponseData<MeetingScheduling> checkHasPool(
			@RequestBody MeetingScheduling query) {
		ResponseData<MeetingScheduling> responseBody = new ResponseData<MeetingScheduling>();
		List<MeetingScheduling> list = new ArrayList<MeetingScheduling>();
		try {
			if (query.getProjectId() == null || query.getMeetingType() == null) {
				responseBody.setResult(new Result(Status.ERROR, null, "参数缺失"));
				return responseBody;
			}
			query.setStatus(DictEnum.meetingResult.待定.getCode());

			list = meetingSchedulingService.queryList(query);
			if (list != null && !list.isEmpty()) {
				if (list.size() == 1) {
					responseBody.setResult(new Result(Status.OK, ""));
					responseBody.setId(list.get(0).getId());
				} else {
					responseBody.setResult(new Result(Status.ERROR, null,
							"数据返回错误"));
					logger.error("checkHasPool 数据返回错误,应返回一条数据  "
							+ GSONUtil.toJson(list));
				}
			} else {
				responseBody.setResult(new Result(Status.OK, ""));
			}
		} catch (Exception e) {
			responseBody.setResult(new Result(Status.ERROR, null, "查询失败"));
			if (logger.isErrorEnabled()) {
				logger.error("checkHasPool 查询失败", e);
			}
		}

		return responseBody;
	}

	/**
	 * 排期池中是否存在 状态为待定
	 * 
	 * CEO评审("CEO评审","meetingType:2"), 立项会("立项会","meetingType:3"),
	 * 投决会("投决会","meetingType:4");
	 */
	@RequestMapping(value = "checkPassMeet")
	@ResponseBody
	public ResponseData<MeetingRecord> checkPassMeet(
			@RequestBody MeetingRecord query) {
		ResponseData<MeetingRecord> responseBody = new ResponseData<MeetingRecord>();
		List<MeetingRecord> list = new ArrayList<MeetingRecord>();
		try {
			if (query.getProjectId() == null || query.getMeetingType() == null) {
				responseBody.setResult(new Result(Status.ERROR, null, "参数缺失"));
				return responseBody;
			}
			query.setMeetingResult(DictEnum.meetingResult.通过.getCode());

			list = meetingRecordService.queryList(query);

			if (list != null && !list.isEmpty()) {
				if (list.size() == 1) {
					responseBody.setResult(new Result(Status.OK, ""));
					responseBody.setId(list.get(0).getId());
				} else {
					responseBody.setResult(new Result(Status.ERROR, null,
							"数据返回错误"));
					logger.error("checkPassMeet 数据返回错误,应返回一条数据  "
							+ GSONUtil.toJson(list));
				}
			} else {
				responseBody.setResult(new Result(Status.OK, ""));
			}
		} catch (Exception e) {
			responseBody.setResult(new Result(Status.ERROR, null, "查询失败"));
			if (logger.isErrorEnabled()) {
				logger.error("checkPassMeet 查询失败", e);
			}
		}

		return responseBody;
	}

	/*
	 * 将项目的list封装成一个FormatData对象
	 * 
	 * @param plist
	 * 
	 * @return
	 */
	public FormatData setFormatData(List<Project> plist) {
		FormatData formatData = new FormatData();
		Map<String, Object> projectmap = new HashMap<String, Object>();
		List<String> ids = new ArrayList<String>();

		for (Project p : plist) {
			projectmap.put(p.getProjectDepartid().toString(), p);
			ids.add(p.getProjectDepartid().toString());
		}
		formatData.setIds(ids);
		formatData.setMap(projectmap);
		return formatData;
	}

	/**
	 * 将项目的list封装成一个FormatData对象
	 * 
	 * @param plist
	 * @return
	 */
	public FormatData setFormatUserDate(List<User> userList) {
		FormatData formatData = new FormatData();
		Map<String, Object> usermap = new HashMap<String, Object>();
		for (User user : userList) {
			usermap.put(user.getDepartmentId().toString(), user);
		}
		formatData.setMap(usermap);
		return formatData;
	}

	/**
	 * 将项目的list封装成一个FormatData对象
	 * 
	 * @param plist
	 * @return
	 */
	public FormatData setFormatdepeentDate(List<Department> depList) {
		FormatData formatData = new FormatData();
		List<String> ids = new ArrayList<String>();
		Map<String, Object> usermap = new HashMap<String, Object>();
		Long deptId = null;
		for (Department dep : depList) {
			usermap.put(String.valueOf(dep.getId()), dep);
			deptId = dep.getManagerId();
			if (null != deptId && deptId.longValue() > 0L) {
				ids.add(String.valueOf(deptId));
			}
		}
		formatData.setIds(ids);
		formatData.setMap(usermap);
		return formatData;
	}

	/***
	 * 更新文件：1.投资意向书;2.更新尽职调查;3.更新投资协议|股权转让...
	 * 
	 * @param p
	 * @param request
	 * @return
	 */
	@com.galaxyinternet.common.annotation.Logger(operationScope = { LogType.LOG, LogType.MESSAGE })
	@ResponseBody
	@RequestMapping(value = "/updateCommonFile", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<ProjectQuery> updateCommonFile(ProjectQuery p,
			HttpServletRequest request) {
		ResponseData<ProjectQuery> responseBody = new ResponseData<ProjectQuery>();
		// 参数校验
		if (p.getPid() == null
				|| p.getStage() == null
				|| !SopConstant._progress_pattern_.matcher(p.getStage())
						.matches() || p.getParseDate() == null) {
			responseBody.setResult(new Result(Status.ERROR, null, "必要的参数丢失!"));
			return responseBody;
		}
		SopFile sopFile = new SopFile();
		String fileKey = null;
		try {
			if (p.getId() != null) {
				sopFile = sopFileService.queryById(p.getId());
			} else {
				responseBody.setResult(new Result(Status.ERROR, null,
						"所存在的更新文件丢失!"));
				return responseBody;
			}
			if(sopFile==null){
				responseBody.setResult(new Result(Status.ERROR, null,
						"文件不存在"));
				return responseBody;
			}
			
			if (sopFile.getFileKey() == null) {
				fileKey = String.valueOf(IdGenerator
						.generateId(OSSHelper.class));
			} else {
				fileKey = sopFile.getFileKey();
			}
			// 更新文件服务器信息
			UploadFileResult result = uploadFileToOSS(request, fileKey,
					tempfilePath);
			if (result == null
					|| !result.getResult().getStatus().equals(Result.Status.OK)) {
				responseBody
						.setResult(new Result(Status.ERROR, null, "缺失相应文档!"));
				return responseBody;
			}
			// 更新文件数据表信息
			sopFile.setFileSource(String.valueOf(p.getType()));
			sopFile.setFileType(p.getFileType());
			sopFile.setFileWorktype(p.getFileWorktype());
			sopFile.setFileName(result.getFileName());
			sopFile.setFileSuffix(result.getFileSuffix());
			sopFile.setBucketName(result.getBucketName());
			sopFile.setFileKey(fileKey);
			sopFile.setFileLength(result.getContentLength());
			sopFileService.updateById(sopFile);
			responseBody.setResult(new Result(Status.OK, null, "更新文件成功!"));
		} catch (Exception e) {
			responseBody.getResult().addError("更新失败");
			logger.error("更新失败", e);
		}

		return responseBody;
	}

	@ResponseBody
	@RequestMapping(value = "/getDegreeByParent/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<Dict> getDictByParent(@PathVariable String id,
			HttpServletRequest request) {
		ResponseData<Dict> responseBody = new ResponseData<Dict>();
		List<Dict> dicts = new ArrayList<Dict>();
		Dict dict = null;
		Result result = new Result();
		try {
			for (DictEnum.degree degree : DictEnum.degree.values()) {
				dict = new Dict();
				dict.setCode(degree.getCode());
				dict.setName(degree.getName());
				dicts.add(dict);
			}
		} catch (PlatformException e) {
			result.setErrorCode(e.getCode() + "");
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			result.setMessage("系统错误");
			result.addError("系统错误");
			logger.error("根据parentId查找数据字典错误", e);
		}
		if (!("null").equals(id)) {
			result.setMessage(id);
		}
		result.setStatus(Status.OK);
		responseBody.setEntityList(dicts);
		responseBody.setResult(result);
		return responseBody;
	}
//TODO
	/**
	 * 排期池列表查询
	 * 
	 * @param type
	 *            -- 1表示立项会、2表示投决会、3表示CEO内评会
	 */
	@ResponseBody
	@RequestMapping(value = "/queryScheduling/{type}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<MeetingScheduling> searchSchedulingList(
			HttpServletRequest request, @PathVariable("type") Integer type,
			@RequestBody MeetingSchedulingBo query) {

		ResponseData<MeetingScheduling> responseBody = new ResponseData<MeetingScheduling>();
		Direction direction = Direction.ASC;
		String property = "apply_time";
		
		if(!StringUtils.isEmpty(query.getProperty())){
			if("desc".equals(query.getDirection())){
				direction = Direction.DESC;
			}else{
				direction = Direction.ASC;
			}
			if("meetingDate".equals(query.getProperty())){
				property = "meeting_date";
			}else if("reserveTimeStart".equals(query.getProperty())){
				property = "reserve_time_start";
			}
		}
		PageRequest pageable = new PageRequest(0, 10, direction,
				property);
		Page<MeetingScheduling> pageEntity = new Page<MeetingScheduling>(null,
				pageable, null);
		List<MeetingScheduling> sl = new ArrayList<MeetingScheduling>();
		pageEntity.setTotal(new Long(0));
		pageEntity.setContent(sl);
		responseBody.setPageList(pageEntity);
		responseBody.setResult(new Result(Status.OK, ""));
		try {
			if (type.intValue() == 0) {
				query.setMeetingType(DictEnum.meetingType.立项会.getCode());
			} else if (type.intValue() == 1) {
				query.setMeetingType(DictEnum.meetingType.投决会.getCode());
			} else if (type.intValue() == 2) {
				query.setMeetingType(DictEnum.meetingType.CEO评审.getCode());
			}
			byte isEdit = 0;
			User user = (User) getUserFromSession(request);

			List<Long> roleIdList = userRoleService.selectRoleIdByUserId(user
					.getId());

			if (roleIdList.contains(UserConstant.DMS)) {

				if (type.intValue() == 0 || type.intValue() == 1) {
					isEdit = 1;
				} else {
					isEdit = 2;
				}
			} else if (roleIdList.contains(UserConstant.CEOMS)) {
				if (type.intValue() == 2) {
					isEdit = 1;
				} else {
					isEdit = 2;
				}
			} else {
				responseBody.setResult(new Result(Status.ERROR, null, "不可见!"));
				return responseBody;
			}
			/**
			 * 默认查询待排期的，可通过条件查询搜索其他
			 */
			if (query.getScheduleStatus() == null) {
				query.setScheduleStatus(0);
			}
			/**
			 * 查询出所有的事业线
			 */
			List<Long> depids = new ArrayList<Long>();
			Map<Long, Department> careerlineMap = new HashMap<Long, Department>();
			Department d = new Department();
			if (query.getCareline() != null) {
				Department de = departmentService.queryById(new Long(query
						.getCareline()));
				careerlineMap.put(de.getId(), de);
				depids.add(de.getId());
			} else {
				List<Department> careerlineList = departmentService
						.queryList(d);
				for (Department department : careerlineList) {
					careerlineMap.put(department.getId(), department);
					depids.add(department.getId());
				}
				d.setType(1);
			}
			/**
			 * 查询出相关的所有项目
			 */
			List<Project> projectCommonList = new ArrayList<Project>();
			List<MeetingScheduling> schedulingList = new ArrayList<MeetingScheduling>();
			Page<MeetingScheduling> pageList = null;
			ProjectBo mpb = new ProjectBo();
			if (query.getKeyword() != null) {
				mpb.setKeyword(query.getKeyword());
			}
			mpb.setDeptIdList(depids);
			projectCommonList = projectService.queryList(mpb);
			/**
			 * 根据相关项目查找排期池数据
			 */
			List<Long> pids = new ArrayList<Long>();
			if (projectCommonList != null && projectCommonList.size() > 0) {
				for (Project pr : projectCommonList) {
					pids.add(pr.getId());
				}
				query.setProjectIdList(pids);
				pageList = meetingSchedulingService
						.getMeetingList(
								query,
								new PageRequest(query.getPageNum(), query.getPageSize(),direction,
										property));
				schedulingList = pageList.getContent();
			} else {
				return responseBody;
			}
			/***
			 * 若无数据则返回
			 */
			if (schedulingList.size() == 0) {
				return responseBody;
			}

			List<String> ids = new ArrayList<String>();
			for (MeetingScheduling ms : schedulingList) {
				byte Edit = 1;
				Integer sheduleStatus = ms.getScheduleStatus();
				if (sheduleStatus == 2 || sheduleStatus == 3) {
					Edit = 0;
				}
				if (ms.getReserveTimeStart() != null) {
					long time = System.currentTimeMillis();
					long startTime = ms.getReserveTimeStart().getTime();
					if ((time > startTime) && sheduleStatus == 1) {
						Edit = 0;
					}
				}
				ms.setIsEdit(Edit);
				ids.add(String.valueOf(ms.getProjectId()));
			}

			/**
			 * 查询出相关的所有项目
			 */
			ProjectBo pb = new ProjectBo();
			pb.setIds(ids);
			List<Project> projectList = projectService.queryList(pb);

			// 组装项目的投资经理uid
			List<String> uids = new ArrayList<String>();
			for (Project pr : projectList) {
				uids.add(String.valueOf(pr.getCreateUid()));
			}
			// 获取投资经理的过会率
/*			PassRateBo borate = new PassRateBo();
			borate.setUids(uids);
			borate.setRateType(type.intValue());
			List<PassRate> prateList = passRateService.queryListById(borate);
			Map<Long, PassRate> passRateMap = new HashMap<Long, PassRate>();
			
			if (prateList.size() > 0) {
				for (PassRate pr : prateList) {
					passRateMap.put(pr.getUid(), pr);
				}
			}*/
			// 组装数据
			for (MeetingScheduling ms : schedulingList) {
				for (Project p : projectList) {
					if (ms.getProjectId().longValue() == p.getId().longValue()) {
						PassRateBo borate = new PassRateBo();
						/*if (passRateMap.get(p.getCreateUid()) != null) {
							ms.setMeetingRate(passRateMap.get(p.getCreateUid())
									.getRate());
						} else {
							ms.setMeetingRate(new Double(0));
						}*/
						if(ms.getMeetingType().equals(DictEnum.meetingType.立项会.getCode())){
							borate.setRateType(0);							
						}else if(ms.getMeetingType().equals(DictEnum.meetingType.投决会.getCode())){
							borate.setRateType(1);
						}else if(ms.getMeetingType().equals(DictEnum.meetingType.CEO评审.getCode())){
							borate.setRateType(2);
						}												
						// 获取投资经理的过会率						
						borate.setUids(uids);						
						List<PassRate> prateList = passRateService.queryListById(borate);
						Map<Long, PassRate> passRateMap = new HashMap<Long, PassRate>();
						
						if (prateList.size() > 0) {
							for (PassRate pr : prateList) {
								passRateMap.put(pr.getUid(), pr);
							}
						}

						if (passRateMap.get(p.getCreateUid()) != null) {
							ms.setMeetingRate(passRateMap.get(p.getCreateUid())
									.getRate());
						} else {
							ms.setMeetingRate(new Double(0));
						}
						ms.setProjectCode(p.getProjectCode());
						ms.setProjectName(p.getProjectName());
						ms.setProjectCareerline(careerlineMap.get(
								p.getProjectDepartid()).getName());
						ms.setCreateUname(p.getCreateUname());
					}

				}
			}
			 
			pageEntity.setTotal(pageList.getTotal());
			pageEntity.setContent(pageList.getContent());
			responseBody.setPageList(pageEntity);
			responseBody.setResult(new Result(Status.OK, ""));
			return responseBody;
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
	 * 更新排期池时间/updateReserveTime
	 */
	@com.galaxyinternet.common.annotation.Logger(operationScope = LogType.MESSAGE)
	@ResponseBody
	@RequestMapping(value = "/updateReserveTime", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<MeetingScheduling> updateReserveTime(
			HttpServletRequest request,
			@RequestBody List<MeetingScheduling> query) {
		
		ResponseData<MeetingScheduling> responseBody = new ResponseData<MeetingScheduling>();
		if (query == null || query.size() == 0) {
			responseBody.setResult(new Result(Status.ERROR, null, "无操作数据!"));
			return responseBody;
		}
		//对要排期的会议进行重组
		List<Long> ids=new ArrayList<Long>();
		List<Long> meetingids = new ArrayList<Long>();
    	for(MeetingScheduling ms:query){
    		meetingids.add(ms.getId());
    	}
    	//判断是否为投决会
    	boolean flag = false;
    	Map<Long,List<String>> proMap= new HashMap<Long,List<String>>();
    	//要操作的排期
    	MeetingScheduling msche = new MeetingScheduling();
    	msche.setIds(meetingids);
    	List<MeetingScheduling> mslist = meetingSchedulingService.getMeetingListByIds(msche);
    	Map<Long,MeetingScheduling> msmap = new HashMap<Long,MeetingScheduling>();
    	if(mslist.isEmpty()){
    		responseBody.setResult(new Result(Status.ERROR, null, "无要操作的排期!"));
    	}else{
    		for(MeetingScheduling meeting:mslist){
    			ids.add(meeting.getProjectId());
    			msmap.put(meeting.getId(), meeting);
    			//如果是投决会阶段-则对人|财|法的进行发送邮件，即查找认领人的id
    			if (DictEnum.meetingType.投决会.getCode().equals(
    					meeting.getMeetingType())) {
    				flag = true;
    				SopTaskBo soptask = new SopTaskBo();
    				soptask.setProjectId(meeting.getProjectId());
    		    	List<SopTask> taskList = sopTaskService.selectForTaskOverList(soptask);
    		    	if(!taskList.isEmpty()){
    		    		List<String> users = new ArrayList<String>();
    		    		for(SopTask task:taskList){
    		    			if(!users.contains(String.valueOf(task.getAssignUid()))){
    		    				users.add(String.valueOf(task.getAssignUid()));
    		    			}
    		    		}
    		    		proMap.put(meeting.getProjectId(), users);
    		    	}
    				
    			}
    		
    		}
    	}
    	//查找项目
    	List<Project> projectList = projectService.queryListById(ids);
    	Map<Long,Project> mapProject = new HashMap<Long,Project>();
		if(projectList.isEmpty()){
			responseBody.setResult(new Result(Status.ERROR, null, "无相关操作项目!"));
		}else{
			//对批量排期的会议项目进行组装
			for(Project pr:projectList){
				mapProject.put(pr.getId(), pr);
				if(!flag){
					List<String> userlists = new ArrayList<String>();
					if(!userlists.contains(String.valueOf(pr.getCreateUid()))){
						userlists.add(String.valueOf(pr.getCreateUid()));
	    			}
					proMap.put(pr.getId(), userlists);
				}
			}
		}
		StringBuffer proNameList=new StringBuffer();
		try {
			for (MeetingScheduling ms : query) {
				String mestr = "";
				String messageType = null;
				MeetingScheduling oldMs = msmap.get(ms.getId());
				Project pj = mapProject.get(oldMs.getProjectId());
				//验证已经已通过|已否决的会议不能进行排期
				if(2 == oldMs.getScheduleStatus() || 3 == oldMs.getScheduleStatus()){
					proNameList.append(pj.getProjectName());
					proNameList.append(" ");
					continue;
				}
				if (DictEnum.meetingType.投决会.getCode().equals(
						ms.getMeetingType())) {
					mestr = DictEnum.meetingType.投决会.getName();
					messageType = "11.3";
				}
				if (DictEnum.meetingType.立项会.getCode().equals(
						ms.getMeetingType())) {
					mestr = DictEnum.meetingType.立项会.getName();
					messageType = "11.2";
				}
				if (DictEnum.meetingType.CEO评审.getCode().equals(
						ms.getMeetingType())) {
					mestr = DictEnum.meetingType.CEO评审.getName();
					messageType = "11.1";
				}
				String messageInfo = mestr + "排期时间为";
				if (oldMs.getReserveTimeStart() != null
						&& ms.getReserveTimeStart() != null) {
					messageInfo = mestr + "排期时间变更为";
				}
				if (oldMs.getReserveTimeStart() != null
						&& ms.getReserveTimeStart() == null) {
					messageInfo = mestr + "排期时间已取消";
				}
				List<String> userLs = proMap.get(pj.getId());
				//获取项目中的user
				List<User> userlist = userService.queryListById(userLs);
				User belongUser = userService.queryById(pj.getCreateUid());
				// 如果是更新或取消排期时间
				if (oldMs.getReserveTimeStart() != null
						&& oldMs.getReserveTimeEnd() != null) {
					// 取消排期时间
					if (ms.getReserveTimeStart() == null
							&& ms.getReserveTimeEnd() == null) {
						ms.setScheduleStatus(0);
						meetingSchedulingService.updateByIdSelective(ms);
						sendTaskProjectEmail(request,pj,messageInfo,userlist,null,null,0,UrlNumber.three);
						belongUser.setKeyword("cancle:"+DateUtil.convertDateToStringForChina(oldMs.getReserveTimeStart()));	
					} else {
						// 更新会议时间
						if (oldMs.getReserveTimeStart().getTime() != ms
								.getReserveTimeStart().getTime()
								|| oldMs.getReserveTimeEnd().getTime() != ms
										.getReserveTimeEnd().getTime()) {
							meetingSchedulingService.updateByIdSelective(ms);
							sendTaskProjectEmail(request,pj,messageInfo,userlist,ms.getReserveTimeStart(),ms.getReserveTimeEnd(),1,UrlNumber.two);
							belongUser.setKeyword("update:"+DateUtil.convertDateToStringForChina(oldMs.getReserveTimeStart()));	
						}
					}
				} else {
					// 新安排会议时间
					if (ms.getReserveTimeStart() != null
							&& ms.getReserveTimeEnd() != null) {
						meetingSchedulingService.updateByIdSelective(ms);
						sendTaskProjectEmail(request,pj,messageInfo,userlist,ms.getReserveTimeStart(),ms.getReserveTimeEnd(),1,UrlNumber.one);
						belongUser.setKeyword("insert:"+DateUtil.convertDateToStringForChina(ms.getReserveTimeStart()));	
					}

				}
				ControllerUtils.setRequestParamsForMessageTip(request, belongUser, pj.getProjectName(), pj.getId(), messageType, UrlNumber.one);
			}
		} catch (Exception e) {
			responseBody.setResult(new Result(Status.ERROR, null, "更新失败!"));
			e.printStackTrace();
			return responseBody;
		}
		responseBody.setResult(new Result(Status.OK, null, "更新成功!"));
		if(proNameList.length() > 0){
			responseBody.setResult(new Result(Status.OK, null, proNameList.toString()+" 项目已经通过,不能进行排期!"));
		}
		return responseBody;
	}
	
	/**
	 * 根据业务发送邮件和消息：人、财、法
	 * @param request
	 * @param pj 项目
	 * @param messageInfo 邮件内容
	 * @param userlist 发送的用户
	 * @param 
	 * @param type 0：取消发送邮件 1:更新或新增邮件
	 */
	public void sendTaskProjectEmail(HttpServletRequest request,Project pj,String messageInfo,List<User> userlist,Timestamp reserveTimeStart,Timestamp reserveTimeEnd,Integer type,UrlNumber number){
		if(!userlist.isEmpty()){
			for(User user: userlist){
				sendMailToTZJL(request, type, user.getEmail(),
						user.getRealName(),
						pj.getProjectCode(),pj.getProjectName(),
						messageInfo, reserveTimeStart,
						reserveTimeEnd);
				ControllerUtils.setRequestParamsForMessageTip(request,
						user, pj.getProjectName(), pj.getId(),
						number);
			}
		}
	}
	
//TODO
	/**
	 * 更新排期池时间/updateReserveTime-客户端用
	 */
	@com.galaxyinternet.common.annotation.Logger(operationScope = {LogType.MESSAGE,LogType.IOSPUSHMESS})
	@ResponseBody
	@RequestMapping(value = "/updateReserveTimeByApp", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<MeetingScheduling> updateReserveTimeByApp(
			HttpServletRequest request, @RequestBody MeetingScheduling query) {
		ResponseData<MeetingScheduling> responseBody = new ResponseData<MeetingScheduling>();
		if (query == null || query.getMeetingType() == null 
				|| "".equals(query.getMeetingType().trim()) || query.getId() == null) {
			responseBody.setResult(new Result(Status.ERROR, null, "无操作数据!"));
			return responseBody;
		}
		try {
			MeetingScheduling oldMs = meetingSchedulingService.queryById(query
					.getId());
			
			MeetingScheduling  redisPush = new MeetingScheduling();
			redisPush.setId(oldMs.getId());
			redisPush.setProjectId(oldMs.getProjectId());
			redisPush.setMeetingType(oldMs.getMeetingType());
			
			//System.out.println("tdj------------- 11");
			String mestr = "";
			String messageType = null;
			if (DictEnum.meetingType.投决会.getCode().equals(
					query.getMeetingType())) {
				mestr = DictEnum.meetingType.投决会.getName();
				messageType = "11.3";
			}
			if (DictEnum.meetingType.立项会.getCode().equals(
					query.getMeetingType())) {
				mestr = DictEnum.meetingType.立项会.getName();
				messageType = "11.2";
			}
			if (DictEnum.meetingType.CEO评审.getCode().equals(
					query.getMeetingType())) {
				mestr = DictEnum.meetingType.CEO评审.getName();
				messageType = "11.1";
			}
			String messageInfo = mestr + "排期时间为";
			if (oldMs.getReserveTimeStart() != null
					&& query.getReserveTimeStart() != null) {
				messageInfo = mestr + "排期时间变更为";
			}
			if (oldMs.getReserveTimeStart() != null
					&& query.getReserveTimeStart() == null) {
				messageInfo = mestr + "排期时间已取消";
			}
			Project pj = projectService.queryById(oldMs.getProjectId());
//			System.out.println("tdj------------- 12");
			
			redisPush.setProjectName(pj.getProjectName());
			redisPush.setCreateId(pj.getCreateUid().toString());
			
			List<String> users = new ArrayList<String>();
			if (DictEnum.meetingType.投决会.getCode().equals(
					query.getMeetingType())) {
				SopTaskBo soptask = new SopTaskBo();
				soptask.setProjectId(oldMs.getProjectId());
		    	List<SopTask> taskList = sopTaskService.selectForTaskOverList(soptask);
		    	if(!taskList.isEmpty()){
		    		for(SopTask task:taskList){
		    			if(!users.contains(String.valueOf(task.getAssignUid()))){
		    				users.add(String.valueOf(task.getAssignUid()));
		    			}
		    		}
		    	
		    	}
				
			}else{
				users.add(String.valueOf(pj.getCreateUid()));
			}
			
			//System.out.println("tdj------------- 13");
			//获取项目中的user
			List<User> userlist = userService.queryListById(users);
			//System.out.println("tdj------------- 14");
			User belongUser = userService.queryById(pj.getCreateUid());
			//System.out.println("tdj------------- 15");
			// 如果是更新或取消排期时间
			if (oldMs.getReserveTimeStart() != null
					&& oldMs.getReserveTimeEnd() != null) {
				// 取消排期时间
				if (query.getReserveTimeStart() == null
						&& query.getReserveTimeEnd() == null) {
					query.setScheduleStatus(0);
					meetingSchedulingService.updateByIdSelective(query);
					//sendTaskProjectEmail(request,pj,messageInfo,userlist,null,null,0,UrlNumber.three);
					belongUser.setKeyword("cancle:"+DateUtil.convertDateToStringForChina(oldMs.getReserveTimeStart()));
					//新增往内存中写入 
					redisPush.setReserveTimeStart(query.getReserveTimeStart());
					cache.removeRedisSetOBJ(Constants.PUSH_MESSAGE_LIST, redisPush);
					//System.out.println("tdj------------- 16");
				} else {
					// 更新会议时间
					if (oldMs.getReserveTimeStart().getTime() != query
							.getReserveTimeStart().getTime()
							|| oldMs.getReserveTimeEnd().getTime() != query
									.getReserveTimeEnd().getTime()) {
						meetingSchedulingService.updateByIdSelective(query);
						//sendTaskProjectEmail(request,pj,messageInfo,userlist,query.getReserveTimeStart(),query.getReserveTimeEnd(),1,UrlNumber.two);
						belongUser.setKeyword("update:"+DateUtil.convertDateToStringForChina(oldMs.getReserveTimeStart())+","+DateUtil.convertDateToStringForChina(query.getReserveTimeStart()));	
						//System.out.println("tdj------------- 17");
						
						redisPush.setReserveTimeStart(oldMs.getReserveTimeStart());
						cache.removeRedisSetOBJ(Constants.PUSH_MESSAGE_LIST, redisPush);
						
						redisPush.setReserveTimeStart(query.getReserveTimeStart());
						cache.setRedisSetOBJ(Constants.PUSH_MESSAGE_LIST,redisPush);
						
					}
				}
			} else {
				// 新安排会议时间
				if (query.getReserveTimeStart() != null
						&& query.getReserveTimeEnd() != null) {
					meetingSchedulingService.updateByIdSelective(query);
					//sendTaskProjectEmail(request,pj,messageInfo,userlist,query.getReserveTimeStart(),query.getReserveTimeEnd(),1,UrlNumber.one);
					belongUser.setKeyword("insert:"+DateUtil.convertDateToStringForChina(query.getReserveTimeStart()));
					//System.out.println("tdj------------- 18");
					//新增的方法 2016/8/22
					redisPush.setReserveTimeStart(query.getReserveTimeStart());
					cache.setRedisSetOBJ(Constants.PUSH_MESSAGE_LIST,redisPush);
				}

			}
			ControllerUtils.setRequestParamsForMessageTip(request, belongUser, pj.getProjectName(), pj.getId(), messageType, UrlNumber.one);
		} catch (Exception e) {
			responseBody.setResult(new Result(Status.ERROR, null, "更新失败!"));
			e.printStackTrace();
			return responseBody;
		}
		responseBody.setResult(new Result(Status.OK, null, "更新成功!"));
		return responseBody;
	}


	/***
	 * 发送邮件
	 */
	public String sendMailToTZJL(HttpServletRequest request, Integer type,
			String toAddress, String tzjlName, String projectinfoCode,String projectinfoName,
			String messageInfo, Date meetingTimestart, Date meetingTimeend) {
		String starttime = null;
		String endtime = null;
		if(meetingTimestart != null){
			starttime = DateUtil.convertDateToStringForChina(meetingTimestart);
		}
		if(meetingTimeend != null){
			endtime = DateUtil.convertDateToStringForChina(meetingTimeend);
		}
		toAddress =toAddress+"@galaxyinternet.com";
		String content = MailTemplateUtils
				.getContentByTemplate(Constants.MAIL_PQC_CONTENT);
		String[] to = toAddress.split(";");
		if (to != null && to.length == 1) {
			int atIndex = toAddress.lastIndexOf("@");
			tzjlName = toAddress.substring(0, atIndex) + ":<br>您好!";
		}
		if (type == 0) {
			content = MailTemplateUtils
					.getContentByTemplate(Constants.MAIL_PQC_CONTENT_CANCLE);
			content = PlaceholderConfigurer.formatText(content, tzjlName,
					projectinfoCode,projectinfoName, messageInfo);
		} else {
			content = PlaceholderConfigurer.formatText(content, tzjlName,
					projectinfoCode,projectinfoName, messageInfo, starttime, endtime);
		}
		boolean success = SimpleMailSender.sendMultiMail(toAddress, "会议排期通知",
				content.toString());

		if (success) {
			return "success";
		} else {
			return "fail";
		}
	}
	
	/***
	 * 返回角色列表
	 */
	@ResponseBody
	@RequestMapping(value = "/getRoleList")
	public List<Long> getRoleList(HttpServletRequest request){
		User user = (User) getUserFromSession(request);
		List<Long> roleIdList = null;
		if(user != null){
			try{
				roleIdList = userRoleService.selectRoleIdByUserId(user
						.getId());
			}catch(Exception e){
				logger.error("获取角色列表失败!", e);
			}
		}
		return roleIdList;
	}

	//供app端实现保存公司法人信息
	/**
	 * 保存公司法人信息
	 * @param project
	 * @return 2016/6/13  app端 
	 * 
	 */
	@RequestMapping(value="/saveCompanyInfo", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseData<Project> saveCompanyInfo(@RequestBody Project project)
	{
		ResponseData<Project> data = new ResponseData<Project>();
		try
		{
			if(project == null || project.getId() == null)
			{
				data.getResult().addError("数据错误.");
				return data;
			}
			Project po = projectService.queryById(project.getId());
			if(po == null)
			{
				data.getResult().addError("数据错误.");
				return data;
			}
			projectService.updateById(project);
			
			po.setProjectCompany(project.getProjectCompany());
			po.setProjectCompanyCode(project.getProjectCompanyCode());
			po.setCompanyLegal(project.getCompanyLegal());
			po.setFormationDate(project.getFormationDate());
			data.setEntity(po);
		}
		catch (Exception e)
		{
			if(logger.isErrorEnabled())
			{
				logger.error("保存法人信息错误:"+project,e);
			}
			data.getResult().addError(e.getMessage());
		}
		return data;
	}
//TODO
	/**
	 * 根据枚举获取
	 * 获取枚举里的融资状态列表
	 * @param id
	 * @param request
	 * @return 2016/6/13 app端独有
	 */
	@ResponseBody
	@RequestMapping(value = "/getFinanceStatusByParent", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<Dict> getFinanceStatusByParent(HttpServletRequest request) {
		ResponseData<Dict> responseBody = new ResponseData<Dict>();
		List<Dict> dicts = new ArrayList<Dict>();
		Dict dict = null;
		Result result = new Result();
		try {
			for (DictEnum.financeStatus financeStatus : DictEnum.financeStatus.values()) {
				dict = new Dict();
				dict.setCode(financeStatus.getCode());
				dict.setName(financeStatus.getName());
				dicts.add(dict);
			}
		} catch (PlatformException e) {
			result.setErrorCode(e.getCode() + "");
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			result.setMessage("系统错误");
			result.addError("系统错误");
			logger.error("根据parentId查找数据字典错误", e);
		}
		result.setStatus(Status.OK);
		responseBody.setEntityList(dicts);
		responseBody.setResult(result);
		return responseBody;
	}
	//TODO
	/**
	 * app端排期会排期(待排期,已排期)
	 * 	 
	 */
	@ResponseBody
	@RequestMapping(value = "/queryMescheduling", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<MeetingScheduling> queryMescheduling(HttpServletRequest request, @RequestBody MeetingSchedulingBo query) {

		ResponseData<MeetingScheduling> responseBody = new ResponseData<MeetingScheduling>();
			
		try {
			
			User user = (User) getUserFromSession(request);

			List<Long> roleIdList = userRoleService.selectRoleIdByUserId(user
					.getId());

			if (roleIdList.contains(UserConstant.TZJL)){
				if(query.getScheduleStatus()==1){
				 SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
				    String dateString = formatter.format(new Date());  
				    query.setStartTime(dateString); 
				}
				/**
				 * 查询出相关的所有项目
				 */
				List<Project> projectCommonList = new ArrayList<Project>();
				List<MeetingScheduling> schedulingList = new ArrayList<MeetingScheduling>();
				Page<MeetingScheduling> pageList = null;
				ProjectBo mpb = new ProjectBo();
				if (query.getKeyword() != null) {
					mpb.setKeyword(query.getKeyword());
				}
				mpb.setCreateUid(user.getId());
				projectCommonList = projectService.queryList(mpb);
				/**
				 * 根据相关项目查找排期池数据
				 */
				List<Long> pids = new ArrayList<Long>();
				if (projectCommonList != null && projectCommonList.size() > 0) {
					for (Project pr : projectCommonList) {
						pids.add(pr.getId());
					}
					query.setProjectIdList(pids);				
					pageList = meetingSchedulingService
							.getMeetingList(
									query,
									new PageRequest(query.getPageNum(), query.getPageSize()));
					schedulingList = pageList.getContent();
				} else {
					MeetingSchedulingBo mebo = new MeetingSchedulingBo();
					mebo.setCountscheduleStatusd(0);
					mebo.setCountscheduleStatusy(0);
					responseBody.setEntity(mebo);
					return responseBody;
				} 			
				if(schedulingList!=null && schedulingList.size()>0){	
					List<String> ids = new ArrayList<String>();
					for (MeetingScheduling ms : schedulingList) {
						byte Edit = 1;
						Integer sheduleStatus = ms.getScheduleStatus();
						if (sheduleStatus == 2 || sheduleStatus == 3) {
							Edit = 0;
						}
						if (ms.getReserveTimeStart() != null) {
							long time = System.currentTimeMillis();
							long startTime = ms.getReserveTimeStart().getTime();
							if ((time > startTime) && sheduleStatus == 1) {
								Edit = 0;
							}
						}
						ms.setIsEdit(Edit);
						ids.add(String.valueOf(ms.getProjectId()));
						
						if(ms.getScheduleStatus()==0 && ms.getMeetingType().equals(DictEnum.meetingType.CEO评审.getCode())){
							
							Long s  = meetingSchedulingService.selectpdCount(ms);
							String ss = pdcount(s.intValue());
							ms.setPdCount(ss);	
							ms.setPaiQCount(s);
								
						}
						
						if(ms.getScheduleStatus()==0 && !ms.getMeetingType().equals(DictEnum.meetingType.CEO评审.getCode())){
							
							Long s  = meetingSchedulingService.selectltpdCount(ms);
							String ss = pdcount(s.intValue());
							ms.setPdCount(ss);	
							ms.setPaiQCount(s);
								
						}
						
						
					}
		
					/**
					 * 查询出相关的所有项目
					 */
					ProjectBo pb = new ProjectBo();
					pb.setIds(ids);
					List<Project> projectList = projectService.queryList(pb);
		
					// 组装数据
					for (MeetingScheduling ms : schedulingList) {
						for (Project p : projectList) {
							if (ms.getProjectId().longValue() == p.getId().longValue()) {								
								ms.setProjectCode(p.getProjectCode());
								ms.setProjectName(p.getProjectName());							
								ms.setCreateUname(p.getCreateUname());
							}
		
						}
					}
					
				}
				MeetingSchedulingBo mebo = new MeetingSchedulingBo();
				
				query.setScheduleStatus(0);
				if(query.getScheduleStatus()==0){
					query.setStartTime(null);
					List<MeetingScheduling> ms1 = meetingSchedulingService.queryList(query);
					if(ms1!=null && ms1.size()>0){
						mebo.setCountscheduleStatusd(ms1.size());
					}else{
						mebo.setCountscheduleStatusd(0);
					}	
				}
				query.setScheduleStatus(1);
				if(query.getScheduleStatus()==1){
					  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
					    String dateString = formatter.format(new Date());  
					    query.setStartTime(dateString); 
					List<MeetingScheduling> ms2 = meetingSchedulingService.queryList(query);
					if(ms2!=null && ms2.size()>0){
						mebo.setCountscheduleStatusy(ms2.size());
					}else{
						mebo.setCountscheduleStatusy(0);
					}
				}
				responseBody.setPageList(pageList);
				responseBody.setEntity(mebo);
				return responseBody;
			}
			else if (roleIdList.contains(UserConstant.HHR)){

				if(query.getScheduleStatus()==1){
					 SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
					    String dateString = formatter.format(new Date());  
					    query.setStartTime(dateString); 
					}
				/**
				 * 查询出相关的所有项目
				 */
				List<Project> projectCommonList = new ArrayList<Project>();
				List<MeetingScheduling> schedulingList = new ArrayList<MeetingScheduling>();
				Page<MeetingScheduling> pageList = null;
				ProjectBo mpb = new ProjectBo();
				if (query.getKeyword() != null) {
					mpb.setKeyword(query.getKeyword());
				}
				mpb.setProjectDepartid(user.getDepartmentId());
				projectCommonList = projectService.queryList(mpb);
				/**
				 * 根据相关项目查找排期池数据
				 */
				List<Long> pids = new ArrayList<Long>();
				if (projectCommonList != null && projectCommonList.size() > 0) {
					for (Project pr : projectCommonList) {
						pids.add(pr.getId());
					}
					query.setProjectIdList(pids);				
					pageList = meetingSchedulingService
							.getMeetingList(
									query,
									new PageRequest(query.getPageNum(), query.getPageSize()));
					schedulingList = pageList.getContent();
				} else {
					MeetingSchedulingBo mebo = new MeetingSchedulingBo();
					mebo.setCountscheduleStatusd(0);
					mebo.setCountscheduleStatusy(0);
					responseBody.setEntity(mebo);
					return responseBody;
				} 			
				if(schedulingList!=null && schedulingList.size()>0){	
					List<String> ids = new ArrayList<String>();
					for (MeetingScheduling ms : schedulingList) {
						byte Edit = 1;
						Integer sheduleStatus = ms.getScheduleStatus();
						if (sheduleStatus == 2 || sheduleStatus == 3) {
							Edit = 0;
						}
						if (ms.getReserveTimeStart() != null) {
							long time = System.currentTimeMillis();
							long startTime = ms.getReserveTimeStart().getTime();
							if ((time > startTime) && sheduleStatus == 1) {
								Edit = 0;
							}
						}
						ms.setIsEdit(Edit);
						ids.add(String.valueOf(ms.getProjectId()));
					}
		
					/**
					 * 查询出相关的所有项目
					 */
					ProjectBo pb = new ProjectBo();
					pb.setIds(ids);
					List<Project> projectList = projectService.queryList(pb);
		
					// 组装数据
					for (MeetingScheduling ms : schedulingList) {
						for (Project p : projectList) {
							if (ms.getProjectId().longValue() == p.getId().longValue()) {								
								ms.setProjectCode(p.getProjectCode());
								ms.setProjectName(p.getProjectName());								
								ms.setCreateUname(p.getCreateUname());
							}
		
						}
						if(ms.getScheduleStatus()==0 && ms.getMeetingType().equals(DictEnum.meetingType.CEO评审.getCode())){
							
							Long s  = meetingSchedulingService.selectpdCount(ms);
							String ss = pdcount(s.intValue());
							ms.setPdCount(ss);	
							ms.setPaiQCount(s);
								
						}
						
						if(ms.getScheduleStatus()==0 && !ms.getMeetingType().equals(DictEnum.meetingType.CEO评审.getCode())){
							
							Long s  = meetingSchedulingService.selectltpdCount(ms);
							String ss = pdcount(s.intValue());
							ms.setPdCount(ss);	
							ms.setPaiQCount(s);
								
						}
						
						
						
					}
					
				}
				MeetingSchedulingBo mebo = new MeetingSchedulingBo();
				
				query.setScheduleStatus(0);
				if(query.getScheduleStatus()==0){
					query.setStartTime(null);
					List<MeetingScheduling> ms1 = meetingSchedulingService.queryList(query);
					if(ms1!=null && ms1.size()>0){
						mebo.setCountscheduleStatusd(ms1.size());
					}else{
						mebo.setCountscheduleStatusd(0);
					}	
				}
				query.setScheduleStatus(1);
				if(query.getScheduleStatus()==1){
					  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
					    String dateString = formatter.format(new Date());  
					    query.setStartTime(dateString); 
					List<MeetingScheduling> ms2 = meetingSchedulingService.queryList(query);
					if(ms2!=null && ms2.size()>0){
						mebo.setCountscheduleStatusy(ms2.size());
					}else{
						mebo.setCountscheduleStatusy(0);
					}
				}
				responseBody.setPageList(pageList);
				responseBody.setEntity(mebo);
				return responseBody;
			}
			else {
				if(query.getScheduleStatus()==1){
					 SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
					    String dateString = formatter.format(new Date());  
					    query.setStartTime(dateString); 
					} 
				/**
				 * 查询出所有的事业线
				 */
				List<Long> depids = new ArrayList<Long>();
				Map<Long, Department> careerlineMap = new HashMap<Long, Department>();
				Department d = new Department();
				if (query.getCareline() != null) {
					Department de = departmentService.queryById(new Long(query
							.getCareline()));
					careerlineMap.put(de.getId(), de);
					depids.add(de.getId());
				} else {
					d.setType(1);
					List<Department> careerlineList = departmentService
							.queryList(d);
					for (Department department : careerlineList) {
						careerlineMap.put(department.getId(), department);
						depids.add(department.getId());
					}
					
				}
				/**
				 * 查询出相关的所有项目
				 */
				List<Project> projectCommonList = new ArrayList<Project>();
				List<MeetingScheduling> schedulingList = new ArrayList<MeetingScheduling>();
				Page<MeetingScheduling> pageList = null;
				ProjectBo mpb = new ProjectBo();
				if (query.getKeyword() != null) {
					mpb.setKeyword(query.getKeyword());
				}
				mpb.setDeptIdList(depids);
				projectCommonList = projectService.queryList(mpb);
				/**
				 * 根据相关项目查找排期池数据
				 */
				List<Long> pids = new ArrayList<Long>();
				if (projectCommonList != null && projectCommonList.size() > 0) {
					for (Project pr : projectCommonList) {
						pids.add(pr.getId());
					}
					query.setProjectIdList(pids);				
					pageList = meetingSchedulingService
							.getMeetingList(
									query,
									new PageRequest(query.getPageNum(), query.getPageSize()));
					schedulingList = pageList.getContent();
				} else {
					MeetingSchedulingBo mebo = new MeetingSchedulingBo();
					mebo.setCountscheduleStatusd(0);
					mebo.setCountscheduleStatusy(0);
					responseBody.setEntity(mebo);
					return responseBody;
				} 			
				if(schedulingList!=null && schedulingList.size()>0){	
					List<String> ids = new ArrayList<String>();
					for (MeetingScheduling ms : schedulingList) {
						byte Edit = 1;
						Integer sheduleStatus = ms.getScheduleStatus();
						if (sheduleStatus == 2 || sheduleStatus == 3) {
							Edit = 0;
						}
						if (ms.getReserveTimeStart() != null) {
							long time = System.currentTimeMillis();
							long startTime = ms.getReserveTimeStart().getTime();
							if ((time > startTime) && sheduleStatus == 1) {
								Edit = 0;
							}
						}
						ms.setIsEdit(Edit);
						ids.add(String.valueOf(ms.getProjectId()));
					}
		
					/**
					 * 查询出相关的所有项目
					 */
					ProjectBo pb = new ProjectBo();
					pb.setIds(ids);
					List<Project> projectList = projectService.queryList(pb);
		
					// 组装数据
					for (MeetingScheduling ms : schedulingList) {
						for (Project p : projectList) {
							if (ms.getProjectId().longValue() == p.getId().longValue()) {
								
								ms.setProjectCode(p.getProjectCode());
								ms.setProjectName(p.getProjectName());
								ms.setProjectCareerline(careerlineMap.get(
										p.getProjectDepartid()).getName());
								ms.setCreateUname(p.getCreateUname());
							}
		
						}
						
						if(ms.getScheduleStatus()==0 && ms.getMeetingType().equals(DictEnum.meetingType.CEO评审.getCode())){
							
							Long s  = meetingSchedulingService.selectpdCount(ms);
							String ss = pdcount(s.intValue());
							ms.setPdCount(ss);	
							ms.setPaiQCount(s);
								
						}
						
						if(ms.getScheduleStatus()==0 && !ms.getMeetingType().equals(DictEnum.meetingType.CEO评审.getCode())){
							
							Long s  = meetingSchedulingService.selectltpdCount(ms);
							String ss = pdcount(s.intValue());
							ms.setPdCount(ss);	
							ms.setPaiQCount(s);
								
						}
					}
					
				}
				MeetingSchedulingBo mebo = new MeetingSchedulingBo();
				
				query.setScheduleStatus(0);
				if(query.getScheduleStatus()==0){
					query.setStartTime(null);
					List<MeetingScheduling> ms1 = meetingSchedulingService.queryList(query);
					if(ms1!=null && ms1.size()>0){
						mebo.setCountscheduleStatusd(ms1.size());
					}else{
						mebo.setCountscheduleStatusd(0);
					}	
				}
				query.setScheduleStatus(1);
				if(query.getScheduleStatus()==1){
					  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
					    String dateString = formatter.format(new Date());  
					    query.setStartTime(dateString); 
					List<MeetingScheduling> ms2 = meetingSchedulingService.queryList(query);
					if(ms2!=null && ms2.size()>0){
						mebo.setCountscheduleStatusy(ms2.size());
					}else{
						mebo.setCountscheduleStatusy(0);
					}
				}
				responseBody.setPageList(pageList);
				responseBody.setEntity(mebo);
				return responseBody;
				}
		} catch (PlatformException e) {
			responseBody.setResult(new Result(Status.ERROR, null,
					"queryList faild"));
			if (logger.isErrorEnabled()) {
				logger.error("queryList ", e);
			}
		}
		return responseBody;

	}
	
	
	
	
	//TODO
	/**all
	 * app端排期会排期(待排期,已排期)
	 * 	 
	 */
	@ResponseBody
	@RequestMapping(value = "/queryMeschedulingAll", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<MeetingScheduling> queryMeschedulingAll(HttpServletRequest request, @RequestBody MeetingSchedulingBo query) {

		ResponseData<MeetingScheduling> responseBody = new ResponseData<MeetingScheduling>();
			
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
		    String dateString = formatter.format(new Date());  
		    query.setStartTime(dateString); 
			User user = (User) getUserFromSession(request);

			List<Long> roleIdList = userRoleService.selectRoleIdByUserId(user.getId());
			if (roleIdList.contains(UserConstant.TZJL)){
				

				/**
				 * 查询出相关的所有项目
				 */
				List<Project> projectCommonList = new ArrayList<Project>();
				List<MeetingScheduling> schedulingList = new ArrayList<MeetingScheduling>();
				Page<MeetingScheduling> pageList = null;
				ProjectBo mpb = new ProjectBo();
				if (query.getKeyword() != null) {
					mpb.setKeyword(query.getKeyword());
				}
				mpb.setCreateUid(user.getId());
				projectCommonList = projectService.queryList(mpb);
				/**
				 * 根据相关项目查找排期池数据
				 */
				List<Long> pids = new ArrayList<Long>();
				if (projectCommonList != null && projectCommonList.size() > 0) {
					for (Project pr : projectCommonList) {
						pids.add(pr.getId());
					}
					query.setProjectIdList(pids);				
					pageList = meetingSchedulingService
							.queryMeschedulingAll(
									query,
									new PageRequest(query.getPageNum(), query.getPageSize()));
					schedulingList = pageList.getContent();
				} else {
					
					return responseBody;
				} 			
				if(schedulingList!=null && schedulingList.size()>0){	
					List<String> ids = new ArrayList<String>();
					for (MeetingScheduling ms : schedulingList) {
						byte Edit = 1;
						Integer sheduleStatus = ms.getScheduleStatus();
						if (sheduleStatus == 2 || sheduleStatus == 3) {
							Edit = 0;
						}
						if (ms.getReserveTimeStart() != null) {
							long time = System.currentTimeMillis();
							long startTime = ms.getReserveTimeStart().getTime();
							if ((time > startTime) && sheduleStatus == 1) {
								Edit = 0;
							}
						}
						ms.setIsEdit(Edit);
						ids.add(String.valueOf(ms.getProjectId()));
					}
		
					/**
					 * 查询出相关的所有项目
					 */
					ProjectBo pb = new ProjectBo();
					pb.setIds(ids);
					List<Project> projectList = projectService.queryList(pb);
		
					// 组装数据
					for (MeetingScheduling ms : schedulingList) {
						for (Project p : projectList) {
							if (ms.getProjectId().longValue() == p.getId().longValue()) {
								
								ms.setProjectCode(p.getProjectCode());
								ms.setProjectName(p.getProjectName());								
								ms.setCreateUname(p.getCreateUname());
							}
		
						}
						if(ms.getScheduleStatus()==0 && ms.getMeetingType().equals(DictEnum.meetingType.CEO评审.getCode())){
							
							Long s  = meetingSchedulingService.selectpdCount(ms);
							String ss = pdcount(s.intValue());
							ms.setPdCount(ss);	
							ms.setPaiQCount(s);
								
						}
						
						if(ms.getScheduleStatus()==0 && !ms.getMeetingType().equals(DictEnum.meetingType.CEO评审.getCode())){
							
							Long s  = meetingSchedulingService.selectltpdCount(ms);
							String ss = pdcount(s.intValue());
							ms.setPdCount(ss);	
							ms.setPaiQCount(s);
								
						}
						
					}
					
				}		
				responseBody.setPageList(pageList);
				return responseBody;
			}
			else if (roleIdList.contains(UserConstant.HHR)){
				
				/**
				 * 查询出相关的所有项目
				 */
				List<Project> projectCommonList = new ArrayList<Project>();
				List<MeetingScheduling> schedulingList = new ArrayList<MeetingScheduling>();
				Page<MeetingScheduling> pageList = null;
				ProjectBo mpb = new ProjectBo();
				if (query.getKeyword() != null) {
					mpb.setKeyword(query.getKeyword());
				}
				mpb.setProjectDepartid(user.getDepartmentId());
				projectCommonList = projectService.queryList(mpb);
				/**
				 * 根据相关项目查找排期池数据
				 */
				List<Long> pids = new ArrayList<Long>();
				if (projectCommonList != null && projectCommonList.size() > 0) {
					for (Project pr : projectCommonList) {
						pids.add(pr.getId());
					}
					query.setProjectIdList(pids);				
					pageList = meetingSchedulingService
							.queryMeschedulingAll(
									query,
									new PageRequest(query.getPageNum(), query.getPageSize()));
					schedulingList = pageList.getContent();
				} else {
					
					return responseBody;
				} 			
				if(schedulingList!=null && schedulingList.size()>0){	
					List<String> ids = new ArrayList<String>();
					for (MeetingScheduling ms : schedulingList) {
						byte Edit = 1;
						Integer sheduleStatus = ms.getScheduleStatus();
						if (sheduleStatus == 2 || sheduleStatus == 3) {
							Edit = 0;
						}
						if (ms.getReserveTimeStart() != null) {
							long time = System.currentTimeMillis();
							long startTime = ms.getReserveTimeStart().getTime();
							if ((time > startTime) && sheduleStatus == 1) {
								Edit = 0;
							}
						}
						ms.setIsEdit(Edit);
						ids.add(String.valueOf(ms.getProjectId()));
					}
		
					/**
					 * 查询出相关的所有项目
					 */
					ProjectBo pb = new ProjectBo();
					pb.setIds(ids);
					List<Project> projectList = projectService.queryList(pb);
		
					// 组装数据
					for (MeetingScheduling ms : schedulingList) {
						for (Project p : projectList) {
							if (ms.getProjectId().longValue() == p.getId().longValue()) {
								
								ms.setProjectCode(p.getProjectCode());
								ms.setProjectName(p.getProjectName());								
								ms.setCreateUname(p.getCreateUname());
							}
		
						}
						if(ms.getScheduleStatus()==0 && ms.getMeetingType().equals(DictEnum.meetingType.CEO评审.getCode())){
							
							Long s  = meetingSchedulingService.selectpdCount(ms);
							String ss = pdcount(s.intValue());
							ms.setPdCount(ss);	
							ms.setPaiQCount(s);
								
						}
						
						if(ms.getScheduleStatus()==0 && !ms.getMeetingType().equals(DictEnum.meetingType.CEO评审.getCode())){
							
							Long s  = meetingSchedulingService.selectltpdCount(ms);
							String ss = pdcount(s.intValue());
							ms.setPdCount(ss);	
							ms.setPaiQCount(s);
								
						}
					}
					
				}		
				responseBody.setPageList(pageList);
				return responseBody;
			}else{

				/**
				 * 查询出所有的事业线
				 */
				List<Long> depids = new ArrayList<Long>();
				Map<Long, Department> careerlineMap = new HashMap<Long, Department>();
				Department d = new Department();
				if (query.getCareline() != null) {
					Department de = departmentService.queryById(new Long(query
							.getCareline()));
					careerlineMap.put(de.getId(), de);
					depids.add(de.getId());
				} else {
					d.setType(1);
					List<Department> careerlineList = departmentService
							.queryList(d);
					for (Department department : careerlineList) {
						careerlineMap.put(department.getId(), department);
						depids.add(department.getId());
					}
					
				}
				/**
				 * 查询出相关的所有项目
				 */
				List<Project> projectCommonList = new ArrayList<Project>();
				List<MeetingScheduling> schedulingList = new ArrayList<MeetingScheduling>();
				Page<MeetingScheduling> pageList = null;
				ProjectBo mpb = new ProjectBo();
				if (query.getKeyword() != null) {
					mpb.setKeyword(query.getKeyword());
				}
				mpb.setDeptIdList(depids);
				projectCommonList = projectService.queryList(mpb);
				/**
				 * 根据相关项目查找排期池数据
				 */
				List<Long> pids = new ArrayList<Long>();
				if (projectCommonList != null && projectCommonList.size() > 0) {
					for (Project pr : projectCommonList) {
						pids.add(pr.getId());
					}
					query.setProjectIdList(pids);				
					pageList = meetingSchedulingService
							.queryMeschedulingAll(
									query,
									new PageRequest(query.getPageNum(), query.getPageSize()));
					schedulingList = pageList.getContent();
				} else {
					
					return responseBody;
				} 			
				if(schedulingList!=null && schedulingList.size()>0){	
					List<String> ids = new ArrayList<String>();
					for (MeetingScheduling ms : schedulingList) {					
						byte Edit = 1;
						Integer sheduleStatus = ms.getScheduleStatus();
						if (sheduleStatus == 2 || sheduleStatus == 3) {
							Edit = 0;
						}
						if (ms.getReserveTimeStart() != null) {
							long time = System.currentTimeMillis();
							long startTime = ms.getReserveTimeStart().getTime();
							if ((time > startTime) && sheduleStatus == 1) {
								Edit = 0;
							}
						}
						ms.setIsEdit(Edit);
						ids.add(String.valueOf(ms.getProjectId()));
					}
		
					/**
					 * 查询出相关的所有项目
					 */
					ProjectBo pb = new ProjectBo();
					pb.setIds(ids);
					List<Project> projectList = projectService.queryList(pb);
		
					// 组装数据
					for (MeetingScheduling ms : schedulingList) {
						for (Project p : projectList) {
							if (ms.getProjectId().longValue() == p.getId().longValue()) {
								
								ms.setProjectCode(p.getProjectCode());
								ms.setProjectName(p.getProjectName());
								ms.setProjectCareerline(careerlineMap.get(
										p.getProjectDepartid()).getName());
								ms.setCreateUname(p.getCreateUname());
							}
		
						}
						
						if(ms.getScheduleStatus()==0 && ms.getMeetingType().equals(DictEnum.meetingType.CEO评审.getCode())){
							
							Long s  = meetingSchedulingService.selectpdCount(ms);
							String ss = pdcount(s.intValue());
							ms.setPdCount(ss);	
							ms.setPaiQCount(s);
								
						}
						
						if(ms.getScheduleStatus()==0 && !ms.getMeetingType().equals(DictEnum.meetingType.CEO评审.getCode())){
							
							Long s  = meetingSchedulingService.selectltpdCount(ms);
							String ss = pdcount(s.intValue());
							ms.setPdCount(ss);	
							ms.setPaiQCount(s);
								
						}
					}
					
				}		
				responseBody.setPageList(pageList);
				return responseBody;
			}
		} catch (PlatformException e) {
			responseBody.setResult(new Result(Status.ERROR, null,
					"queryList faild"));
			if (logger.isErrorEnabled()) {
				logger.error("queryList ", e);
			}
		}
		return responseBody;
	}
	
	
	/**
	 * 查询指定的项目信息接口
	 * @author gxc
	 * @return 2016/6/13修改
	 */
	@com.galaxyinternet.common.annotation.Logger
	@ResponseBody
	@RequestMapping(value = "/xmms/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<Project> selectAppProject(@PathVariable("pid") String pid, HttpServletRequest request) {
		ResponseData<Project> responseBody = new ResponseData<Project>();

		if (pid == null) {
			responseBody.setResult(new Result(Status.ERROR, null, "重要参数缺失"));
			return responseBody;
		}
		Project project = projectService.queryById(Long.parseLong(pid));
		try {
			if (project != null) {

				Department Department = new Department();//
				Department.setId(project.getProjectDepartid());
				Department queryOne = departmentService.queryOne(Department);
				Long deptId = null;
				if (queryOne != null) {
					project.setProjectCareerline(queryOne.getName());
					deptId = queryOne.getManagerId();
					if (null != deptId && deptId.longValue() > 0L) {
						User queryById = userService.queryById(queryOne.getManagerId());
						if (queryById != null) {
							project.setHhrName(queryById.getRealName());
						}
					}
				}

/*				if (project.getIndustryOwn() != null) {
					Department Dt = new Department();
					Dt.setId(project.getIndustryOwn());
					Department queryTwo = departmentService.queryOne(Dt);
					if (queryTwo != null) {
						project.setIndustryOwnDs(queryTwo.getName());
					}
				}
				
*/
				//新修改的项目行业归属 2016/12/5
				if(project.getIndustryOwn()!=null){
	                String name=DictEnum.industryOwn.getNameByCode(
	        		 project.getIndustryOwn().toString());
				if (name != null) {
						project.setIndustryOwnDs(name);				
					}else{
						project.setIndustryOwnDs(null);
					}
				}	
				// 1.添加项目描述的暂无标识
				if (project.getProjectDescribe() != null && !project.getProjectDescribe().equals("")) {
					project.setProjectDescribezw(1);
				} else {
					project.setProjectDescribezw(0);
				}
				// 2.添加公司定位的暂无标识
				if (project.getCompanyLocation() != null && !project.getCompanyLocation().equals("")) {
					project.setCompanyLocationzw(1);
				} else {
					project.setCompanyLocationzw(0);
				}
				// 3.用户画像的暂无标识
				if (project.getUserPortrait() != null && !project.getUserPortrait().equals("")) {
					project.setUserPortraitzw(1);
				} else {

					project.setUserPortraitzw(0);
				}
				// 4.产品服务的暂无标识
				if (project.getProjectBusinessModel() != null && !project.getProjectBusinessModel().equals("")) {
					project.setProjectBusinessModelzw(1);
				} else {
					project.setProjectBusinessModelzw(0);
				}
				// 5.竟情分析的暂无标识
				if (project.getProspectAnalysis() != null && !project.getProspectAnalysis().equals("")) {
					project.setProspectAnalysiszw(1);
				} else {
					project.setProspectAnalysiszw(0);
				}
				// 6.运营数据的暂无标识
				if (project.getOperationalData() != null && !project.getOperationalData().equals("")) {
					project.setOperationalDatazw(1);
				} else {
					project.setOperationalDatazw(0);
				}
				// 7.行业分析的暂无标识
				if (project.getIndustryAnalysis() != null && !project.getIndustryAnalysis().equals("")) {
					project.setIndustryAnalysiszw(1);
				} else {
					project.setIndustryAnalysiszw(0);
				}
				// 8.下一轮融资路径的暂无标识
				if (project.getNextFinancingSource() != null && !project.getNextFinancingSource().equals("")) {
					project.setNextFinancingSourcezw(1);
				} else {
					project.setNextFinancingSourcezw(0);
				}
				//9.项目的要点的暂无标识
				if(project.getProjectDescribeFinancing()!=null &&!project.getProjectDescribeFinancing().equals("")){
					project.setProjectDescribeFinancingZW(1);
				}else{
					project.setProjectDescribeFinancingZW(0);
				}


			} else {
				responseBody.setResult(new Result(Status.ERROR, null, "未查找到指定项目信息!"));
				return responseBody;
			}

			responseBody.setEntity(project);
			ControllerUtils.setRequestParamsForMessageTip(request, project.getProjectName(), project.getId());

		} catch (Exception e) {
			responseBody.setResult(new Result(Status.ERROR, null, "查询项目详情出现异常"));
			if (logger.isErrorEnabled()) {
				logger.error("selectAppProject ", e);
			}
		}
		return responseBody;
	}
	
	
	/**
	 * 2016/12/24 添加 取消排期的功能 只改 排期的状态    schedule_status 改为 2 (投资经理取消排期)
	 */
	@ResponseBody
	@RequestMapping(value = "/qxpq", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<MeetingSchedulingBo> qxpq(@RequestBody MeetingSchedulingBo meetingSchedulingBo,
			HttpServletRequest request) throws ParseException {
		ResponseData<MeetingSchedulingBo> responseBody = new ResponseData<MeetingSchedulingBo>();
		if (meetingSchedulingBo == null || meetingSchedulingBo.getId() == null) {
			responseBody.setResult(new Result(Status.ERROR, null, "必要的参数丢失!"));
			return responseBody;
		}

		MeetingScheduling ji = meetingSchedulingService.queryById(meetingSchedulingBo.getId());
		try{
			if( ji==null){
				responseBody.setResult(new Result(Status.ERROR, null, "不存在此排期"));
				return responseBody;
			}
			if(ji!=null && !ji.getScheduleStatus().equals(DictEnum.meetingSheduleResult.待排期.getCode())){
							
				responseBody.setResult(new Result(Status.ERROR, null, "此排期不在待排期中"));
				return responseBody;
			}
		
			MeetingScheduling ss = new MeetingScheduling();
				ss.setId(meetingSchedulingBo.getId());
				ss.setScheduleStatus(DictEnum.meetingSheduleResult.已通过.getCode());
				ss.setStatus(DictEnum.meetingResult.通过.getCode());
			int nui = meetingSchedulingService.updateById(ss);
			if(nui>0){
				responseBody.setResult(new Result(Status.OK, null, "取消排期成功"));				
			}else{
				responseBody.setResult(new Result(Status.ERROR, null, "取消排期异常"));
				return responseBody;
			}
		}catch(Exception e){
			responseBody.setResult(new Result(Status.ERROR, null, "取消排期异常"));
			if (logger.isErrorEnabled()) {
				logger.error("selectAppProject ", e);
			}
		}
		return responseBody;
		
	}
	
	public static String pdcount(Integer s){
		if(s>=0 && s<=9){
			return "小于10";
		}else if(10<=s && s<=29){
			return "小于30";
		}else if(30<=s&&s<=49){
			return "小于50";
		}else if(50<=s&&s<=99){
			return "大于50";
		}else if(s>100){
			return "大于100";
		}
		return "";
	}
	
	
	
	

}
