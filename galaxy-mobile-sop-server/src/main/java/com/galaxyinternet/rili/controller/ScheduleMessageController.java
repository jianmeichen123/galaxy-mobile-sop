package com.galaxyinternet.rili.controller;


import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.galaxyinternet.common.controller.BaseControllerImpl;
import com.galaxyinternet.framework.core.constants.Constants;
import com.galaxyinternet.framework.core.model.PageRequest;
import com.galaxyinternet.framework.core.model.ResponseData;
import com.galaxyinternet.framework.core.model.Result;
import com.galaxyinternet.framework.core.model.Result.Status;
import com.galaxyinternet.framework.core.service.BaseService;
import com.galaxyinternet.model.user.User;
import com.galaxyinternet.rili.model.ScheduleMessage;
import com.galaxyinternet.rili.model.ScheduleMessageUser;
import com.galaxyinternet.rili.service.ScheduleMessageService;
import com.galaxyinternet.rili.util.ScheduleUtil;


@Controller
@RequestMapping("/galaxy/schedule/message")
public class ScheduleMessageController  extends BaseControllerImpl<ScheduleMessage, ScheduleMessage> {

	final Logger logger = LoggerFactory.getLogger(ScheduleMessageController.class);
	
	@Autowired
	private ScheduleMessageService scheduleMessageService;



	@Override
	protected BaseService<ScheduleMessage> getBaseService() {
		return this.scheduleMessageService;
	}

	
	
	
	/**
	 * 个人消息 列表查询
     */
	@ResponseBody
	@RequestMapping(value = "/querySchedule", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<ScheduleMessageUser> querySchedule(HttpServletRequest request,@RequestBody ScheduleMessageUser query ) {
		
		ResponseData<ScheduleMessageUser> responseBody = new ResponseData<ScheduleMessageUser>();
		
		try {
			User user = (User) request.getSession().getAttribute(Constants.SESSION_USER_KEY);
			
			
			PageRequest pageable = new PageRequest();
			
			Integer pageNum = query.getPageNum() != null ? query.getPageNum() : 0;
			Integer pageSize = query.getPageSize() != null ? query.getPageSize() : 10;
			String property = query.getProperty() != null ? query.getProperty() : "created_time";
			String direction = query.getDirection() != null ? query.getDirection() : "desc";
			
			pageable = new PageRequest(pageNum,pageSize, new Sort(direction,property));
			
			query.setUid(user.getId());
			
			//结果查询  封装
			List<ScheduleMessageUser> qList = scheduleMessageService.queryAndConvertList(query,pageable);
			
			responseBody.setEntityList(qList);
			responseBody.setResult(new Result(Status.OK, ""));
		} catch (Exception e) {
			responseBody.setResult(new Result(Status.ERROR, null,"查询失败"));
			logger.error("querySchedule 查询失败",e);
		}
		
		return responseBody;
	}
	
	
	
	
	
	

	
	
	
	
	
}



