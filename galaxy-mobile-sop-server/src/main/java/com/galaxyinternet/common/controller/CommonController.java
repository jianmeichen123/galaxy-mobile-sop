package com.galaxyinternet.common.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.galaxyinternet.bo.UserBo;
import com.galaxyinternet.framework.core.constants.Constants;
import com.galaxyinternet.framework.core.constants.UserConstant;
import com.galaxyinternet.framework.core.exception.DaoException;
import com.galaxyinternet.framework.core.model.Header;
import com.galaxyinternet.framework.core.model.ResponseData;
import com.galaxyinternet.framework.core.model.Result;
import com.galaxyinternet.framework.core.model.Result.Status;
import com.galaxyinternet.framework.core.service.BaseService;
import com.galaxyinternet.model.project.Project;
import com.galaxyinternet.model.user.Menus;
import com.galaxyinternet.model.user.User;
import com.galaxyinternet.service.ProjectService;
import com.galaxyinternet.service.UserRoleService;
import com.galaxyinternet.utils.RoleUtils;

@Controller
@RequestMapping("/galaxy/common")
public class CommonController extends BaseControllerImpl<User, UserBo>{
	
	final Logger logger = LoggerFactory.getLogger(CommonController.class);
	
	@Autowired
	private UserRoleService userRoleService;
	
	@Autowired
	com.galaxyinternet.framework.cache.Cache cache;
	
	private String serverUrl;
	@Autowired
	private ProjectService projectService;
	
	/**
	 * 动态生成左边菜单项列表
	 * @author yangshuhua
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/menu/{selected}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<Menus> menu(@PathVariable("selected") int selected, HttpServletRequest request){
		
		serverUrl = getServerUrl();
		
		ResponseData<Menus> responseBody = new ResponseData<Menus>();
		Header header = new Header();
		header.setAttachment(selected);
		responseBody.setHeader(header);
		String p = request.getRequestURI();
		String url = request.getRequestURL().toString();
		String contextPath = request.getContextPath();
		String u = null;
		if(contextPath == null || "".equals(contextPath.trim())){
			//rl.substring(0, url.indexOf(p) + 1);
			u = url.substring(0, url.indexOf(p) + 1);
		}else{
			u = url.substring(0, url.indexOf(contextPath) + contextPath.length() + 1);
		}
		
		User user = (User) getUserFromSession(request);
		
		List<Menus> tabs = new ArrayList<Menus>();
		String params = Constants.SESSOPM_SID_KEY + "=" + getSessionId(request) + "&" + Constants.REQUEST_URL_USER_ID_KEY + "=" + getUserId(request);
		//通用Tab
		tabs.add(new Menus(1L, 0, "工作桌面", u + "galaxy/index?" + params));
		tabs.add(new Menus(2L, 0, "待办任务", u + "galaxy/soptask?" + params));
		//tabs.add(new Menus(3L, 0, "消息提醒", u + "galaxy/operationMessage/index?" + params));
		List<Long> roleIdList = userRoleService.selectRoleIdByUserId(user.getId());
		
		if(roleIdList.contains(UserConstant.TZJL)){
			//tabs.add(new Menus(4L, 0, "添加项目", u + "galaxy/app?" + params));
			tabs.add(new Menus(5L, 0, "我的项目", u + "galaxy/mpl?" + params));
			tabs.add(new Menus(21L, 0, "创意管理", u + "galaxy/idea?" + params));
			tabs.add(new Menus(6L, 0, "访谈跟进", u + "galaxy/project/progress/interView?" + params));
			tabs.add(new Menus(7L, 0, "会议纪要", u + "galaxy/project/progress/meetView?" + params));
		}
		
		if(roleIdList.contains(UserConstant.HRZJ) || roleIdList.contains(UserConstant.HRJL)
				|| roleIdList.contains(UserConstant.CWZJ) || roleIdList.contains(UserConstant.CWJL)
				|| roleIdList.contains(UserConstant.FWZJ) || roleIdList.contains(UserConstant.FWJL)){
			tabs.add(new Menus(9L, 0, "尽调报告", u + "galaxy/soptask?flag=jz&"+ params));
		}
		
		if(roleIdList.contains(UserConstant.HRZJ) || roleIdList.contains(UserConstant.HRJL)){
			tabs.add(new Menus(10L, 0, "完善简历", u + "galaxy/soptask?flag=jl&"+ params));
		}
		if(roleIdList.contains(UserConstant.CWZJ) || roleIdList.contains(UserConstant.CWJL)){
			tabs.add(new Menus(11L, 0, "付款凭证", u + "galaxy/soptask?flag=pz&"+ params));
		}
		
		if(roleIdList.contains(UserConstant.FWZJ) || roleIdList.contains(UserConstant.FWJL)){
			tabs.add(new Menus(12L, 0, "股权交割", u + "galaxy/soptask?flag=gq&"+ params));
		}
		
		tabs.add(new Menus(13L, 0, "模板管理", u + "galaxy/template?" + params));
		tabs.add(new Menus(14L, 0, "档案管理", u + "galaxy/sopFile/toFileList?" + params));
		
		
		//档案管理员
		if(roleIdList.contains(17L)){
			tabs.clear();
			tabs.add(new Menus(14L, 0, "档案管理", u + "galaxy/sopFile/toFileList?" + params));
		}
		
		//管理员
		if(roleIdList.contains(16L)){
			tabs.clear();
			tabs.add(new Menus(15L, 0, "用户管理", serverUrl + "mobilePlatform/galaxy/user?" + params));
			tabs.add(new Menus(16L, 0, "数据字典", serverUrl + "mobilePlatform/galaxy/dict/index?" + params));
		}
		
		//高管
		if(roleIdList.contains(UserConstant.HHR) || roleIdList.contains(1L) || roleIdList.contains(2L)){
			tabs.clear();
			tabs.add(new Menus(1L, 0, "工作桌面", serverUrl + "mobileReport/galaxy/report/platform?" + params));
			//tabs.add(new Menus(3L, 0, "消息提醒", serverUrl +"sop/galaxy/operationMessage/index?"+params));
			tabs.add(new Menus(4L, 0, "项目查询", serverUrl +"mobileSop/galaxy/cpl?" + params));
			tabs.add(new Menus(21L, 0, "创意管理", u + "galaxy/idea?" + params));
			tabs.add(new Menus(5L, 0, "数据简报", serverUrl +"mobileReport/galaxy/report/dataBriefing?" + params));
			tabs.add(new Menus(6L, 0, "项目分析", serverUrl +"mobileReport/galaxy/report/projectAnalysis?" + params));
			tabs.add(new Menus(7L, 0, "绩效考核", serverUrl +"mobileReport/galaxy/report/kpi?" + params));
			tabs.add(new Menus(11L, 0, "投后运营", "javascript:void(0);")
					.addNode(new Menus(8L, 1, "投后项目跟踪", serverUrl +"mobileReport/galaxy/report/afterInvestTrack?" + params))
					.addNode(new Menus(9L, 1, "投后业务运营", serverUrl +"mobileReport/galaxy/report/afterInvestBusiness?" + params))
					.addNode(new Menus(10L, 1, "投后企业财报", serverUrl +"mobileReport/galaxy/report/afterInvestFinace?" + params)));
		}
		//董事长秘书      CEO秘书
		if(roleIdList.contains(UserConstant.DMS) ||roleIdList.contains(UserConstant.CEOMS)){
			tabs.clear();
			tabs.add(new Menus(1L, 0, "工作桌面", u + "galaxy/index?" + params));
			tabs.add(new Menus(18L, 0, "立项会排期池", u + "galaxy/lxh?" + params));
			tabs.add(new Menus(19L, 0, "投决会排期池", u + "galaxy/tjh?" + params));
			tabs.add(new Menus(20L, 0, "CEO评审会排期池", u + "galaxy/psh?" + params));								
		}
	    responseBody.setEntityList(tabs);
		return responseBody;
	}

	@Override
	protected BaseService<User> getBaseService() {
		return null;
	}

	public String getServerUrl() {
		return serverUrl;
	}

	@Value("${project.server.url}")
	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}
	
	@ResponseBody
	@RequestMapping(value="/judgeRole/{projectId}",method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<User> judgeRole(HttpServletRequest request,@PathVariable String projectId){
		ResponseData<User> responseBody = new ResponseData<User>();
		User user = (User) request.getSession().getAttribute(Constants.SESSION_USER_KEY);
		if (user == null) {
			responseBody.setResult(new Result(Status.ERROR, "未登录!"));
			return responseBody;
		}
		if(StringUtils.isBlank(projectId)){
			responseBody.setResult(new Result(Status.ERROR,"传入的projectId为空"));
		}
		try {
			Result result = null;
			List<Long> roleIdList = userRoleService.selectRoleIdByUserId(user
					.getId());
			Project query = new Project();
			//1为高管
			if(RoleUtils.isGaoGuan(roleIdList)){
				result = new Result(Status.OK,"");
				result.setMessage("show");
				responseBody.setResult(result);	
				return responseBody;
			}else if(RoleUtils.isTZJL(roleIdList)){
				//当为投资经理时候本人
				query.setId(Long.parseLong(projectId));
				query.setCreateUid(user.getId());
			}else if(RoleUtils.isHHR(roleIdList)){
				//当为合伙人时本事业线下
				query.setId(Long.parseLong(projectId));
				query.setProjectDepartid(user.getDepartmentId());
			}else if(RoleUtils.isHRJL(roleIdList) || RoleUtils.isHRZJ(roleIdList) || RoleUtils.isCWJL(roleIdList) || RoleUtils.isCWZJ(roleIdList)|| RoleUtils.isFWJL(roleIdList)||RoleUtils.isFWZJ(roleIdList)){
				//当为人财法不显示
				result = new Result(Status.OK,"");
				result.setMessage("hide");
				responseBody.setResult(result);	
				return responseBody;
			}else if(RoleUtils.isMs(roleIdList)){
				//秘书不显示
				result = new Result(Status.OK,"");
				result.setMessage("hide");
				responseBody.setResult(result);	
				return responseBody;
			}
			List<Project> project = projectService.queryList(query);
			result = new Result(Status.OK,"");
			if(project.isEmpty()){
				result.setMessage("hide");
			}else{
				result.setMessage("show");
			}
			responseBody.setResult(result);	
		} catch (DaoException e) {
			// TODO: handle exception
			responseBody.setResult(new Result(Status.ERROR,"系统异常"));
		}
		return responseBody;
		
	}
	
	
	

}
