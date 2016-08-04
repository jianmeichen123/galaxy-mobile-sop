package com.galaxyinternet.feedback.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.galaxyinternet.bo.OperationMessageBo;
import com.galaxyinternet.exception.PlatformException;
import com.galaxyinternet.framework.core.model.Page;
import com.galaxyinternet.framework.core.model.PageRequest;
import com.galaxyinternet.framework.core.model.ResponseData;
import com.galaxyinternet.framework.core.model.Result;
import com.galaxyinternet.framework.core.model.Result.Status;
import com.galaxyinternet.model.feedback.Feedback;
import com.galaxyinternet.model.operationMessage.OperationMessage;
import com.galaxyinternet.model.user.User;
import com.galaxyinternet.operationMessage.controller.OperationMessageController;
import com.galaxyinternet.service.FeedbackService;

@Controller
@RequestMapping("/galaxy/feedback")
public class FeedbackController {
	final Logger logger = LoggerFactory.getLogger(FeedbackController.class);
	@Autowired
	private FeedbackService feedbackService;
	
	@ResponseBody
	@RequestMapping(value = "/insertFeedback", method = RequestMethod.POST,  produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<Feedback> insertFeedback(@RequestBody Feedback feedback,BindingResult result){
		ResponseData<Feedback> responseBody = new ResponseData<Feedback>();
		if(null == feedback ||  null == feedback.getCreatedUid()|| StringUtils.isEmpty(feedback.getContent())){
			responseBody.setResult(new Result(Status.ERROR,"MISS","缺少重要参数或参数长度超出范围"));
			return responseBody;
		}
		if(feedback.getContent().length()>200){
			responseBody.setResult(new Result(Status.ERROR,"INVLID_PARAMETER","参数长度超出范围"));
			return responseBody;
		}
		
		try {
			  SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
			  Long time = System.currentTimeMillis();
			  String d = format.format(time);  
			  Date date=format.parse(d); 
			  feedback.setCreatedDate(date);
			  feedbackService.insertFeedback(feedback);
			  responseBody.setResult(new Result(Status.OK,"SUCCESS","添加反馈成功！"));
		} catch (Exception e) {
			
			responseBody.setResult(new Result(Status.ERROR,"FAIL","服务器异常，添加反馈失败！"));
		}
		return responseBody;
	}
	
	/**
	 * 用于appweb查看 跳转到意见反馈页
	 * @return
	 */
	@RequestMapping(value="/tofeedbackList",method = RequestMethod.GET)
	public String toFileList(){
		return "feedback/feedbackList";
	}
	
	@ResponseBody
	@RequestMapping(value = "/queryFeedbackList", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<Feedback> queryUserList(HttpServletRequest request,@RequestBody Feedback feedback) {
		ResponseData<Feedback> responseBody = new ResponseData<Feedback>();
		Result result = new Result();
		try {
			
			Page<Feedback> feedbackpage = feedbackService.queryPageList(feedback,new PageRequest(feedback.getPageNum(), feedback.getPageSize()));
			responseBody.setPageList(feedbackpage);
			responseBody.setResult(new Result(Status.OK, ""));
			return responseBody;	
		} catch (PlatformException e) {
			result.addError(e.getMessage(), e.getCode()+"");
			logger.error("queryFeedbackList ", e);
		}
		return responseBody;
	}
}
