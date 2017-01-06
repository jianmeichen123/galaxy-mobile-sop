package com.galaxyinternet.dao.project;

import java.util.List;

import com.galaxyinternet.bo.project.MeetingSchedulingBo;
import com.galaxyinternet.framework.core.dao.BaseDao;
import com.galaxyinternet.framework.core.model.Page;
import com.galaxyinternet.framework.core.model.PageRequest;
import com.galaxyinternet.model.project.MeetingScheduling;

public interface MeetingSchedulingDao extends BaseDao<MeetingScheduling, Long> {
	
	public int updateCountBySelective(MeetingScheduling meetingScheduling);
	
	/**
	 * 查询top5，按次数时间降序
	 * @author zhaoying
	 * @return
	 */
	List<MeetingSchedulingBo> selectTop5ProjectMeetingByType(String type);
	
	List<MeetingSchedulingBo> selectProjectMeetingByType(String type);
	
	int updateBySelective(MeetingScheduling ms);
	/**
	 * 批量更新
	 * @param entityList
	 */
	void updateBatch(List<MeetingScheduling> entityList);
	
	public Page<MeetingScheduling> getMeetingList(MeetingScheduling bo,PageRequest page);
	
	 /***
     * 根据id查询排期集合
     * @param ids
     * @return
     */
    public List<MeetingScheduling> getMeetingListByIds(MeetingScheduling bo);
    
    /**
     * 新增搜索排期会排期
     * 
     */
    public Page<MeetingScheduling> queryMeschedulingAll(MeetingScheduling query,PageRequest pageRequest);
    
    /**
     * 新增查询日历上的排期会排期
     */
    
    public List<MeetingSchedulingBo> selectMonthScheduling (MeetingSchedulingBo query);
    
    /**
     * 查询日历上的每天出现的会议类型及次数
     */
    
    public Long selectMonthSchedulingCount(MeetingSchedulingBo query);
    
    
    /**
     * 新增查询当日的事项
     */
    
    public List<MeetingSchedulingBo> selectDayScheduling (MeetingSchedulingBo query);
    
    /**
     * 查询未排期的总数
     */
    
    public Long selectdpqCount(MeetingSchedulingBo query);
    
    
    /**
     * 2016/12/26
     * 查询出排队数量
     */
    
    public Long selectpdCount(MeetingScheduling query);
    
    
    
    /**
     * 2016/12/26
     * 查询出排队数量
     */
    
    public Long selectltpdCount(MeetingScheduling query);
}