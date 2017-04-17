package com.galaxyinternet.rili.controller;


import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.galaxyinternet.common.controller.BaseControllerImpl;
import com.galaxyinternet.framework.core.constants.Constants;
import com.galaxyinternet.framework.core.model.ResponseData;
import com.galaxyinternet.framework.core.model.Result;
import com.galaxyinternet.framework.core.model.Result.Status;
import com.galaxyinternet.framework.core.service.BaseService;
import com.galaxyinternet.rili.model.ScheduleDepartUno;
import com.galaxyinternet.rili.service.ScheduleDepartUnoService;


@Controller
@RequestMapping("/galaxy/schedule/deptUno")
public class ScheduleDepartUnoController  extends BaseControllerImpl<ScheduleDepartUno, ScheduleDepartUno> {

	final Logger logger = LoggerFactory.getLogger(ScheduleDepartUnoController.class);
	
	@Autowired
	private ScheduleDepartUnoService scheduleDepartUnoService;
	
	
	@Override
	protected BaseService<ScheduleDepartUno> getBaseService() {
		return this.scheduleDepartUnoService;
	}
	
	
	
	    
	/**
	 * 查询所有部门信息 id-name
	 * 封装  默认部门      人员  和   已经选择的人员
	 * 封装  部门 - 部门用户数量
	 * @param query : {remarkType:0/1, remarkId:schedule_id}  
	 * 				   remarkType 记录类型 0:日程（会议）  1:共享,不要remarkId
	 * @return {[id:1, toUid:111, toUname:"TEST",toDeptName:"DNAME"]}
	*/
	@ResponseBody
	@RequestMapping(value = "/queryDeptUinfo", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<ScheduleDepartUno> queryDeptUinfo(HttpServletRequest request,@RequestBody ScheduleDepartUno query ) {
		
		ResponseData<ScheduleDepartUno> responseBody = new ResponseData<ScheduleDepartUno>();
		
		try{
			Object objUser = request.getSession().getAttribute(Constants.SESSION_USER_KEY);
			//User user = (User) objUser;
			
			Boolean iserror = false;
			Byte remarkType = query.getRemarkType();
			Long remarkId = query.getRemarkId();
			if(remarkType == null){
				iserror = true;
			}else if(remarkType.intValue() == 0 && (remarkId == null||remarkId.intValue() == 0)){
				iserror = true;
			}
			if(iserror){
				responseBody.setResult(new Result(Status.ERROR,null, "请完善信息"));
				return responseBody;
			}
			
			List<ScheduleDepartUno> resultList = scheduleDepartUnoService.queryDeptUinfo(objUser,query);
			
			responseBody.setEntityList(resultList);
			responseBody.setResult(new Result(Status.OK, ""));
		} catch (Exception e) {
			responseBody.setResult(new Result(Status.ERROR, null, "查询失败"));
			logger.error("queryDeptUinfo 异常 ");
		}
		
		return responseBody;
	}

	
	
	
	
	
	
	/**
	 * 查询所选部门下   人员  和   已经选择的人员
	 * 封装  部门 - 部门用户数量
	 * @param query : {remarkType:0/1, remarkId:schedule_id,departmentId:1212}  
	 * 				   remarkType 记录类型 0:日程（会议）  1:共享,不要remarkId
	 * @return {[id:1, toUid:111, toUname:"TEST",toDeptName:"DNAME"]}
	*/
	@ResponseBody
	@RequestMapping(value = "/queryDeptUinfoByDid", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<ScheduleDepartUno> queryDeptUinfoByDid(HttpServletRequest request,@RequestBody ScheduleDepartUno query ) {
		
		ResponseData<ScheduleDepartUno> responseBody = new ResponseData<ScheduleDepartUno>();
		
		try{
			
			Object objUser = request.getSession().getAttribute(Constants.SESSION_USER_KEY);
			//User user = (User) objUser;
			
			Boolean iserror = false;
			Byte remarkType = query.getRemarkType();
			Long remarkId = query.getRemarkId();
			Long checkDdptId = query.getDepartmentId();
			
			if(remarkType == null || checkDdptId == null){
				iserror = true;
			}else if(remarkType.intValue() == 0 && (remarkId == null||remarkId.intValue() == 0)){
				iserror = true;
			}
			if(iserror){
				responseBody.setResult(new Result(Status.ERROR,null, "请完善信息"));
				return responseBody;
			}
			
			ScheduleDepartUno result = scheduleDepartUnoService.queryDeptUinfoByDid(objUser,query);
			
			responseBody.setEntity(result);
			responseBody.setResult(new Result(Status.OK, ""));
		} catch (Exception e) {
			responseBody.setResult(new Result(Status.ERROR, null, "查询失败"));
			logger.error("queryDeptUinfo 异常 ");
		}
		
		return responseBody;
	}
	
	
	
	
	
}
