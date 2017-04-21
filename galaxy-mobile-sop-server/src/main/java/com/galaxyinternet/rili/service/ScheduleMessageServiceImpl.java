package com.galaxyinternet.rili.service;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.galaxyinternet.framework.core.dao.BaseDao;
import com.galaxyinternet.framework.core.model.PageRequest;
import com.galaxyinternet.framework.core.service.impl.BaseServiceImpl;
import com.galaxyinternet.framework.core.thread.GalaxyThreadPool;
import com.galaxyinternet.framework.core.utils.DateUtil;
import com.galaxyinternet.rili.dao.ScheduleMessageDao;
import com.galaxyinternet.rili.dao.ScheduleMessageUserDao;
import com.galaxyinternet.rili.mesHandler.ScheduleMessageGenerator;
import com.galaxyinternet.rili.model.ScheduleMessage;
import com.galaxyinternet.rili.model.ScheduleMessageUser;


@Service("com.galaxyinternet.rili.service.ScheduleMessageService")
public class ScheduleMessageServiceImpl extends BaseServiceImpl<ScheduleMessage> implements ScheduleMessageService{

	@Autowired
	private ScheduleMessageDao scheduleMessageDao;
	@Autowired
	private ScheduleMessageUserDao scheduleMessageUserDao;
	
	@Autowired
	ScheduleMessageGenerator messageGenerator;
	
	
	@Override
	protected BaseDao<ScheduleMessage, Long> getBaseDao() {
		return this.scheduleMessageDao;
	}

	
	
	/**
	 * 个人消息 列表查询
     */
	public List<ScheduleMessageUser> queryPerMessAndConvertList(ScheduleMessageUser query, PageRequest pageable) {
		List<ScheduleMessageUser> results = new ArrayList<ScheduleMessageUser>();

		List<ScheduleMessageUser> mus = scheduleMessageUserDao.selectList(query, pageable);
		
		Map<Long,ScheduleMessageUser> u_mess_map = new HashMap<Long,ScheduleMessageUser>();
		if(mus != null && !mus.isEmpty()){
			for(ScheduleMessageUser tempU : mus){
				u_mess_map.put(tempU.getMid(), tempU);
			}
			
			ScheduleMessage mQ = new ScheduleMessage();
			mQ.setIds(u_mess_map.keySet());
			List<ScheduleMessage> mess = scheduleMessageDao.selectList(mQ);
			
			if(mess != null && !mess.isEmpty()){
				for(ScheduleMessage tempM : mess){
					if(u_mess_map.containsKey(tempM.getId())){
						u_mess_map.get(tempM.getId()).setMessage(tempM);
						results.add(u_mess_map.get(tempM.getId()));
					}
				}
			}
		}
		
		return results;
	}
	
	
	
	/**
	 * 消息   查询   当天需要推送的消息
     */
	public LinkedHashMap<Long,ScheduleMessage> queryTodayMessToSend() {
		
		//List<ScheduleMessageUser> results = new ArrayList<ScheduleMessageUser>();
		LinkedHashMap<Long,ScheduleMessage> mid_mess_map = new LinkedHashMap<Long,ScheduleMessage>();
		
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
			
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		long bdate = calendar.getTimeInMillis();
		String btime = DateUtil.longString(bdate);
		
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 0);
		
		long edate = calendar.getTimeInMillis();
		String etime = DateUtil.longString(edate);
		
		// 消息内容查询
		ScheduleMessage mQ = new ScheduleMessage();
		mQ.setBtime(bdate);
		mQ.setEtime(edate);
		mQ.setStatus((byte) 1);
		mQ.setProperty("send_time");
		mQ.setDirection("asc");
		List<ScheduleMessage> mess = scheduleMessageDao.selectList(mQ);
		
		// 消息内容  -> 消息人 查询
		if(mess != null && !mess.isEmpty()){
			// 消息 ids
			List<Long> mids = new ArrayList<Long>();
			for(ScheduleMessage tempM : mess){
				mids.add(tempM.getId());
			}
		    
			// 根据消息 ids  查询 muser
			ScheduleMessageUser muQ = new ScheduleMessageUser();
			muQ.setIsUse((byte)0);    //0:可用    1:禁用
			muQ.setIsSend((byte)0);   //0:未发送  1+:已发送
			muQ.setIsDel((byte)0);    //0:未删除  1:已删除
			muQ.setMids(mids);
			List<ScheduleMessageUser> mus = scheduleMessageUserDao.selectList(muQ);
			
			if(mus != null && !mus.isEmpty()){
				for(ScheduleMessage tempM : mess){
					for(ScheduleMessageUser tempU : mus){
						if(tempU.getMid().longValue() == tempM.getId().longValue()){
							tempM.getToUsers().add(tempU);
						}
					}
					mid_mess_map.put(tempM.getId(), tempM);
				}
			}
			
		}
		
		return mid_mess_map;
	}
	
	
	
	
	@Override
	public void saveMessageByInfo(Object scheduleInfo){
		
		final Object info = scheduleInfo;
		
		GalaxyThreadPool.getExecutorService().execute(new Runnable() {
			@Override
			public void run() {

				ScheduleMessage message = messageGenerator.process(info);
				Long mid = scheduleMessageDao.insert(message);
				/*
				private Long mid; // 消息 id
			    private Long uid; // 接收人 id
			    private String uname; 
			    private Byte typeRole; //会议(1:组织人 2:受邀人) 拜访(3:去拜访者 4:被拜访人)
			    private byte isUse;  //0:可用    1:禁用
			    private Byte isSend; //0:未发送  1+:已发送
			    private Byte isRead; //0:未读    1:已读
			    private Byte isDel;  //0:未删除  1:已删除
			    */
				List<ScheduleMessageUser> toInserts = new ArrayList<ScheduleMessageUser>();
				for(ScheduleMessageUser toU : message.getToUsers()){
					toU.setMid(mid);
					toInserts.add(toU);
				}
				scheduleMessageUserDao.insertInBatch(toInserts);
			}
		});
		
	}



	



	
	
	
}
