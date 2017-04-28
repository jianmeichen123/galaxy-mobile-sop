package com.galaxyinternet.rili.controller;


import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.galaxyinternet.common.controller.BaseControllerImpl;
import com.galaxyinternet.framework.core.constants.Constants;
import com.galaxyinternet.framework.core.model.ResponseData;
import com.galaxyinternet.framework.core.model.Result;
import com.galaxyinternet.framework.core.model.Result.Status;
import com.galaxyinternet.framework.core.service.BaseService;
import com.galaxyinternet.model.user.User;
import com.galaxyinternet.rili.model.ScheduleDepartUno;
import com.galaxyinternet.rili.model.ScheduleShared;
import com.galaxyinternet.rili.service.ScheduleSharedService;
import com.galaxyinternet.rili.util.DeptNoUsers;


@Controller
@RequestMapping("/galaxy/schedule/share")
public class ScheduleSharedController  extends BaseControllerImpl<ScheduleShared, ScheduleShared> {

	final Logger logger = LoggerFactory.getLogger(ScheduleSharedController.class);
	
	@Autowired
	private ScheduleSharedService scheduleSharedService;
	
	
	@Override
	protected BaseService<ScheduleShared> getBaseService() {
		return this.scheduleSharedService;
	}
	
	

	
	/**
	 * 选择查看成员，成员包括：自己
	 * 共享给自己的成员（默认为自己）
	 * @return {[createUid:111, createUname:"TEST"]}
	*/
	@ResponseBody
	@RequestMapping(value = "/querySharedUsers", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<ScheduleShared> querySharedUsers(HttpServletRequest request) {
		
		ResponseData<ScheduleShared> responseBody = new ResponseData<ScheduleShared>();
		try{
			Object objUser = request.getSession().getAttribute(Constants.SESSION_USER_KEY);
			
			List<ScheduleShared> cusers = scheduleSharedService.querySharedUsers(objUser);
			
			responseBody.setEntityList(cusers);
			responseBody.setResult(new Result(Status.OK, ""));
		} catch (Exception e) {
			responseBody.setResult(new Result(Status.ERROR, null, "查询失败"));
			logger.error("querySharedUsers 异常 ");
		}
		
		return responseBody;
	}
	

	
	/**
	 * 自己共享的 共享人列表查询
	 * 共享人列表
	 * @param  toUname 查询
	 * @return {[id:1, toUid:111, toUname:"TEST",toDeptName:"DNAME"]}    //有问题
	*/
	@ResponseBody
	@RequestMapping(value = "/queryMySharedUsers", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<ScheduleShared> queryMySharedUsers(HttpServletRequest request,@RequestBody ScheduleShared scheduleShared ) {
		
		
	//	@RequestParam(value="toUname", required=false)String toUname 
		
		
		ResponseData<ScheduleShared> responseBody = new ResponseData<ScheduleShared>();
		try{
			Object objUser = request.getSession().getAttribute(Constants.SESSION_USER_KEY);
			
			List<ScheduleShared> cusers = scheduleSharedService.queryMySharedUsers(objUser,scheduleShared.getToUname());
			
			responseBody.setEntityList(cusers);
			responseBody.setResult(new Result(Status.OK, ""));
		} catch (Exception e) {
			responseBody.setResult(new Result(Status.ERROR, null, "查询失败"));
			logger.error("queryMySharedUsers 异常 ");
		}
		
		return responseBody;
	}
	
	
	
	
	/**
	 * 添加共享人
	  @param  {deptNoUsers : [{deptId:111,userCount:10,userIds:[222,333]},{deptId:111, userCount:10,userIds:[222,333]}]}
	 */
	@ResponseBody
	@RequestMapping(value = "/saveSharedUsers", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<ScheduleShared> saveSharedUsers(HttpServletRequest request, @RequestBody ScheduleShared query) {
		
		ResponseData<ScheduleShared> responseBody = new ResponseData<ScheduleShared>();
		try{
			
			Object objUser = request.getSession().getAttribute(Constants.SESSION_USER_KEY);
			User user = (User)objUser;
			
			//传入的需要保存的 
			List<DeptNoUsers> deptNoUsers = query.getDeptNoUsers();
			/*if(deptNoUsers!=null && deptNoUsers.size()>0){
				for(DeptNoUsers dUsers:deptNoUsers){
					if(dUsers.getUserIds()==null){
						ScheduleShared scheduleShared = new ScheduleShared();
						scheduleShared.setDepartmentId(dUsers.getDeptId());
						scheduleShared.setCreateUid(user.getId());
						List<Long> ss= scheduleSharedService.selectByUserId(scheduleShared);
						dUsers.setUserIds(ss);
					}
				}
			
			}*/
			//删除所有共享人
			ScheduleShared comShareQ = new ScheduleShared();
			comShareQ.setCreateUid(user.getId());
			
			
		/*	//删除所有部门共享人数量
			ScheduleDepartUno dun = new ScheduleDepartUno();
			dun.setRemarkType((byte) 1);
			dun.setCreatedId(user.getId());
			*/
			
			
			
			
		//	scheduleSharedService.saveSharedUsers(objUser,comShareQ, dun, deptNoUsers);
			scheduleSharedService.saveSharedUsers(objUser,comShareQ, deptNoUsers);
			responseBody.setResult(new Result(Status.OK, null, "添加成功"));
		} catch (Exception e) {
			responseBody.setResult(new Result(Status.ERROR, null, "添加失败"));
			logger.error("saveSharedUsers 异常 ");
		}
		
		return responseBody;
	}
	
	
	
	
	/**
	 * 删除共享人
	 * @param  共享id
	 */
	@ResponseBody
	@RequestMapping(value = "/delSharedUser/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<ScheduleShared> delSharedUser(HttpServletRequest request, @PathVariable Long id) {
		
		ResponseData<ScheduleShared> responseBody = new ResponseData<ScheduleShared>();
		try{
			//User user = (User)request.getSession().getAttribute(Constants.SESSION_USER_KEY);
			
			//部门共享人数量 - 1
		//	ScheduleDepartUno queryDun = new ScheduleDepartUno();
			/*		queryDun.setRemarkType((byte) 1);
			queryDun.setDepartmentId(user.getDepartmentId());
			queryDun.setCreatedId(user.getId());*/
			scheduleSharedService.deleteById(id);
			//scheduleSharedService.delSharedUser(id, queryDun);
			
			responseBody.setResult(new Result(Status.OK, null, "删除成功"));
		} catch (Exception e) {
			responseBody.setResult(new Result(Status.ERROR, null, "删除失败"));
			logger.error("delSharedUser 异常 ");
		}
		
		return responseBody;
	}
	
	


	
	
	
	
}
