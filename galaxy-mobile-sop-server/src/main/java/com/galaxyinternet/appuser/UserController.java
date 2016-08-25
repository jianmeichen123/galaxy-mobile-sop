package com.galaxyinternet.appuser;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.galaxyinternet.bo.UserBo;
import com.galaxyinternet.common.controller.BaseControllerImpl;
import com.galaxyinternet.exception.PlatformException;
import com.galaxyinternet.framework.core.model.Page;
import com.galaxyinternet.framework.core.model.PageRequest;
import com.galaxyinternet.framework.core.model.ResponseData;
import com.galaxyinternet.framework.core.model.Result;
import com.galaxyinternet.framework.core.model.Result.Status;
import com.galaxyinternet.framework.core.service.BaseService;
import com.galaxyinternet.model.logonHis.UserLogonHis;
import com.galaxyinternet.model.user.User;
import com.galaxyinternet.model.user.appUserList;
import com.galaxyinternet.service.AppUserListService;
import com.galaxyinternet.service.UserLogonHisService;
import com.galaxyinternet.service.UserService;

/**
 * 用户相关
 * 
 * @author zhaoying
 *
 */
@Controller
@RequestMapping("/galaxy/user")
public class UserController extends BaseControllerImpl<User, UserBo> {
	final Logger logger = LoggerFactory.getLogger(UserController.class);
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserLogonHisService userLogonHisService;
	@Autowired
	private AppUserListService appUserListService;

	@Override
	protected BaseService<User> getBaseService() {
		return this.userService;
	}


	/**
	 * 默认页面
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String list(HttpServletRequest request,HttpServletResponse response) {
		
		return"system/user/user_list";
	}

	@RequestMapping(value = "/userdeng",method = RequestMethod.GET)
	public String tolist() {
		
		return"user/user_wer";
	}

	//TODO
	/**
	 * app端获取登录人的列表
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/selectAllapp", method = RequestMethod.POST)
	public ResponseData<UserLogonHis> selectAllapp(HttpServletRequest request) {
		
		
		ResponseData<UserLogonHis> responseBody = new ResponseData<UserLogonHis>();
		
		List<UserLogonHis> sl = userLogonHisService.selectBiao();
		responseBody.setEntityList(sl);
		return responseBody;
	}
	
	
/*	@ResponseBody
	@RequestMapping(value = "/queryUserappList", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<UserLogonHis> queryUserappList(HttpServletRequest request,@RequestBody UserLogonHis userLogonHis) {
		ResponseData<UserLogonHis> responseBody = new ResponseData<UserLogonHis>();
		Result result = new Result();
		try {
			Page<UserLogonHis> userLogonHisList = null;
			if(userLogonHis.getProperty()!=null && userLogonHis.getProperty().equals("inTime")){
				userLogonHis.setProperty("init_logon_time");
			}
			Direction direction = Direction.DESC;
			String property = "init_logon_time";
			if(!StringUtils.isEmpty(userLogonHis.getProperty())){
				if("desc".equals(userLogonHis.getDirection())){
					direction = Direction.DESC;
				}else{
					direction = Direction.ASC;
				}
				property = "init_logon_time";
			}
			List<String> list = new ArrayList<String>();
			list.add(property);
			Sort sort = new Sort(direction,property);
			if(userLogonHis.getProperty()!=null && userLogonHis.getDirection()!=null){
				userLogonHisList = userLogonHisService.queryPageListapp(userLogonHis,new PageRequest(userLogonHis.getPageNum(), userLogonHis.getPageSize(),direction,property));
			}else{
				userLogonHisList = userLogonHisService.queryPageListapp(userLogonHis,new PageRequest(userLogonHis.getPageNum(), userLogonHis.getPageSize()));
			}
			responseBody.setPageList(userLogonHisList);
			responseBody.setResult(new Result(Status.OK, ""));
			return responseBody;	
		} catch (PlatformException e) {
			result.addError(e.getMessage(), e.getCode()+"");
			logger.error("queryFeedbackList ", e);
		}
		return responseBody;
	}*/
	
	@ResponseBody
	@RequestMapping(value = "/queryUserappList", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<appUserList> queryUserappList(HttpServletRequest request,@RequestBody appUserList appUserList) {
		ResponseData<appUserList> responseBody = new ResponseData<appUserList>();
		Result result = new Result();
		try {
			Page<appUserList> userLogonHisList = null;
			if(appUserList.getProperty()!=null && appUserList.getProperty().equals("inTime")){
				appUserList.setProperty("init_logon_time");
			}
			Direction direction = Direction.DESC;
			String property = "init_logon_time";
			if(!StringUtils.isEmpty(appUserList.getProperty())){
				if("desc".equals(appUserList.getDirection())){
					direction = Direction.DESC;
				}else{
					direction = Direction.ASC;
				}
				property = "init_logon_time";
			}
			List<String> list = new ArrayList<String>();
			list.add(property);
			/*Sort sort = new Sort(direction,property);*/
			if(appUserList.getProperty()!=null && appUserList.getDirection()!=null){
				userLogonHisList = appUserListService.queryPageListapp(appUserList,new PageRequest(appUserList.getPageNum(), appUserList.getPageSize(),direction,property));
			}else{
				userLogonHisList = appUserListService.queryPageListapp(appUserList,new PageRequest(appUserList.getPageNum(), appUserList.getPageSize()));
			}
			responseBody.setPageList(userLogonHisList);
			responseBody.setResult(new Result(Status.OK, ""));
			return responseBody;	
		} catch (PlatformException e) {
			result.addError(e.getMessage(), e.getCode()+"");
			logger.error("queryFeedbackList ", e);
		}
		return responseBody;
	}
}
