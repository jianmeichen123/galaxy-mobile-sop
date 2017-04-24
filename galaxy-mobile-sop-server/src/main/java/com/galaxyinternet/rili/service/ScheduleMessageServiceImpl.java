package com.galaxyinternet.rili.service;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.galaxyinternet.framework.core.dao.BaseDao;
import com.galaxyinternet.framework.core.model.PageRequest;
import com.galaxyinternet.framework.core.service.impl.BaseServiceImpl;
import com.galaxyinternet.framework.core.thread.GalaxyThreadPool;
import com.galaxyinternet.framework.core.utils.DateUtil;
import com.galaxyinternet.model.user.User;
import com.galaxyinternet.rili.dao.ScheduleMessageDao;
import com.galaxyinternet.rili.dao.ScheduleMessageUserDao;
import com.galaxyinternet.rili.mesHandler.ScheduleMessageGenerator;
import com.galaxyinternet.rili.model.ScheduleInfo;
import com.galaxyinternet.rili.model.ScheduleMessage;
import com.galaxyinternet.rili.model.ScheduleMessageUser;
import com.galaxyinternet.rili.util.SchedulePushMessControlTask;


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
	 * 个人消息  设为全部已读
	 * 1.查询出 消息user 表中      个人的   可用   未读  未删除  的数据
	 * 2.查询出消息内容列表          状态为可用的消息
     */
	public void perMessageToRead(Object objUser){
		
		User user = (User)objUser;
		
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
		ScheduleMessageUser query = new ScheduleMessageUser();
		query.setUid(user.getId());
		query.setIsUse((byte)0);  //0:可用    1:禁用
		query.setIsRead((byte)0); //0:未读    1:已读
		query.setIsDel((byte)0);  //0:未删除  1:已删除
		
		List<ScheduleMessageUser> mus = scheduleMessageUserDao.selectList(query);
		
		if(mus != null && !mus.isEmpty()){
			
			Map<Long,Long> muid_mid_map = new HashMap<Long,Long>();
			
			for(ScheduleMessageUser tempU: mus){
				muid_mid_map.put(tempU.getId(), tempU.getMid());
			}
			
			ScheduleMessage mQ = new ScheduleMessage();
			mQ.setStatus((byte) 0);
			Set<Long> set = new HashSet<Long>(muid_mid_map.values());
			mQ.setIds(set);
			List<ScheduleMessage> mess = scheduleMessageDao.selectList(mQ);
			
			if(mess != null && !mess.isEmpty()){
				List<Long> editMuids = new ArrayList<Long>();
				for(ScheduleMessage tempm : mess){
					for(Map.Entry<Long,Long> entry : muid_mid_map.entrySet()) {
						if(entry.getValue().longValue() == tempm.getId().longValue()){
							editMuids.add(entry.getKey());
						}
					}
				}
				
				ScheduleMessageUser updateU = new ScheduleMessageUser();
				updateU.setIds(editMuids);
				updateU.setIsRead((byte)1); //0:未读    1:已读
				scheduleMessageUserDao.updateByIdSelective(updateU);
			}
			
		}
		
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
		//mQ.setSendTimeNotNull(true);
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
							if(tempM.getToUsers() == null){
								List<ScheduleMessageUser> tmus = new ArrayList<ScheduleMessageUser>();
								tmus.add(tempU);
								tempM.setToUsers(tmus);
							}else{
								tempM.getToUsers().add(tempU);
							}
						}
					}
					mid_mess_map.put(tempM.getId(), tempM);
				}
			}
			
		}
		
		return mid_mess_map;
	}
	
	
	
	/**
	 * 新增（日程 、、）操作完成后
	 * 生成对应消息
	 * ScheduleMessage    ScheduleMessageUser
     */
	@Override
	public void operateMessageBySaveInfo(Object scheduleInfo){
		
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
				
				
				//通知消息 已经改变
				if(DateUtil.checkLongIsToday(message.getSendTime())){
					SchedulePushMessControlTask.setHasChanged(true);
				}
				
			}
		});
		
	}



	

	/**
	 * 删除（日程 、、）操作完成后
	 * 消息同步修改
	 * ScheduleMessage    ScheduleMessageUser
     */
	@Override
	public void operateMessageByDeleteInfo(Object scheduleInfo){
		
		final Object info = scheduleInfo;
		
		GalaxyThreadPool.getExecutorService().execute(new Runnable() {
			@Override
			public void run() {
/*
				ScheduleInfo model = (ScheduleInfo) info;
				
				ScheduleMessage scheduleMessage =scheduleMessageDao.selectById(model.getId());
				
				
				if(scheduleMessage!=null){
					ScheduleMessageUser scheduleMessageUser = new ScheduleMessageUser();
					
					scheduleMessageUser.setMid(scheduleMessage.getId());
					
					List<ScheduleMessageUser> sss = scheduleMessageUserDao.selectList(scheduleMessageUser);
					
					ScheduleMessageUser schuleMgeUser = new ScheduleMessageUser();
					schuleMgeUser.setId(sss.getId());
					schuleMgeUser.setIsDel((byte)1);
					
					scheduleMessageUserDao.updateById(schuleMgeUser);
					
				}
				*/
			}
		});
		
	}

	
	
	/**
	 * 修改（日程 、、）操作完成后
	 * 消息同步修改
	 * ScheduleMessage    ScheduleMessageUser
     */
	@Override
	public void operateMessageByUpdateInfo(Object scheduleInfo){
		
		final Object info = scheduleInfo;
		
		GalaxyThreadPool.getExecutorService().execute(new Runnable() {
			@Override
			public void run() {



			}
		});
		
	}
	
	
}
