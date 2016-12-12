package com.galaxyinternet.operationMessage.controller;


import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.galaxyinternet.bo.OperationMessageBo;
import com.galaxyinternet.common.controller.BaseControllerImpl;
import com.galaxyinternet.common.utils.StaticParamService;
import com.galaxyinternet.exception.PlatformException;
import com.galaxyinternet.framework.cache.Cache;
import com.galaxyinternet.framework.core.constants.UserConstant;
import com.galaxyinternet.framework.core.model.Page;
import com.galaxyinternet.framework.core.model.PageRequest;
import com.galaxyinternet.framework.core.model.ResponseData;
import com.galaxyinternet.framework.core.model.Result;
import com.galaxyinternet.framework.core.model.Result.Status;
import com.galaxyinternet.framework.core.service.BaseService;
import com.galaxyinternet.framework.core.utils.DateUtil;
import com.galaxyinternet.model.operationMessage.OperationMessage;
import com.galaxyinternet.model.project.AppDelete;
import com.galaxyinternet.model.project.AppSign;
import com.galaxyinternet.model.user.User;
import com.galaxyinternet.platform.constant.PlatformConst;
import com.galaxyinternet.service.AppDeleteService;
import com.galaxyinternet.service.AppSignService;
import com.galaxyinternet.service.OperationMessageService;
import com.galaxyinternet.service.UserRoleService;

@Controller
@RequestMapping("/galaxy/AppOperationMessage")
public class AppOperationMessageController extends BaseControllerImpl<OperationMessage, OperationMessageBo> {
	
	final Logger logger = LoggerFactory.getLogger(AppOperationMessageController.class);
	
	@Autowired
	private OperationMessageService operationMessageService;

	@Autowired
	private UserRoleService userRoleService;
	
	@Autowired
	private StaticParamService staticParamService;
	
	@Autowired
	private AppSignService appSignService;
	
	@Autowired
	private AppDeleteService appDeleteService;
	@Autowired
	Cache cache;
	
	@Override
	protected BaseService<OperationMessage> getBaseService() {
		return this.operationMessageService;
	}
	
	
	
	@ResponseBody
	@RequestMapping(value = "/AppQueryList", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<OperationMessage> queryUserList(HttpServletRequest request,@RequestBody OperationMessageBo operationMessageBo) {
		ResponseData<OperationMessage> responseBody = new ResponseData<OperationMessage>();
		Result result = new Result();
		try {
			User user = (User) getUserFromSession(request);
			List<Long> roleIdList = userRoleService.selectRoleIdByUserId(user.getId());
			
			/*if(operationMessageBo.getModule()!=null&&operationMessageBo.getModule() != PlatformConst.MODULE_BROADCAST_MESSAGE.intValue()){
				operationMessageBo.setBelongUid(user.getId());
			}*/
			initquery(operationMessageBo,user,roleIdList);
			//读未删除的消息 2016/10/17
		
			
			Page<OperationMessage> operationMessage = operationMessageService.selectListMessage(operationMessageBo,new PageRequest(operationMessageBo.getPageNum(), operationMessageBo.getPageSize()));
			
			responseBody.setPageList(operationMessage);
			responseBody.setResult(new Result(Status.OK, ""));
			return responseBody;	
		} catch (PlatformException e) {
			result.addError(e.getMessage(), e.getCode()+"");
			logger.error("queryUserList ", e);
		}
		return responseBody;
	}
	
	
	//实时统计消息数
	@ResponseBody
	@RequestMapping(value = "/remind", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<OperationMessageBo> remind(HttpServletRequest request) {
		ResponseData<OperationMessageBo> responseBody = new ResponseData<OperationMessageBo>();
		Result result = new Result();
		try {
			User user = (User) getUserFromSession(request);
			List<Long> roleIdList = userRoleService.selectRoleIdByUserId(user.getId());
			
 			OperationMessageBo operationMessageBo = new OperationMessageBo();
			Long lastTime = (Long) cache.get(PlatformConst.OPERATIO_NMESSAGE_TIME+getUserId(request));
			if(lastTime == null){
				lastTime= DateUtil.getCurrentDate().getTime();
			}
			operationMessageBo.setCreatedTimeStart(lastTime);
			initquery(operationMessageBo,user,roleIdList);
			
			/*operationMessageBo.setOperatorId(user.getId());
			operationMessageBo.setModule(PlatformConst.MODULE_BROADCAST_MESSAGE);//1
			 */			
			
			Long count = operationMessageService.selectCount(operationMessageBo);
			operationMessageBo.setCount(count);
			operationMessageBo.setOperatorId(null);
			responseBody.setEntity(operationMessageBo);
			return responseBody;	
		} catch (PlatformException e) {
			result.addError(e.getMessage(), e.getCode()+"");
			logger.error("queryUserList ", e);
		}
		return responseBody;
	}
	
	
	public void initquery(OperationMessageBo operationMessageBo,User user,List<Long> roleIdList){
		
		boolean flat = true;
		
		if(roleIdList.contains(UserConstant.DSZ) || roleIdList.contains(UserConstant.CEO)
				|| roleIdList.contains(UserConstant.DMS) || roleIdList.contains(UserConstant.CEOMS)
				|| roleIdList.contains(UserConstant.HHR)
				|| roleIdList.contains(UserConstant.TZJL)
				|| roleIdList.contains(UserConstant.HRZJ) || roleIdList.contains(UserConstant.FWZJ) || roleIdList.contains(UserConstant.CWZJ)
				|| roleIdList.contains(UserConstant.HRJL) || roleIdList.contains(UserConstant.FWJL) || roleIdList.contains(UserConstant.CWJL)
				){     
			operationMessageBo.setBelongDepartmentId(user.getDepartmentId());
			operationMessageBo.setBelongUid(user.getId());
		}else{
			 flat = false;
		}
		
		
		if(flat){
			Map<String,List<String>> typelist = StaticParamService.getRoleTypeList(roleIdList, staticParamService);
			if(typelist.get("inAll")!= null && !typelist.get("inAll").isEmpty()){
				operationMessageBo.setInAll(typelist.get("inAll"));
			}
			if(typelist.get("inPer")!= null && !typelist.get("inPer").isEmpty()){
				operationMessageBo.setInPer(typelist.get("inPer"));
			}
			if(typelist.get("inPat")!= null && !typelist.get("inPat").isEmpty()){
				operationMessageBo.setInPat(typelist.get("inPat"));
			}
		}
		
//		if(typelist!=null && !typelist.isEmpty()){
//			operationMessageBo.setMessageTypes(typelist);
//		}
		
	}
	//修改已读
	@ResponseBody
	@RequestMapping(value = "/updateMessageIsRead", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<OperationMessage> updateMessageIsRead(@RequestBody OperationMessageBo p,
			HttpServletRequest request) {
		ResponseData<OperationMessage> responseBody = new ResponseData<OperationMessage>();
		// 参数校验
		User user = (User) getUserFromSession(request);
		if (p.getId() == null) 
		{
			responseBody.setResult(new Result(Status.ERROR, null, "必要的参数丢失!"));
			return responseBody;
		}
		
		if(p.getId()!=null&&!p.getId().equals("")){
			if(operationMessageService.queryById(p.getId())==null){
				responseBody.setResult(new Result(Status.ERROR, null, "没找到此条消息"));
				return responseBody;
			}
			AppSign ss = new AppSign();
			ss.setUserId(user.getId().toString());
			ss.setMessageId(p.getId().toString());					
			Long s = appSignService.select(ss);	
			if(s!=0L){
				responseBody.setResult(new Result(Status.ERROR, null, "该消息已经标记为已读了!"));
				return responseBody;
			}
		}
		try {	

			AppSign sign = new AppSign();
			sign.setMessageId(p.getId().toString());
			sign.setUserId(user.getId().toString());
			sign.setUpdateTime(DateUtil.convertDateToStringChina (new Date()));
			sign.setIsRead(1);
			
			Long id = appSignService.insert(sign);
			
			responseBody.setId(id);
			System.out.println(id);
			
			responseBody.setResult(new Result(Status.OK, null, "更新数据库成功!"));
		} catch (Exception e) {
			responseBody.getResult().addError("更新失败");
			logger.error("更新失败", e);
		}

		return responseBody;
	}
	
	//2016/10/17
	//清空所有
	@ResponseBody
	@RequestMapping(value = "/allDelete", method = RequestMethod.GET)
	public ResponseData<OperationMessage> allDelete(HttpServletRequest request) {
		
		ResponseData<OperationMessage> responseBody = new ResponseData<OperationMessage>();

		try {
			User user = (User) getUserFromSession(request);
			List<Long> roleIdList = userRoleService.selectRoleIdByUserId(user.getId());
			
			/*if(operationMessageBo.getModule()!=null&&operationMessageBo.getModule() != PlatformConst.MODULE_BROADCAST_MESSAGE.intValue()){
				operationMessageBo.setBelongUid(user.getId());
			}*/
			OperationMessageBo operationMessageBo = new OperationMessageBo();
			initquery(operationMessageBo,user,roleIdList);
			
			
			List<OperationMessage> lis = operationMessageService.selectList(operationMessageBo);
			
			for(OperationMessage li:lis){
				
				AppDelete ss = new AppDelete();
				ss.setUserId(user.getId().toString());
				ss.setMessageId(li.getId().toString());					
				Long s = appDeleteService.select(ss);					
				if(s==0){
					AppDelete sign = new AppDelete();
					sign.setMessageId(li.getId().toString());
					sign.setUserId(user.getId().toString());
					sign.setUpdateTime(DateUtil.convertDateToStringChina (new Date()));
					sign.setIsDelete(1);	
					appDeleteService.insert(sign);	
				}else{
					continue;
				}
				
			}			
		
			responseBody.setResult(new Result(Status.OK, "批量更新成功"));
			return responseBody;	
		} catch (PlatformException e) {
			responseBody.getResult().addError("批量更新失败");
			logger.error("updateById ", e);
		}
		return responseBody;
	}
	
	
	//2016/10/17 2016/10/19号修改
		//已读未读  最终的全部已读未读
		@ResponseBody
		@RequestMapping(value = "/ydwd", method = RequestMethod.GET)
		public ResponseData<OperationMessage> allydwd(HttpServletRequest request) {
			
			ResponseData<OperationMessage> responseBody = new ResponseData<OperationMessage>();

			try {
				User user = (User) getUserFromSession(request);
				List<Long> roleIdList = userRoleService.selectRoleIdByUserId(user.getId());
				
				/*if(operationMessageBo.getModule()!=null&&operationMessageBo.getModule() != PlatformConst.MODULE_BROADCAST_MESSAGE.intValue()){
					operationMessageBo.setBelongUid(user.getId());
				}*/
				OperationMessageBo operationMessageBo = new OperationMessageBo();
				operationMessageBo.setUserId(user.getId().toString());
				initquery(operationMessageBo,user,roleIdList);

				//修改下 
				List<OperationMessage> lis = operationMessageService.selectList(operationMessageBo);
				
				for(OperationMessage li:lis){
					
					AppSign ss = new AppSign();
					ss.setUserId(user.getId().toString());
					ss.setMessageId(li.getId().toString());					
					Long s = appSignService.select(ss);	
					
					if(s==0){
						AppSign sign = new AppSign();
						sign.setMessageId(li.getId().toString());
						sign.setUserId(user.getId().toString());
						sign.setUpdateTime(DateUtil.convertDateToStringChina (new Date()));
						sign.setIsRead(1);
						appSignService.insert(sign);						
					}else{
						continue;
					}
						
				}			
			
				responseBody.setResult(new Result(Status.OK, "批量添加已读未读成功"));
				return responseBody;	
			} catch (PlatformException e) {
				responseBody.getResult().addError("批量添加已读未读失败");
				logger.error("allydwd ", e);
			}
			return responseBody;
		}
	
		//已删除
		@ResponseBody
		@RequestMapping(value = "/updateMessageIsDelete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
		public ResponseData<OperationMessage> updateMessageIsDelete(@RequestBody OperationMessageBo p,
				HttpServletRequest request) {
			ResponseData<OperationMessage> responseBody = new ResponseData<OperationMessage>();
			// 参数校验
			User user = (User) getUserFromSession(request);
			if (p.getId() == null) 
			{

				responseBody.setResult(new Result(Status.ERROR, null, "必要的参数丢失!"));
				return responseBody;
			}
			if(p.getId() != null && !p.getId().equals("")){
				if(operationMessageService.queryById(p.getId())==null){
					responseBody.setResult(new Result(Status.ERROR, null, "没找到此条消息"));
					return responseBody;
				}
			}
			try {	

				AppDelete sign = new AppDelete();
				sign.setMessageId(p.getId().toString());
				sign.setUserId(user.getId().toString());
				sign.setUpdateTime(DateUtil.convertDateToStringChina (new Date()));
				sign.setIsDelete(1);
				
				 appDeleteService.insert(sign);
				
				
				//System.out.println(id);
				
				responseBody.setResult(new Result(Status.OK, null, "更新数据库成功!"));
			} catch (Exception e) {
				responseBody.getResult().addError("更新失败");
				logger.error("更新失败", e);
			}

			return responseBody;
		}
		
	
}