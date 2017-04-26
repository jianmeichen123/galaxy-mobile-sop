package com.galaxyinternet.rili.service;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.galaxyinternet.framework.core.dao.BaseDao;
import com.galaxyinternet.framework.core.model.Result;
import com.galaxyinternet.framework.core.model.Result.Status;
import com.galaxyinternet.framework.core.service.impl.BaseServiceImpl;
import com.galaxyinternet.framework.core.utils.DateUtil;
import com.galaxyinternet.rili.dao.ScheduleDepartUnoDao;
import com.galaxyinternet.rili.dao.ScheduleInfoDao;
import com.galaxyinternet.rili.dao.ScheduleMettingUsersDao;
import com.galaxyinternet.rili.dao.SchedulePersonPlanDao;
import com.galaxyinternet.rili.model.ScheduleInfo;
import com.galaxyinternet.rili.util.AccountDate;
import com.galaxyinternet.rili.util.ScheduleUtil;
import com.galaxyinternet.rili.util.UtilService;


@Service("com.galaxyinternet.rili.service.ScheduleInfoService")
public class ScheduleInfoServiceImpl extends BaseServiceImpl<ScheduleInfo> implements ScheduleInfoService{

	@Autowired
	private ScheduleInfoDao scheduleInfoDao;
	
	@Autowired
	private ScheduleMettingUsersDao scheduleMettingUsersDao;
	
	
	@Autowired
	private SchedulePersonPlanDao schedulePersonPlanDao;
	
	@Autowired
	private UtilService utilService;

	
	@Autowired
	private ScheduleDepartUnoDao scheduleDepartUnoDao;
	
	@Override
	protected BaseDao<ScheduleInfo, Long> getBaseDao() {
		return this.scheduleInfoDao;
	}

	
	
	
	
	/** 
	* 日历结果分组
	* 上中下午分组
	*    深夜 00:00-05:59 d
	*    上午 06:00-11:59 c
	*    下午 12:00-17:59 b
	*    晚上 18:00-23:59 a
	* 日分组
	* 月分组
	*/
	public List<ScheduleUtil> queryAndConvertList(ScheduleInfo query) throws ParseException {

		List<ScheduleUtil> resultList = new ArrayList<ScheduleUtil>();
		
		//结果查询  封装
		List<ScheduleInfo> qList = null;

		//有隔日的日程list
		List<ScheduleInfo> scheduleInfoList = new ArrayList<ScheduleInfo>();
		
		//有隔日的日程重新封装list按年月查询
		if(query.getDay()==null){
			 qList = scheduleInfoDao.selectList(query);
			if(qList!=null && !qList.isEmpty()){
				ScheduleInfo sinfo ;
				for(ScheduleInfo temp : qList){	
					if(temp.getStartTime()!=null && temp.getEndTime()!=null && !AccountDate.get(temp.getStartTime(),temp.getEndTime())){														
						
						List<String> sss = AccountDate.getXiuEveryday(temp.getStartTime().substring(0, 10),temp.getEndTime().substring(0, 10), query.getLastMouthDay());
						for(String ss:sss){
							sinfo = new ScheduleInfo();
							if(temp.getStartTime().substring(0, 10).equals(ss)){
								sinfo.setStartTime(temp.getStartTime());
								sinfo.setEndTime(ss+" 23:59:00");
								sinfo.setName(temp.getName());
								sinfo.setId(temp.getId());	
								sinfo.setCreatedId(temp.getCreatedId());
								sinfo.setUpdatedId(temp.getUpdatedId());
								sinfo.setIsAllday(temp.getIsAllday());
								sinfo.setRemark(temp.getRemark());
								sinfo.setCreatedTime(temp.getCreatedTime());
								sinfo.setUpdatedTime(temp.getUpdatedTime());
							}
							else if(temp.getEndTime().substring(0, 10).equals(ss)){
								sinfo.setStartTime(ss+" 00:00:00");
								sinfo.setEndTime(temp.getEndTime());
								sinfo.setName(temp.getName());
								sinfo.setId(temp.getId());	
								sinfo.setCreatedId(temp.getCreatedId());
								sinfo.setUpdatedId(temp.getUpdatedId());
								sinfo.setIsAllday(temp.getIsAllday());
								sinfo.setRemark(temp.getRemark());
								sinfo.setCreatedTime(temp.getCreatedTime());
								sinfo.setUpdatedTime(temp.getUpdatedTime());
							}else{				
								sinfo.setStartTime(ss+" 00:00:00");
								sinfo.setEndTime(ss+" 23:59:00");
								sinfo.setName(temp.getName());
								sinfo.setId(temp.getId());
								sinfo.setCreatedId(temp.getCreatedId());
								sinfo.setUpdatedId(temp.getUpdatedId());
								sinfo.setIsAllday(temp.getIsAllday());
								sinfo.setRemark(temp.getRemark());
								sinfo.setCreatedTime(temp.getCreatedTime());
								sinfo.setUpdatedTime(temp.getUpdatedTime());
								
							}	
							scheduleInfoList.add(sinfo);
							
						}
	
					}
					
				}
				
			}		
			qList.addAll(scheduleInfoList);
			Iterator<ScheduleInfo> it = qList.iterator();
			while(it.hasNext()){
				ScheduleInfo x = it.next();
			    if(x.getStartTime()!=null && x.getEndTime()!=null && !AccountDate.get(x.getStartTime(),x.getEndTime())){
			        it.remove();
			    }		    
			}
		}
		//按天查询时日程跨日的情况
		if(query.getDay()!=null){
			
			ScheduleInfo scheduleInfo = new ScheduleInfo();
			ScheduleInfo sInfo = new ScheduleInfo();
			
			String bqEndTime = query.getBqEndTime();
			String eqStartTime = query.getBqStartTime();

			scheduleInfo.setBqEndTime(bqEndTime);
			scheduleInfo.setEqStartTime(eqStartTime);
			scheduleInfo.setCreatedId(query.getCreatedId());
			
			qList = scheduleInfoDao.selectList(scheduleInfo);
			
			sInfo.setBqEndTime(bqEndTime);
			sInfo.setBqStartTime(eqStartTime);
			sInfo.setIsAllday((byte) 1);
			sInfo.setCreatedId(query.getCreatedId());
			
			scheduleInfoList=scheduleInfoDao.selectList(sInfo);
			
			
			String sd = query.getBqEndTime().substring(0, 10);
			if(qList!=null && !qList.isEmpty()){
				
				for(ScheduleInfo temp : qList){	
					if(temp.getStartTime()!=null && temp.getEndTime()!=null){
						if(!temp.getStartTime().substring(0, 10).equals(sd)){
							
							temp.setStartTime(sd+" 00:00:00");
							
							
						}
						if(!temp.getEndTime().substring(0, 10).equals(sd)){							
							temp.setEndTime(sd+" 23:59:59");
	
						}
						
					}

					
				}
			}
			qList.addAll(scheduleInfoList);
				
		}

		//结果封装
		if(qList!=null && !qList.isEmpty()){
			
			Map<String,List<ScheduleInfo>> dateKey_infos = new HashMap<String,List<ScheduleInfo>>();
			
			if(query.getDay()!=null && query.getMonth()!=null && query.getYear()!=null){ // 年月日  按日的00-24点查询， 上中下午分组
				Long _time = (long)6  * 60 * 60 * 1000 ;
				
				Long  t1_b = DateUtil.convertHMSToDateTime(query.getYear(), query.getMonth(), query.getDay(), 0, 0, 0); 
				Long  t2_b = t1_b + _time ;
				Long  t3_b = t2_b + _time ;
				Long  t4_b = t3_b + _time ;
				
				Long group[] = { t4_b, t3_b, t2_b, t1_b };
				
				String code = null;
				
				for(ScheduleInfo temp : qList){
					if(temp.getStartTime()!=null){
						if(temp.getIsAllday()!=null && temp.getIsAllday().intValue()==1){
							code = "e" ;
						}else{						
							Long dateKeyLong = DateUtil.stringToLong(temp.getStartTime(), "yyyy-MM-dd HH:mm:ss");
							if(dateKeyLong>=t4_b){
								code = "a" ;
							}else if(dateKeyLong>=t3_b){
								code = "b" ;
							}else if(dateKeyLong>=t2_b){
								code = "c" ;
							}else if(dateKeyLong>=t1_b){
								code = "d" ;
							}
/*							for(int i=0;i<group.length;i++){
								
								if(dateKeyLong >= group[i]){
									switch (i) {
									case 0:
										code = "a";
										break;
									case 1:
										code = "b";
										break;
									case 2:
										code = "c";
										break;
									case 3:
										code = "d";
										break;
									default:
										break;
									}
								}
							}*/
							
							
							
							
						}
						if(dateKey_infos.containsKey(code)){
							dateKey_infos.get(code).add(temp);
						}else{
							List<ScheduleInfo> tempInfos = new ArrayList<ScheduleInfo>();
							tempInfos.add(temp);
							dateKey_infos.put(code, tempInfos);
						}
					}
				}
			}else if (query.getDay()==null){
				String format = null;
				if( query.getMonth()!=null && query.getYear()!=null){    // 年月   按月的1-31号查询， 日分组
					format = "yyyy-MM-dd";
				}else if( query.getMonth()==null && query.getYear()!=null){ // 年   按月1-12月查询，月分组
					format = "yyyy-MM";
				}
				
				for(ScheduleInfo temp : qList){
					//2017/4/17号修改 报空指针
					String stime= temp.getStartTime().substring(0,format.length());
					//String dateKey = DateUtil.dateFormat(temp.getStartTime(), format);
					String dateKey = DateUtil.dateFormat(stime, format);
					if(dateKey_infos.containsKey(dateKey)){
						dateKey_infos.get(dateKey).add(temp);
					}else{
						List<ScheduleInfo> tempInfos = new ArrayList<ScheduleInfo>();
						tempInfos.add(temp);
						dateKey_infos.put(dateKey, tempInfos);
					}
				}
			}
			for(Map.Entry<String, List<ScheduleInfo>> tempE : dateKey_infos.entrySet()){
				ScheduleUtil au = new ScheduleUtil();
				au.setDateKey(tempE.getKey());
				au.setSchedules(tempE.getValue());
				
				resultList.add(au);
			}
		}
		
		return resultList;
	}

	
	/** 
	 *  验证要保存的日程是否有冲突的日程
	 *  @param   query.getCreatedId()  query.getStartTime()   query.getEndTime()
	 *  @return  日程“XXX”  /   2个日程
	 */
	@Override
	public String getCconflictSchedule(ScheduleInfo query){
		
		String content = null;
		
		String bqEndTime = query.getEndTime();
		String eqStartTime = query.getStartTime();
		
		if(query.getIsAllday() != null && query.getIsAllday().intValue() == 1){
			bqEndTime = eqStartTime + " 23:59:59";
		}
		query.setBqEndTime(bqEndTime);
		query.setEqStartTime(eqStartTime);
		query.setStartTime(null);
		query.setEndTime(null);
		List<ScheduleInfo> qList = scheduleInfoDao.selectList(query);
		if(qList != null && !qList.isEmpty()){
			if(qList.size() == 1){
				content = "日程\""+qList.get(0).getName() +"\"";
			}else
				content = qList.size() + "个日程";
		}
		
		return content;
	}
	/**
	 * 判断今天 添加其他日程 是否超过20个
	 */
	public String getCountSchedule(ScheduleInfo query){
		
		ScheduleInfo scheduleInfo = new ScheduleInfo();
		scheduleInfo.setCreatedId(query.getCreatedId());
		scheduleInfo.setType((byte) 3);
		String content = null;
		
		String bqStartTime = DateUtil.convertDateToString(new Date(),"yyyy-MM-dd")+" 00:00:00";
		String bqEndTime = DateUtil.convertDateToString(new Date(),"yyyy-MM-dd")+" 23:59:59";
		

		scheduleInfo.setBqEndTime(bqEndTime);
		scheduleInfo.setBqStartTime(bqStartTime);

		List<ScheduleInfo> qList = scheduleInfoDao.selectList(scheduleInfo);
		if(qList != null && !qList.isEmpty()){
			if(qList.size() >20){
				content = "您每天最多可创建20条日程，无法再创建了。";				
			}
		}
	return content;
	}
	/**
	 * 添加 会议及与会人
	 */
	/*@Transactional
	public Long saveMeetSchedule(Object objUser,ScheduleInfo query) {
		

		//添加会议日程
		User user = (User)objUser;	
		Long id = scheduleInfoDao.insert(query);		
		
		//删除所有与会人id
		ScheduleMettingUsers comShareQ = new ScheduleMettingUsers();
		comShareQ.setScheduleId(id);
		scheduleMettingUsersDao.delete(comShareQ);
		
		//删除所有部门参会议人数量
		ScheduleDepartUno dun = new ScheduleDepartUno();
		dun.setRemarkType((byte) 0);
		dun.setCreatedId(user.getId());
		scheduleDepartUnoDao.delete(dun);
		
		
		List<ScheduleDepartUno> toSaveDeptUnos = new ArrayList<ScheduleDepartUno>();
		
		List<ScheduleMettingUsers> toSaveScheduleMettingUsers = new ArrayList<ScheduleMettingUsers>();
		
		
		List<DeptNoUsers> deptNoUsers = query.getDeptNoUsers();
		
		
		if(deptNoUsers!=null && !deptNoUsers.isEmpty()){
			
			for(DeptNoUsers temp : deptNoUsers){
				
				List<Long> userIds = temp.getUserIds();
				if(userIds == null || userIds.isEmpty()) continue;
				Map<Long, String> uidNmaeMap = utilService.queryUidNmaeMap(userIds);
				
				// 部门-相关人数
				dun = new ScheduleDepartUno();
				dun.setRemarkType((byte) 1);
				dun.setDepartmentId(temp.getDeptId());
				dun.setUserCount(temp.getUserCount());
				dun.setCreatedId(user.getId());
				toSaveDeptUnos.add(dun);
				
				for(Long uidTemp : userIds){
					// 日程与会议
				
					comShareQ = new ScheduleMettingUsers();
					comShareQ.setScheduleId(id);
					comShareQ.setUserId(uidTemp);
					comShareQ.setUserName(uidNmaeMap.get(uidTemp));
					comShareQ.setDepartmentId(temp.getDeptId());
					comShareQ.setCreatedId(user.getId());
					
					
					toSaveScheduleMettingUsers.add(comShareQ);
					
				}
			}
			
			scheduleDepartUnoDao.insertInBatch(toSaveDeptUnos);
			scheduleMettingUsersDao.insertInBatch(toSaveScheduleMettingUsers);
		}

		return id;
	}

	/**
	 * 拜访的添加
	 *//*
	@Transactional
	public Long savePlanSchedule(Object objUser, ScheduleInfo scheduleInfo) {
		User user = (User)objUser;	
		Long id = scheduleInfoDao.insert(scheduleInfo);		
		if(id!=null && scheduleInfo.getSchedulePersonPlanList()!=null && scheduleInfo.getSchedulePersonPlanList().size()>0){
			
			List<SchedulePersonPlan> ss = scheduleInfo.getSchedulePersonPlanList();
			for(SchedulePersonPlan sp:ss){
				sp.setScheduleId(id);
				sp.setCreatedId(user.getId());
			}
			schedulePersonPlanDao.insertInBatch(ss);
		}
		
		return id;
	}

	*//**
	 * 更新拜访     需要传的参数有schedule_id 拜访日历的id  contacts_id 联系人的id
	 *//*
	@Transactional
	public void updatePlanSchedule(Object objUser,ScheduleInfo scheduleInfo) {
		
		User user = (User)objUser;
		
		//更新拜访记录
		scheduleInfo.setUpdatedId(user.getId());
		scheduleInfoDao.updateById(scheduleInfo);
		
		//删除所有的拜访人与联系人的关联表
		SchedulePersonPlan schedulePersonPlan = new SchedulePersonPlan();
		schedulePersonPlan.setScheduleId(scheduleInfo.getId());
		schedulePersonPlanDao.delete(schedulePersonPlan);
		

		List<SchedulePersonPlan> ss = scheduleInfo.getSchedulePersonPlanList();
		if(ss!=null && ss.size()>0){
			
			for(SchedulePersonPlan schedulePersonPla:ss){
				schedulePersonPla.setScheduleId(scheduleInfo.getId());
				schedulePersonPla.setCreatedId(user.getId());

			}
			schedulePersonPlanDao.insertInBatch(ss);
		}
		
	}
	
	*//**
	 * 拜访的删除
	 *//*

	@Override
	public void delePlanSchedule(ScheduleInfo scheduleInfo) {
		
		//删除拜访日程

		scheduleInfoDao.delete(scheduleInfo);
		
		//删除拜访和人关联表数据
		SchedulePersonPlan schedulePersonPlan = new SchedulePersonPlan();
		schedulePersonPlan.setScheduleId(scheduleInfo.getId());
		schedulePersonPlanDao.delete(schedulePersonPlan);
		
		
		
	}

*/
	
	
	
	
}
