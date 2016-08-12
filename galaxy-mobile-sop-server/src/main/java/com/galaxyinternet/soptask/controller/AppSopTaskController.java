package com.galaxyinternet.soptask.controller;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.galaxyinternet.bo.SopTaskBo;
import com.galaxyinternet.common.annotation.LogType;
import com.galaxyinternet.common.controller.BaseControllerImpl;
import com.galaxyinternet.common.dictEnum.DictEnum;
import com.galaxyinternet.common.utils.ControllerUtils;
import com.galaxyinternet.exception.PlatformException;
import com.galaxyinternet.framework.core.constants.Constants;
import com.galaxyinternet.framework.core.model.ResponseData;
import com.galaxyinternet.framework.core.model.Result;
import com.galaxyinternet.framework.core.model.Result.Status;
import com.galaxyinternet.framework.core.service.BaseService;
import com.galaxyinternet.model.operationLog.UrlNumber;
import com.galaxyinternet.model.project.Project;
import com.galaxyinternet.model.soptask.SopTask;
import com.galaxyinternet.model.user.User;
import com.galaxyinternet.service.ProjectService;
import com.galaxyinternet.service.SopTaskService;
import com.galaxyinternet.service.UserService;

@Controller
@RequestMapping("/galaxy/appSoptask")
public class AppSopTaskController extends BaseControllerImpl<SopTask, SopTaskBo> {

	final Logger logger = LoggerFactory.getLogger(AppSopTaskController.class);
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private SopTaskService sopTaskService;	

	@Override
	protected BaseService<SopTask> getBaseService() {
		// TODO Auto-generated method stub
		return this.sopTaskService;
	}
	
	/**
	 * App端待办任务认领
	 */
	 @com.galaxyinternet.common.annotation.Logger
	@ResponseBody
	@RequestMapping(value = "/{id}/receiveAppTask",method = RequestMethod.GET ,  produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<SopTask> receiveHandlerTask(@PathVariable("id") String tid ,HttpServletRequest request) {
		//当前登录人
		User user = (User) request.getSession().getAttribute(Constants.SESSION_USER_KEY);					
		ResponseData<SopTask> responseBody = new ResponseData<SopTask>();
		try {
			SopTask sopTask=new SopTask();        
			if(StringUtils.isNotBlank(tid)){
				sopTask.setId(Long.parseLong(tid));
			}
			sopTask.setTaskStatus(DictEnum.taskStatus.待完工.getCode());
		   
			SopTask queryById = sopTaskService.queryById(Long.parseLong(tid));
	
			sopTask.setAssignUid(user.getId());
			sopTask.setTaskFlag(queryById.getTaskFlag());
			sopTask.setProjectId(queryById.getProjectId());
			 sopTaskService.updateById(sopTask);
//			 request.setAttribute("taskid", tid);	
			 responseBody.setResult(new Result(Status.OK,null,"待办认领成功!"));
//			k responseBody.setResult(new Result(Status.ERROR, null, "必要的参数丢失!"));
		} catch (Exception e) {
			responseBody.setResult(new Result(Status.ERROR,null,"待办认领失败!"));
			logger.error("待办认领操作发生异常", e);
		}
		return responseBody;
	}
	 
/*	 *//**
	  * 为满足开发需要消息提醒  认领任务后添加提醒 2016/8/9
	  * @param request
	  * @return
	  *//*
	    @com.galaxyinternet.common.annotation.Logger(operationScope = { LogType.LOG,LogType.MESSAGE})
	    @ResponseBody
	    @RequestMapping(value = "/goClaimtcPage",method = RequestMethod.GET)
		public ResponseData<SopTask> goClaimtcPage(HttpServletRequest request) {
	    	ResponseData<SopTask> responseBody = new ResponseData<SopTask>();
			//当前登录人
			User user = (User) request.getSession().getAttribute(
					Constants.SESSION_USER_KEY);
			SopTask sopTask=new SopTask();
			
			String id=request.getParameter("id");
			if(id!=null&&!"".equals(id)){
				sopTask.setId(Long.parseLong(id));
			}
			sopTask.setTaskStatus(DictEnum.taskStatus.待完工.getCode());
			
			try {
				SopTask queryById = sopTaskService.queryById(Long.parseLong(id));
				UrlNumber urlNum=null;
				String messageType = null;
				boolean flag = true;
				switch(queryById.getTaskFlag())
				{
					case 0: //完善简历
					  urlNum=UrlNumber.one;
						break;
					case 2 : //人事尽职调查报告
						 urlNum=UrlNumber.two;
						 messageType="8.1";
						break;
					case 3 : //法务尽职调查报告
						urlNum=UrlNumber.five;
						messageType="8.3";
						break;
					case 4 : //财务尽调报告
						urlNum=UrlNumber.three;
						messageType="8.2";
						break;
					case 8 : //资金拨付凭证
						urlNum=UrlNumber.four;
						break;
					case 9 : //工商变更登记凭证
						urlNum=UrlNumber.six;
						break;
					default :
						flag=false;
				}
				Project project = projectService.queryById(queryById.getProjectId());
				
				User manager = userService.queryById(project.getCreateUid());
				
				sopTask.setAssignUid(user.getId());
				sopTask.setTaskFlag(queryById.getTaskFlag());
				sopTask.setProjectId(queryById.getProjectId());
				sopTaskService.updateById(sopTask);
				 responseBody.setResult(new Result(Status.OK,null,"待办认领成功!"));			
				 ControllerUtils.setRequestParamsForMessageTip(request,manager,project.getProjectName(), project.getId(),messageType,urlNum);				 				
			} catch (Exception e) {
				responseBody.setResult(new Result(Status.ERROR,null,"待办认领失败!"));
				logger.error("待办认领操作发生异常", e);
			}
			return responseBody;
		}*/
	 
	 /**
	  * 为满足开发需要消息提醒  认领任务后添加提醒 2016/8/9
	  * @param request
	  * @return
	  */
	    @com.galaxyinternet.common.annotation.Logger(operationScope = { LogType.LOG,LogType.MESSAGE})
	    @ResponseBody
	    @RequestMapping(value = "/goClaimtcPage",method = RequestMethod.POST,  produces = MediaType.APPLICATION_JSON_VALUE)
		public ResponseData<SopTask> goClaimtcPage(@RequestBody SopTask query,HttpServletRequest request) {
	    	ResponseData<SopTask> responseBody = new ResponseData<SopTask>();
			//当前登录人
			User user = (User) request.getSession().getAttribute(
					Constants.SESSION_USER_KEY);
			SopTask sopTask=new SopTask();
			
			String id= query.getId().toString();
			
			if(id!=null&&!"".equals(id)){
				sopTask.setId(Long.parseLong(id));
			}
			sopTask.setTaskStatus(DictEnum.taskStatus.待完工.getCode());
			
			try {
				SopTask queryById = sopTaskService.queryById(Long.parseLong(id));
				UrlNumber urlNum=null;
				String messageType = null;
				boolean flag = true;
				switch(queryById.getTaskFlag())
				{
					case 0: //完善简历
					  urlNum=UrlNumber.one;
						break;
					case 2 : //人事尽职调查报告
						 urlNum=UrlNumber.two;
						 messageType="8.1";
						break;
					case 3 : //法务尽职调查报告
						urlNum=UrlNumber.five;
						messageType="8.3";
						break;
					case 4 : //财务尽调报告
						urlNum=UrlNumber.three;
						messageType="8.2";
						break;
					case 8 : //资金拨付凭证
						messageType="8.5";
						urlNum=UrlNumber.four;
						break;
					case 9 : //工商变更登记凭证
						messageType="8.4";
						urlNum=UrlNumber.six;
						break;
					default :
						flag=false;
				}
				Project project = projectService.queryById(queryById.getProjectId());
				
				User manager = userService.queryById(project.getCreateUid());
				
				sopTask.setAssignUid(user.getId());
				sopTask.setTaskFlag(queryById.getTaskFlag());
				sopTask.setProjectId(queryById.getProjectId());
				sopTaskService.updateById(sopTask);
				 responseBody.setResult(new Result(Status.OK,null,"待办认领成功!"));			
				 ControllerUtils.setRequestParamsForMessageTip(request,manager,project.getProjectName(), project.getId(),messageType,urlNum);				 				
			} catch (Exception e) {
				responseBody.setResult(new Result(Status.ERROR,null,"待办认领失败!"));
				logger.error("待办认领操作发生异常", e);
			}
			return responseBody;
		}
}
