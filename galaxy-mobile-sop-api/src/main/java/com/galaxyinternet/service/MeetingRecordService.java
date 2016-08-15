package com.galaxyinternet.service;

import org.springframework.data.domain.Pageable;

import com.galaxyinternet.bo.project.MeetingRecordBo;
import com.galaxyinternet.framework.core.model.Page;
import com.galaxyinternet.framework.core.service.BaseService;
import com.galaxyinternet.model.project.MeetingRecord;
import com.galaxyinternet.model.project.Project;
import com.galaxyinternet.model.sopfile.SopFile;



public interface MeetingRecordService extends BaseService<MeetingRecord> {

	Long insertMeet(MeetingRecord meetingRecord, Project project, SopFile sopFile, boolean equalNowPrograss);
	
	public Page<MeetingRecordBo> queryMeetPageList(MeetingRecordBo query, Pageable pageable);

	public void upTermSheetSign(Project project, Long id, Long departmentId);

	public Page<MeetingRecordBo> queryMeetPage(MeetingRecordBo query, Pageable pageable);

	Long addCyMeetRecord(MeetingRecord meetingRecord, SopFile sopFile);
	
/*	//app2期新增的查询会议时查询出多个录音文件
	public Page<MeetingRecordBo> queryAppMeetPage(MeetingRecordBo query, Pageable pageable);*/
	
	

}