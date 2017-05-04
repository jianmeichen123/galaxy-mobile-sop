package com.galaxyinternet.rili.controller;


import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.galaxyinternet.common.controller.BaseControllerImpl;
import com.galaxyinternet.framework.core.constants.Constants;
import com.galaxyinternet.framework.core.model.Page;
import com.galaxyinternet.framework.core.model.PageRequest;
import com.galaxyinternet.framework.core.model.ResponseData;
import com.galaxyinternet.framework.core.model.Result;
import com.galaxyinternet.framework.core.model.Result.Status;
import com.galaxyinternet.framework.core.service.BaseService;
import com.galaxyinternet.model.user.User;
import com.galaxyinternet.rili.model.ScheduleMessage;
import com.galaxyinternet.rili.model.ScheduleMessageUser;
import com.galaxyinternet.rili.service.ScheduleMessageService;
import com.galaxyinternet.rili.service.ScheduleMessageUserService;


@Controller
@RequestMapping("/galaxy/schedule/message")
public class ScheduleMessageController  extends BaseControllerImpl<ScheduleMessage, ScheduleMessage> {

	final Logger logger = LoggerFactory.getLogger(ScheduleMessageController.class);
	
	@Autowired
	private ScheduleMessageService scheduleMessageService;

	@Autowired
	private ScheduleMessageUserService scheduleMessageUserService;


	@Override
	protected BaseService<ScheduleMessage> getBaseService() {
		return this.scheduleMessageService;
	}

	
	
	
	/**
	 * 个人消息 列表查询
     */
	@ResponseBody
	@RequestMapping(value = "/querySchedule", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<ScheduleMessageUser> querySchedule(HttpServletRequest request,@RequestBody ScheduleMessageUser query ) {
		
		ResponseData<ScheduleMessageUser> responseBody = new ResponseData<ScheduleMessageUser>();
		
		try {
			User user = (User) request.getSession().getAttribute(Constants.SESSION_USER_KEY);
			
			
			PageRequest pageable = new PageRequest();
			
			Integer pageNum = query.getPageNum() != null ? query.getPageNum() : 0;
			Integer pageSize = query.getPageSize() != null ? query.getPageSize() : 10;
			String property = query.getProperty() != null ? "mu."+query.getProperty() : "mu.created_time";
			String dir = query.getDirection() != null ? query.getDirection() : "desc";
			
			query.setProperty(property);
			query.setDirection(dir);
			//Direction direction = Direction.fromString(dir);
			pageable = new PageRequest(pageNum,pageSize);

			
			//muser 查询条件
			query.setUid(user.getId());
			query.setIsDel((byte) 0);
			
			//mContent 查询条件
			ScheduleMessage mQ = new ScheduleMessage();
			mQ.setStatus((byte) 0);
			query.setMessage(mQ);
			
			
			//结果查询  封装
			Page<ScheduleMessageUser> qList = scheduleMessageService.queryPerMessAndConvertPage(query,pageable);
			
			//responseBody.setEntityList(qList);
			
			responseBody.setPageList(qList);
			responseBody.setResult(new Result(Status.OK, ""));
		} catch (Exception e) {
			responseBody.setResult(new Result(Status.ERROR, null,"查询失败"));
			logger.error("querySchedule 查询失败",e);
		}
		
		return responseBody;
	}
	
	
	
	/**
	 * 已读
     */
	@ResponseBody
	@RequestMapping(value = "/toRead/{muid}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<ScheduleMessageUser> toRead(HttpServletRequest request,@PathVariable Long muid ) {
		
		ResponseData<ScheduleMessageUser> responseBody = new ResponseData<ScheduleMessageUser>();
		
		try {
			User user = (User)request.getSession().getAttribute(Constants.SESSION_USER_KEY);
			
			ScheduleMessageUser updateU = new ScheduleMessageUser();
			updateU.setId(muid);
			updateU.setIsRead((byte)1); //0:未读    1:已读
			scheduleMessageUserService.updateById(updateU);
			
			responseBody.setResult(new Result(Status.OK, ""));
		} catch (Exception e) {
			responseBody.setResult(new Result(Status.ERROR, null,"异常"));
			logger.error("ScheduleMessageController . toRead 异常",e);
		}
		
		return responseBody;
	}
	
	

	/**
	 * 个人消息列表  设为全部已读
	 * 1.查询出 消息user 表中      个人的   可用   未读  未删除  的数据
	 * 2.查询出消息内容列表          状态为可用的消息
     */
	@ResponseBody
	@RequestMapping(value = "/perMessageToRead", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<ScheduleMessageUser> perMessageToRead(HttpServletRequest request ) {
		
		ResponseData<ScheduleMessageUser> responseBody = new ResponseData<ScheduleMessageUser>();
		
		try {
			Object objU = request.getSession().getAttribute(Constants.SESSION_USER_KEY);
			
			//结果查询  封装
			scheduleMessageService.perMessageToRead(objU);
			
			responseBody.setResult(new Result(Status.OK, ""));
		} catch (Exception e) {
			responseBody.setResult(new Result(Status.ERROR, null,"异常"));
			logger.error("ScheduleMessageController . perMessageToRead 异常",e);
		}
		
		return responseBody;
	}
	
	
	
	/**
	 * 个人消息列表  清空
	 * 1.查询出 消息user 表中      个人的  未删除  的数据
	 * 2.查询出消息内容列表          状态为可用的消息
     */
	@ResponseBody
	@RequestMapping(value = "/perMessageToClear", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<ScheduleMessageUser> perMessageToClear(HttpServletRequest request ) {
		
		ResponseData<ScheduleMessageUser> responseBody = new ResponseData<ScheduleMessageUser>();
		
		try {
			Object objU = request.getSession().getAttribute(Constants.SESSION_USER_KEY);
			
			//结果查询  封装
			scheduleMessageService.perMessageToClear(objU);
			
			responseBody.setResult(new Result(Status.OK, ""));
		} catch (Exception e) {
			responseBody.setResult(new Result(Status.ERROR, null,"异常"));
			logger.error("ScheduleMessageController . perMessageToRead 异常",e);
		}
		
		return responseBody;
	}
	
	
}



