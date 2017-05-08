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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.galaxyinternet.common.controller.BaseControllerImpl;
import com.galaxyinternet.framework.core.model.Page;
import com.galaxyinternet.framework.core.model.PageRequest;
import com.galaxyinternet.framework.core.model.ResponseData;
import com.galaxyinternet.framework.core.model.Result;
import com.galaxyinternet.framework.core.model.Result.Status;
import com.galaxyinternet.framework.core.service.BaseService;
import com.galaxyinternet.model.user.User;
import com.galaxyinternet.rili.model.ScheduleContacts;
import com.galaxyinternet.rili.service.ScheduleContactsService;


@Controller
@RequestMapping("/galaxy/scheduleContacts")
public class ScheduleContactsController  extends BaseControllerImpl<ScheduleContacts, ScheduleContacts> {

	final Logger logger = LoggerFactory.getLogger(ScheduleContactsController.class);
	
	
	@Autowired
	private ScheduleContactsService scheduleContactsService;
	
	@Override
	protected BaseService<ScheduleContacts> getBaseService() {
		return this.scheduleContactsService;
	}

	/**
	 * 保存联系人
	 * @param scheduleContacts
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/savePerson", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<ScheduleContacts> savePerson(@RequestBody ScheduleContacts scheduleContacts,
			HttpServletRequest request) {
		ResponseData<ScheduleContacts> responseBody = new ResponseData<ScheduleContacts>();
		User user = (User) getUserFromSession(request);

		if(scheduleContacts.getName()==null||scheduleContacts.getPhone1()==null){
			responseBody.setResult(new Result(Status.ERROR, null,"添加联系人失败参数缺失"));
			return responseBody;
		}
		try{
			scheduleContacts.setCreatedId(user.getId());
			Long id = scheduleContactsService.insert(scheduleContacts);	
			logger.info("添加联系人["+"联系人名称:"+scheduleContacts.getName()+"]");
			responseBody.setId(id);
		    responseBody.setResult(new Result(Status.OK, null,"添加联系人成功"));
		} catch (Exception e) {
			responseBody.setResult(new Result(Status.ERROR, null,"添加联系人失败"));
			logger.error("异常信息:",e.getMessage());
			e.printStackTrace();
		}
		return responseBody;
	}
	/*
	*//**
	 * 获取联系人详情
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/selectById/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<ScheduleContacts> selectById(@PathVariable String id) {
		ResponseData<ScheduleContacts> responseBody = new ResponseData<ScheduleContacts>();
				
		try{			
			ScheduleContacts ss = scheduleContactsService.queryById(Long.valueOf(id));
			if(ss==null){
				responseBody.setResult(new Result(Status.ERROR, null,"该联系人不存在"));
				return responseBody;
			}else{
				responseBody.setEntity(ss);				    
			    responseBody.setResult(new Result(Status.OK, null,"查询联系人详情"));
			}
		} catch (Exception e) {
			responseBody.setResult(new Result(Status.ERROR, null,"查询联系人失败"));
			logger.error("异常信息:",e.getMessage());
			e.printStackTrace();
		}
		return responseBody;
	}
	/**
	 * 删除联系人
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/deleteById/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<ScheduleContacts> delete(@PathVariable String id) {
		ResponseData<ScheduleContacts> responseBody = new ResponseData<ScheduleContacts>();		
		try{
			ScheduleContacts ss = scheduleContactsService.queryById(Long.valueOf(id));
			if(ss!=null){
				int y = scheduleContactsService.deleteById(Long.valueOf(id));
				System.out.println(y);
			    responseBody.setResult(new Result(Status.OK, null,"删除联系人成功"));
			}else{
				responseBody.setResult(new Result(Status.ERROR, null,"该联系人不存在"));
				return responseBody;
			}
		} catch (Exception e) {
			responseBody.setResult(new Result(Status.ERROR, null,"删除联系人失败"));
			logger.error("异常信息:",e.getMessage());
			e.printStackTrace();
		}
		return responseBody;
	}
	
	
	/**
	 * 更新联系人
	 */
	@ResponseBody
	@RequestMapping(value = "/updatePerson", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<ScheduleContacts> updatePerson(@RequestBody ScheduleContacts scheduleContacts,
			HttpServletRequest request) {
		ResponseData<ScheduleContacts> responseBody = new ResponseData<ScheduleContacts>();
		User user = (User) getUserFromSession(request);

		if(scheduleContacts.getId()==null){
			responseBody.setResult(new Result(Status.ERROR, null,"联系人id为null缺少必要参数"));
			return responseBody;
		}
		try{
			ScheduleContacts ss = 	scheduleContactsService.queryById(scheduleContacts.getId());
			
			if(ss!=null){
				scheduleContacts.setUpdatedId(user.getId());
				scheduleContactsService.updateById(scheduleContacts);		    
			    responseBody.setResult(new Result(Status.OK, null,"编辑联系人成功"));
			}else{
				responseBody.setResult(new Result(Status.ERROR, null,"该联系人不存在"));
				return responseBody;
			}
			
		} catch (Exception e) {
			responseBody.setResult(new Result(Status.ERROR, null,"编辑联系人失败"));
			logger.error("异常信息:",e.getMessage());
			e.printStackTrace();
		}
		return responseBody;
	}

	
	

	//分页的联系人列表查询
	@ResponseBody
	@RequestMapping(value = "/selectPersonList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<ScheduleContacts> selectPersonList(@RequestBody ScheduleContacts scheduleContacts,
			HttpServletRequest request) {
		ResponseData<ScheduleContacts> responseBody = new ResponseData<ScheduleContacts>();
		User user = (User) getUserFromSession(request);
		try{		
			scheduleContacts.setCreatedId(user.getId());
			Page<ScheduleContacts> pageProject = scheduleContactsService.queryPageList(scheduleContacts,new PageRequest(scheduleContacts.getPageNum(), scheduleContacts.getPageSize()));
			responseBody.setPageList(pageProject);
			responseBody.setResult(new Result(Status.OK, null,"查询联系人列表成功"));
			
		} catch (Exception e) {
			responseBody.setResult(new Result(Status.ERROR, null,"查询联系人列表失败"));
			logger.error("异常信息:",e.getMessage());
			e.printStackTrace();
		}
		return responseBody;
	}
	
	//不分页查询联系人列表
	@ResponseBody
	@RequestMapping(value = "/selectAppPersonList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<ScheduleContacts> selectAppPersonList(@RequestBody ScheduleContacts scheduleContacts,
			HttpServletRequest request) {
		ResponseData<ScheduleContacts> responseBody = new ResponseData<ScheduleContacts>();
		User user = (User) getUserFromSession(request);
		try{		
			scheduleContacts.setCreatedId(user.getId());
			List<ScheduleContacts> ss = scheduleContactsService.queryList(scheduleContacts);
			responseBody.setEntityList(ss);
			responseBody.setResult(new Result(Status.OK, null,"查询联系人列表成功"));
			
		} catch (Exception e) {
			responseBody.setResult(new Result(Status.ERROR, null,"查询联系人列表失败"));
			logger.error("异常信息:",e.getMessage());
			e.printStackTrace();
		}
		return responseBody;
	}
	
	
	
	
	
	
}



