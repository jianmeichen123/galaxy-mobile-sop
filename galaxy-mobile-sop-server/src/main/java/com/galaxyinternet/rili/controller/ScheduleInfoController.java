package com.galaxyinternet.rili.controller;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import com.galaxyinternet.framework.core.constants.Constants;
import com.galaxyinternet.framework.core.model.ResponseData;
import com.galaxyinternet.framework.core.model.Result;
import com.galaxyinternet.framework.core.model.Result.Status;
import com.galaxyinternet.framework.core.service.BaseService;
import com.galaxyinternet.framework.core.utils.DateUtil;
import com.galaxyinternet.model.project.Project;
import com.galaxyinternet.model.user.User;
import com.galaxyinternet.rili.model.ScheduleContacts;
import com.galaxyinternet.rili.model.ScheduleDict;
import com.galaxyinternet.rili.model.ScheduleInfo;
import com.galaxyinternet.rili.model.ScheduleMettingUsers;
import com.galaxyinternet.rili.model.SchedulePersonPlan;
import com.galaxyinternet.rili.service.ScheduleDictService;
import com.galaxyinternet.rili.service.ScheduleInfoService;
import com.galaxyinternet.rili.service.ScheduleMessageService;
import com.galaxyinternet.rili.service.ScheduleMettingUsersService;
import com.galaxyinternet.rili.service.SchedulePersonPlanService;
import com.galaxyinternet.rili.util.AccountDate;
import com.galaxyinternet.rili.util.Receipt;
import com.galaxyinternet.rili.util.ScheduleUtil;
import com.galaxyinternet.service.ProjectService;


@Controller
@RequestMapping("/galaxy/scheduleInfo")
public class ScheduleInfoController  extends BaseControllerImpl<ScheduleInfo, ScheduleInfo> {

	final Logger logger = LoggerFactory.getLogger(ScheduleInfoController.class);

	@Autowired
	private ProjectService projectService;
	@Autowired
	private ScheduleInfoService scheduleInfoService;
	@Autowired
	private ScheduleDictService scheduleDictService;
	@Autowired
	private ScheduleMettingUsersService scheduleMettingUsersService;
	@Autowired
	private SchedulePersonPlanService  schedulePersonPlanService;	
	@Autowired
	private ScheduleMessageService scheduleMessageService;
	@Override
	protected BaseService<ScheduleInfo> getBaseService() {
		return this.scheduleInfoService;
	}

	
	
	
	
	
	//TODO: 日程 日历 列表查询
	
	
	/**
	 * 日程条件查询
	 * 按条件  日、周、月      
	 * @param query : {year:2014, month:12, day:12, createdId:111111}
	 *                day != null ： 按日查
	 *                month != null ： 按月查
	 *                year != null ： 按年查
	 *                createdId = null : 查询本人
	*/
	@ResponseBody
	@RequestMapping(value = "/querySchedule", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<ScheduleUtil> querySchedule(HttpServletRequest request,@RequestBody ScheduleInfo query ) {
		
		ResponseData<ScheduleUtil> responseBody = new ResponseData<ScheduleUtil>();
		
		try {
			User user = (User) request.getSession().getAttribute(Constants.SESSION_USER_KEY);
			
			
			/*PageRequest pageable = new PageRequest();
			Integer pageNum = query.getPageNum() != null ? query.getPageNum() : 0;
			Integer pageSize = query.getPageSize() != null ? query.getPageSize() : 10;
			if(StringUtils.isNotEmpty(query.getProperty())){
				Direction direction = Direction.ASC;
				if(StringUtils.isNotEmpty(query.getDirection())){
					direction = Direction.fromString(query.getDirection());
				}
				pageable = new PageRequest(pageNum,pageSize, new Sort(direction,query.getProperty()));
			}
			else{
				pageable = new PageRequest(pageNum,pageSize);
			}*/
			if(query.getYear()!=null && query.getMonth()!=null){
				String lastMouthDay = AccountDate.getLastDayOfMonth(query.getYear(), query.getMonth()-1);
				query.setLastMouthDay(lastMouthDay);
			}
			if(query.getProperty()==null)  query.setProperty("start_time"); 
			if(query.getDirection()==null) query.setDirection("asc");
			if(query.getCreatedId()==null) query.setCreatedId(user.getId());
			
			Map<String,String> qtime = DateUtil.getBeginEndTimeStr(query.getYear(),query.getMonth(),query.getDay());
			if(qtime != null){
				query.setBqStartTime(qtime.get("beginTimeStr"));
				query.setBqEndTime(qtime.get("endTimeStr"));
			}
			
			//结果查询  封装
			List<ScheduleUtil> qList = scheduleInfoService.queryAndConvertList(query);
			
			responseBody.setEntityList(qList);
			responseBody.setResult(new Result(Status.OK, ""));
		} catch (Exception e) {
			responseBody.setResult(new Result(Status.ERROR, null,"查询失败"));
			logger.error("querySchedule 查询失败",e);
		}
		
		return responseBody;
	}
	
	
	
	
	
	
	
	
	/**
	 * 其他日程的添加
	 * @param ScheduleInfo
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/saveOtherSchedule", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<ScheduleInfo> saveOtherSchedule(@RequestBody ScheduleInfo scheduleInfo,
			HttpServletRequest request) {
		ResponseData<ScheduleInfo> responseBody = new ResponseData<ScheduleInfo>();
		User user = (User) getUserFromSession(request);

/*		if(scheduleInfo.getIsAllday()==1){
			
		}
		*/
		try {
			//标识是 其他日程
			scheduleInfo.setType((byte) 3);
			scheduleInfo.setCreatedId(user.getId());
			
			Long id = scheduleInfoService.insert(scheduleInfo);
			scheduleInfo.setMessageType("common_schedule");
			scheduleInfo.setId(id);
			
			responseBody.setId(id);			
			responseBody.setResult(new Result(Status.OK, null,"添加日程成功"));
			scheduleMessageService.saveMessageByInfo(scheduleInfo);
		} catch (Exception e) {
			e.printStackTrace();
			responseBody.setResult(new Result(Status.ERROR, null,"添加日程失败"));
			logger.error("异常信息:",e.getMessage());
		}
		return responseBody;
	}
	
	/**
	 * 删除其他日程
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/deleteOtherScheduleById/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<ScheduleInfo> delete(@PathVariable String id) {
		ResponseData<ScheduleInfo> responseBody = new ResponseData<ScheduleInfo>();		
		try{
			ScheduleInfo ss = scheduleInfoService.queryById(Long.valueOf(id));
			if(ss!=null){
				int y = scheduleInfoService.deleteById(Long.valueOf(id));
				responseBody.setResult(new Result(Status.OK, null,"删除其他日程成功"));
				System.out.println(y);
			}else{
				responseBody.setResult(new Result(Status.ERROR, null,"此其他日程不存在"));
				return responseBody;
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseBody.setResult(new Result(Status.ERROR, null,"删除其他日程失败"));
			logger.error("异常信息:",e.getMessage());
		}
		return responseBody;
	}
	
	/**
	 * 查询其他日程详情
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/selectOtherScheduleById/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<ScheduleInfo> selectOtherScheduleById(@PathVariable String id) {
		ResponseData<ScheduleInfo> responseBody = new ResponseData<ScheduleInfo>();		
		try{
			
			ScheduleInfo ss = scheduleInfoService.queryById(Long.valueOf(id));
			if(ss==null){
				responseBody.setResult(new Result(Status.ERROR, null,"其他日程不存在"));
				return responseBody;
			}
			if(ss.getStartTime()!=null){
				
				String starttime = dateStrformat(ss.getStartTime());
				ss.setStartTime(starttime);
			}
			
			if(ss.getEndTime()!=null){
				
				String starttime = dateStrformat(ss.getEndTime());
				ss.setEndTime(starttime);
			}
			if (ss.getWakeupId() != null) {

				ScheduleDict scheduleDict = scheduleDictService.queryById(ss.getWakeupId());

				ss.setRemind(scheduleDict.getName());

			}

			responseBody.setEntity(ss);
			responseBody.setResult(new Result(Status.OK, null,"查询其他日程成功"));
		} catch (Exception e) {
			e.printStackTrace();
			responseBody.setResult(new Result(Status.ERROR, null,"查询其他日程失败"));
			logger.error("异常信息:",e.getMessage());
		}
		return responseBody;
	}
	

	/**
	 * 更新其他日程
	 * @param scheduleInfo
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/updateOtherSchedule", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<ScheduleInfo> updateOtherSchedule(@RequestBody ScheduleInfo scheduleInfo,
			HttpServletRequest request) {
		ResponseData<ScheduleInfo> responseBody = new ResponseData<ScheduleInfo>();
		User user = (User) getUserFromSession(request);

		if(scheduleInfo.getId()==null){
			responseBody.setResult(new Result(Status.ERROR, null,"必要参数缺失"));
			return responseBody;
		}
		try {
			ScheduleInfo ss =scheduleInfoService.queryById(scheduleInfo.getId());
			if(ss!=null){
				scheduleInfo.setUpdatedId(user.getId());
				int y= scheduleInfoService.updateById(scheduleInfo);
				responseBody.setResult(new Result(Status.OK, null,"修改其他日程成功"));
				
			}else{				
				responseBody.setResult(new Result(Status.ERROR, null,"其他日程不存在"));
				return responseBody;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			responseBody.setResult(new Result(Status.ERROR, null,"修改其他日程失败"));
			logger.error("异常信息:",e.getMessage());
		}
		return responseBody;
	}
	
	
	/**
	 * 查询字典表
	 * @param ScheduleDict
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/selectScheduleDict", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<ScheduleDict> selectScheduleDict(@RequestBody ScheduleDict scheduleDict,
			HttpServletRequest request) {
		ResponseData<ScheduleDict> responseBody = new ResponseData<ScheduleDict>();

		if(scheduleDict.getDirection()==null){
			scheduleDict.setDirection("asc");
		}
		if(scheduleDict.getProperty()==null){
			scheduleDict.setProperty("index_num");
		}
		try {
			List<ScheduleDict> list = scheduleDictService.queryList(scheduleDict);
			responseBody.setEntityList(list);			
			responseBody.setResult(new Result(Status.OK, null,"查询字典表成功"));
			
		} catch (Exception e) {
			e.printStackTrace();
			responseBody.setResult(new Result(Status.ERROR, null,"查询字典表失败"));
			logger.error("异常信息:",e.getMessage());
		}
		return responseBody;
	}

	/**
	 * 转化日期
	 * @param dateStr
	 * @return
	 */
	public static String dateStrformat(String dateStr){  //2016-05-27 16:00:00   19
		
		if( dateStr.indexOf("-") != -1){
			dateStr = dateStr.replaceAll("-", "/");
		}
		String dateStrs = dateStr.substring(0,16);
		
		return dateStrs;
	}
	
	
	/**
	 * 日程新建会议的添加
	 * @param ScheduleInfo
	 * @param request
	 * @return
	 */
	/*@ResponseBody
	@RequestMapping(value = "/saveMeetSchedule", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<ScheduleInfo> saveMeetSchedule(@RequestBody ScheduleInfo scheduleInfo,
			HttpServletRequest request) {
		ResponseData<ScheduleInfo> responseBody = new ResponseData<ScheduleInfo>();
		User user = (User) getUserFromSession(request);
		Object objUser = request.getSession().getAttribute(Constants.SESSION_USER_KEY);
		if(scheduleInfo.getIsAllday()==1){
			
		}
		
		try {
			//标识是会议
			scheduleInfo.setType((byte) 1);
			scheduleInfo.setCreatedId(user.getId());
			
			Long id = scheduleInfoService.saveMeetSchedule(objUser,scheduleInfo);
			
			responseBody.setId(id);			
			responseBody.setResult(new Result(Status.OK, null,"添加会议成功"));
			
		} catch (Exception e) {
			e.printStackTrace();
			responseBody.setResult(new Result(Status.ERROR, null,"添加会议失败"));
			logger.error("异常信息:",e.getMessage());
		}
		return responseBody;
	}
	*/
	
	

	/**
	 * 日程新建拜访的添加
	 * @param ScheduleInfo
	 * @param request
	 * @return
	 */
	/*@ResponseBody
	@RequestMapping(value = "/savePlanSchedule", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<ScheduleInfo> savePlanSchedule(@RequestBody ScheduleInfo scheduleInfo,
			HttpServletRequest request) {
		ResponseData<ScheduleInfo> responseBody = new ResponseData<ScheduleInfo>();
		User user = (User) getUserFromSession(request);

		if(scheduleInfo.getIsAllday()==1){
			
		}
		
		try {
			//标识是拜访日程
			scheduleInfo.setType((byte) 2);
			scheduleInfo.setCreatedId(user.getId());
			
			Long id = scheduleInfoService.savePlanSchedule(user,scheduleInfo);
			
			responseBody.setId(id);			
			responseBody.setResult(new Result(Status.OK, null,"添加会议成功"));
			
		} catch (Exception e) {
			e.printStackTrace();
			responseBody.setResult(new Result(Status.ERROR, null,"添加会议失败"));
			logger.error("异常信息:",e.getMessage());
		}
		return responseBody;
	}
	
	
	*//**
	 * 日程会议的详情    可能有问题 判断
	 * @param ScheduleInfo
	 * @param request
	 * @return
	 *//*
	
	@ResponseBody
	@RequestMapping(value = "/selectMeetScheduleById/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<ScheduleInfo> selectMeetScheduleById(@PathVariable String id) {
		ResponseData<ScheduleInfo> responseBody = new ResponseData<ScheduleInfo>();		
		
		try{
			
			ScheduleInfo ss = scheduleInfoService.queryById(Long.valueOf(id));

			if (ss != null) {
				String starttime = dateStrformat(ss.getStartTime());
				ss.setStartTime(starttime);

				String endtime = dateStrformat(ss.getEndTime());
				ss.setEndTime(endtime);

				if (ss.getProjectId() != null) {
					Project p = projectService.queryById(ss.getProjectId());
					if (p != null && p.getProjectName() != null) {

						ss.setProjectName(p.getProjectName());
					}
				}
				if (ss.getWakeupId() != null) {

					ScheduleDict scheduleDict = scheduleDictService.queryById(ss.getWakeupId());

					ss.setRemind(scheduleDict.getName());

				}
			}
			ScheduleMettingUsers scheduleMettingUsers = new ScheduleMettingUsers();

			scheduleMettingUsers.setScheduleId(Long.valueOf(id));

			List<ScheduleMettingUsers> lis = scheduleMettingUsersService.queryList(scheduleMettingUsers);

			ss.setScheduleMettingUsersList(lis);

			responseBody.setEntity(ss);
			responseBody.setResult(new Result(Status.OK, null, "查询其他日程成功"));
		} catch (Exception e) {
			responseBody.setResult(new Result(Status.ERROR, null, "查询其他日程失败"));
			logger.error("异常信息:", e.getMessage());
		}
		return responseBody;
	}
	
	*//**
	 * 查询 会议回执信息
	 *//*

	@ResponseBody
	@RequestMapping(value = "/selectMeetReceiptById/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<Receipt> selectMeetReceiptById(@PathVariable String id) {
		ResponseData<Receipt> responseBody = new ResponseData<Receipt>();		
		
		try{
			ScheduleMettingUsers scheduleMettingUsers = new ScheduleMettingUsers();

			scheduleMettingUsers.setScheduleId(Long.valueOf(id));

			List<ScheduleMettingUsers> lis = scheduleMettingUsersService.queryList(scheduleMettingUsers);


			
			List<ScheduleMettingUsers> lis1 = new ArrayList<ScheduleMettingUsers>();
			List<ScheduleMettingUsers> lis2 = new ArrayList<ScheduleMettingUsers>();
			List<ScheduleMettingUsers> lis3 = new ArrayList<ScheduleMettingUsers>();
			
			List<Receipt> rs = new ArrayList<Receipt>();
			
			for(ScheduleMettingUsers scheduleUsers:lis){
				
				if(scheduleUsers.getMessageState().intValue()==0){
					lis1.add(scheduleUsers);					
				}else if(scheduleUsers.getMessageState().intValue()==1){					
					lis2.add(scheduleUsers);
				}else{
					lis3.add(scheduleUsers);
				}

			}
			
			int s1 = lis1.size();
			int s2 = lis2.size();
			int s3 = lis3.size();
			
			Receipt receipt1 = new Receipt();
			Receipt receipt2 = new Receipt();				
			Receipt receipt3 = new Receipt();
			
			receipt1.setCode(0);
			receipt1.setName("未回复");
			receipt1.setReceiptCount(s1);
			receipt1.setSchlist(lis1);
			
			receipt2.setCode(1);
			receipt2.setName("已接受");
			receipt2.setReceiptCount(s2);
			receipt2.setSchlist(lis2);

			receipt3.setCode(2);
			receipt3.setName("已拒绝");
			receipt3.setReceiptCount(s3);
			receipt3.setSchlist(lis3);
			
			rs.add(receipt1);
			rs.add(receipt2);
			rs.add(receipt3);

			responseBody.setEntityList(rs);
			responseBody.setResult(new Result(Status.OK, null, "查询回执列表成功"));
		} catch (Exception e) {
			responseBody.setResult(new Result(Status.ERROR, null, "查询回执列表失败"));
			logger.error("异常信息:", e.getMessage());
		}
		return responseBody;
	}
	
	*//**
	 * 拜访日程详情
	 *//*
	@ResponseBody
	@RequestMapping(value = "/selectPlanScheduleById/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<ScheduleInfo> selectPlanScheduleById(@PathVariable String id) {
		ResponseData<ScheduleInfo> responseBody = new ResponseData<ScheduleInfo>();		
		
		try{
			
			ScheduleInfo ss = scheduleInfoService.queryById(Long.valueOf(id));

			if (ss != null) {
				String starttime = dateStrformat(ss.getStartTime());
				ss.setStartTime(starttime);

				String endtime = dateStrformat(ss.getEndTime());
				ss.setEndTime(endtime);

				if (ss.getProjectId() != null) {
					Project p = projectService.queryById(ss.getProjectId());
					if (p != null && p.getProjectName() != null) {

						ss.setProjectName(p.getProjectName());
					}
				}
				if (ss.getWakeupId() != null) {

					ScheduleDict scheduleDict = scheduleDictService.queryById(ss.getWakeupId());

					ss.setRemind(scheduleDict.getName());

				}				
				SchedulePersonPlan schedulePersonPlan = new SchedulePersonPlan();

				schedulePersonPlan.setScheduleId(Long.valueOf(id));

				List<ScheduleContacts> lis = schedulePersonPlanService.queryByCountsId(schedulePersonPlan);								
				ss.setScheduleContactsList(lis);				
			}else{				
				responseBody.setResult(new Result(Status.ERROR, null, "查询其他日程失败,日程不存在"));				
				return responseBody;				
				
			}			
			responseBody.setEntity(ss);
			responseBody.setResult(new Result(Status.OK, null, "查询其他日程成功"));
		} catch (Exception e) {
			e.printStackTrace();
			responseBody.setResult(new Result(Status.ERROR, null, "查询其他日程失败"));
			logger.error("异常信息:", e.getMessage());
		}
		return responseBody;
	}
	
	*//**
	 * 更新拜访日程
	 * @param scheduleInfo
	 * @param request
	 * @return
	 *//*
	@ResponseBody
	@RequestMapping(value = "/updatePlanSchedule", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<ScheduleInfo> updatePlanSchedule(@RequestBody ScheduleInfo scheduleInfo,
			HttpServletRequest request) {
		ResponseData<ScheduleInfo> responseBody = new ResponseData<ScheduleInfo>();
		User user = (User) getUserFromSession(request);
		Object objUser = request.getSession().getAttribute(Constants.SESSION_USER_KEY);
		if(scheduleInfo.getId()==null){
			responseBody.setResult(new Result(Status.ERROR, null,"必要参数缺失"));
			return responseBody;
		}
		try {
			ScheduleInfo ss =scheduleInfoService.queryById(scheduleInfo.getId());
			if(ss!=null){
				scheduleInfo.setUpdatedId(user.getId());
				scheduleInfoService.updatePlanSchedule(objUser,scheduleInfo);
				responseBody.setResult(new Result(Status.OK, null,"修改拜访日程成功"));
				
			}else{				
				responseBody.setResult(new Result(Status.ERROR, null,"拜访日程不存在"));
				return responseBody;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			responseBody.setResult(new Result(Status.ERROR, null,"修改拜访日程失败"));
			logger.error("异常信息:",e.getMessage());
		}
		return responseBody;
	}
	
	*//**
	 * 删除拜访日程
	 * @param id
	 * @return
	 *//*
	@ResponseBody
	@RequestMapping(value = "/deletePlanScheduleById/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<ScheduleInfo> deletePlanScheduleById(@PathVariable String id) {
		ResponseData<ScheduleInfo> responseBody = new ResponseData<ScheduleInfo>();		
		try{
			ScheduleInfo ss = scheduleInfoService.queryById(Long.valueOf(id));
			if(ss!=null){
				scheduleInfoService.delePlanSchedule(ss);
				responseBody.setResult(new Result(Status.OK, null,"删除拜访日程成功"));
			}else{
				responseBody.setResult(new Result(Status.ERROR, null,"此拜访日程不存在"));
				return responseBody;
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseBody.setResult(new Result(Status.ERROR, null,"删除拜访日程失败"));
			logger.error("异常信息:",e.getMessage());
		}
		return responseBody;
	}*/
	
	/**
	 * 获取验证时间
	 * @param scheduleInfo
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/ctSchedule", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<ScheduleInfo> ctSchedule(@RequestBody ScheduleInfo scheduleInfo,
			HttpServletRequest request) {
		ResponseData<ScheduleInfo> responseBody = new ResponseData<ScheduleInfo>();
		
		try {
			
			String ss = scheduleInfoService.getCconflictSchedule(scheduleInfo);	
			if(ss==null){
				responseBody.setResult(new Result(Status.OK, null,""));
			}else{
				responseBody.setResult(new Result(Status.OK, null,ss));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			responseBody.setResult(new Result(Status.ERROR, null,"查询日程冲突失败"));
			logger.error("异常信息:",e.getMessage());
		}
		return responseBody;
	}
	
}



