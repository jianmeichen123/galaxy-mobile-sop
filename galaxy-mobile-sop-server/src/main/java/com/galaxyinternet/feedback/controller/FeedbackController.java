package com.galaxyinternet.feedback.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.galaxyinternet.framework.core.model.ResponseData;
import com.galaxyinternet.framework.core.model.Result;
import com.galaxyinternet.framework.core.model.Result.Status;
import com.galaxyinternet.model.feedback.Feedback;
import com.galaxyinternet.service.FeedbackService;

@Controller
@RequestMapping("/galaxy/feedback")
public class FeedbackController {
	
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
}
