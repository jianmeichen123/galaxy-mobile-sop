package com.galaxyinternet.project.controller;

import java.util.ArrayList;
import java.util.List;

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

import com.galaxyinternet.bo.project.MeetingRecordBo;
import com.galaxyinternet.bo.touhou.ProjectHealthBo;
import com.galaxyinternet.common.annotation.RecordType;
import com.galaxyinternet.common.controller.BaseControllerImpl;
import com.galaxyinternet.framework.core.exception.DaoException;
import com.galaxyinternet.framework.core.model.Page;
import com.galaxyinternet.framework.core.model.PageRequest;
import com.galaxyinternet.framework.core.model.ResponseData;
import com.galaxyinternet.framework.core.model.Result;
import com.galaxyinternet.framework.core.model.Result.Status;
import com.galaxyinternet.framework.core.service.BaseService;
import com.galaxyinternet.model.dict.Dict;
import com.galaxyinternet.model.project.MeetingRecord;
import com.galaxyinternet.model.sopfile.SopFile;
import com.galaxyinternet.model.touhou.ProjectHealth;
import com.galaxyinternet.service.DictService;
import com.galaxyinternet.service.MeetingRecordService;
import com.galaxyinternet.service.ProjectHealthService;
import com.galaxyinternet.service.SopFileService;

@Controller
@RequestMapping(value="/galaxy/project/postOperation")
public class PostOperatationController extends BaseControllerImpl<MeetingRecord, MeetingRecord> {

	final Logger logger = LoggerFactory.getLogger(PostOperatationController.class);
	
	@Autowired
	private MeetingRecordService meetingService;
	@Autowired
	private ProjectHealthService projectHealthService;
	@Autowired
	private DictService dictService;
	
	@Autowired
	private SopFileService sopFileService;
	
	public static final String ERROR_DAO_EXCEPTION = "系统出现异常";
	
	@Override
	protected BaseService<MeetingRecord> getBaseService() {
		return meetingService;
	}
	
	
	
	
	
	/**
	 * 分页查询透后运营会议记录
	 * @param meetingRecord
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/queryPostMeeting", method=RequestMethod.POST ,produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<MeetingRecord> queryPostMeeting(@RequestBody MeetingRecordBo meetingRecord){
		
		ResponseData<MeetingRecord> responseBody = new ResponseData<MeetingRecord>();
		try {
			PageRequest pageRequest = new PageRequest(meetingRecord.getPageNum(),
					meetingRecord.getPageSize());
			//运营分析 类型投后运营会议
			meetingRecord.setRecordType(RecordType.OPERATION_MEETING.getType());
			List<String> meetingTypeList = new ArrayList<String>();
			List<Dict> dictList = dictService.selectByParentCode("postMeetingType");
			for(Dict dict : dictList){
				meetingTypeList.add(dict.getCode());
			}
			meetingRecord.setMeetingTypeList(meetingTypeList);
			Page<MeetingRecord> pageList = meetingService.queryPageList(meetingRecord, pageRequest);
			responseBody.setPageList(pageList);
			
			//运营状态-默认为正常(2)
			Byte healthState = (byte)2;
			ProjectHealthBo healthQuery = new ProjectHealthBo();
			healthQuery.setProjectId(meetingRecord.getProjectId());
			PageRequest healthPageable = new PageRequest(0,1, new Sort(Direction.DESC,"created_time"));
			List<ProjectHealth> healthList = projectHealthService.queryList(healthQuery, healthPageable);
			if(healthList != null && healthList.size() >0)
			{
				ProjectHealth health = healthList.iterator().next();
				if(health != null && health.getHealthState() != null)
				{
					healthState = health.getHealthState();
				}
			}
			responseBody.putAttachmentItem("healthState", healthState);
		} catch (DaoException e) {
			responseBody.setResult(new Result(Status.ERROR, ERROR_DAO_EXCEPTION));
		}
		return responseBody;	
	}
	
	/**
	 * 运营分析-会议详情
	 */
	@ResponseBody
	@RequestMapping(value="postMeetingDetail", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<MeetingRecord> postMeetingDetail(@RequestBody MeetingRecord meetingRecord){
		ResponseData<MeetingRecord> data = new ResponseData<MeetingRecord>();
		if(null == meetingRecord.getId()){
			data.setResult(new Result(Status.ERROR,"缺少重要参数！"));
			return data;
		}
		
		//查询会议相关
		meetingRecord.setRecordType(RecordType.OPERATION_MEETING.getType());
		MeetingRecord record= meetingService.queryOne(meetingRecord);
		if(null == record){
			data.setResult(new Result(Status.ERROR,"输入参数不正确！未查到结果"));
			return data;
		}
		//重新组装设置会议名称 
		record.setMeetingNameStr(record.getMeetingTypeStr()+"纪要"+record.getMeetingName());
		//查询附件
		SopFile sopfile = new SopFile();
	    sopfile.setMeetingId(meetingRecord.getId());
		List<SopFile> sopFileList = new ArrayList<SopFile>();
		sopFileList = sopFileService.queryList(sopfile);
		record.setFiles(sopFileList);
		data.setEntity(record);
		return data;
	}

}
