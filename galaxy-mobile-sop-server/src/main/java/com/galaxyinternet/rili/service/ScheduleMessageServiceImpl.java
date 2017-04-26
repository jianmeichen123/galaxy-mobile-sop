package com.galaxyinternet.rili.service;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.galaxyinternet.framework.core.dao.BaseDao;
import com.galaxyinternet.framework.core.model.Page;
import com.galaxyinternet.framework.core.model.PageRequest;
import com.galaxyinternet.framework.core.service.impl.BaseServiceImpl;
import com.galaxyinternet.framework.core.thread.GalaxyThreadPool;
import com.galaxyinternet.framework.core.utils.DateUtil;
import com.galaxyinternet.model.user.User;
import com.galaxyinternet.rili.dao.ScheduleMessageDao;
import com.galaxyinternet.rili.dao.ScheduleMessageUserDao;
import com.galaxyinternet.rili.mesHandler.ScheduleMessageGenerator;
import com.galaxyinternet.rili.mesSchedule.SchedulePushMessTask;
import com.galaxyinternet.rili.model.ScheduleInfo;
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
	
	@Autowired
	SchedulePushMessTask schedulePushMessTask;
	
	@Override
	protected BaseDao<ScheduleMessage, Long> getBaseDao() {
		return this.scheduleMessageDao;
	}

	
	
	/**
	 * 个人消息 列表查询
     */
	public Page<ScheduleMessageUser> queryPerMessAndConvertPage(ScheduleMessageUser query, PageRequest pageable) {
		

		Page<ScheduleMessageUser> mus = scheduleMessageUserDao.selectMuserAndMcontentList(query, pageable);
		/*
		List<ScheduleMessageUser> results = new ArrayList<ScheduleMessageUser>();
		Map<Long,ScheduleMessageUser> u_mess_map = new HashMap<Long,ScheduleMessageUser>();
		if(mus != null && !mus.isEmpty()){
			for(ScheduleMessageUser tempU : mus){
				u_mess_map.put(tempU.getMid(), tempU);
			}
			
			ScheduleMessage mQ = new ScheduleMessage();
			//mQ.setStatus((byte) 0);
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
		*/
		return mus;
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
			//mQ.setIds((Set<Long>) muid_mid_map.values());
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
	 * 个人消息  清空
     */
	public void perMessageToClear(Object objUser){
		
		User user = (User)objUser;
		
		ScheduleMessageUser query = new ScheduleMessageUser();
		query.setUid(user.getId());
		//query.setIsUse((byte)0);  //0:可用    1:禁用
		query.setIsDel((byte)0);  //0:未删除  1:已删除
		
		List<ScheduleMessageUser> mus = scheduleMessageUserDao.selectList(query);
		
		if(mus != null && !mus.isEmpty()){
			
			Map<Long,Long> muid_mid_map = new HashMap<Long,Long>();
			
			for(ScheduleMessageUser tempU: mus){
				muid_mid_map.put(tempU.getId(), tempU.getMid());
			}
			
			ScheduleMessage mQ = new ScheduleMessage();
			mQ.setStatus((byte) 0);
			//mQ.setIds((Set<Long>) muid_mid_map.values());
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
				updateU.setIsDel((byte)1);
				scheduleMessageUserDao.updateByIdSelective(updateU);
			}
			
		}
		
	}
	
	
	
	/**
	 * 消息   查询   当天需要推送的消息
     */
	public List<ScheduleMessage> queryTodayMessToSend() {
		
		//List<ScheduleMessageUser> results = new ArrayList<ScheduleMessageUser>();
		//LinkedHashMap<Long,ScheduleMessage> mid_mess_map = new LinkedHashMap<Long,ScheduleMessage>();
		
		List<ScheduleMessage> results = new ArrayList<ScheduleMessage>();
		
		
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
		//mQ.setBtime(bdate);
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
			muQ.setIsRead((byte)0);   //0:未读    1:已读
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
					results.add(tempM);
					//mid_mess_map.put(tempM.getId(), tempM);
				}
			}
		}
		
		return results;
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
					toU.setIsSend((byte) 0);
					toU.setIsRead((byte) 0);
					toU.setIsDel((byte) 0);
					toU.setMid(mid);
					
					toInserts.add(toU);
				}
				scheduleMessageUserDao.insertInBatch(toInserts);
				
				//通知消息 ：  已经添加新的消息
				//if(DateUtil.checkLongIsToday(message.getSendTime())){
					//SchedulePushMessControlTask.setHasChanged(true);
					message.setToUsers(toInserts);
					schedulePushMessTask.setHasSaved(message);
				//}
				
			}
		});
		
	}



	

	/**
	 * 删除（日程 、、）操作完成后
	 * 消息同步修改
	 * ScheduleMessage    ScheduleMessageUser
     */
	@Override
	public void operateMessageByDeleteInfo(Object scheduleInfo, String messageType){
		
		final Object info = scheduleInfo;
		final String mType = messageType;
		
		GalaxyThreadPool.getExecutorService().execute(new Runnable() {
			@Override
			public void run() {
				/*
				if(mType.startsWith("1")){
					
					//日程
					ScheduleInfo info_model = (ScheduleInfo) info;
					
					// 消息内容
					ScheduleMessage mq = new ScheduleMessage();
					mq.setStatus((byte) 1);
					mq.setType(mType);
					mq.setRemarkId(info_model.getId());
					ScheduleMessage message =scheduleMessageDao.selectOne(mq);
					
					if(message!=null){
						//主从判断
						Long info_pid = info_model.getParentId();
						if(info_pid != null){
							ScheduleMessageUser scheduleMessageUser = new ScheduleMessageUser();
							scheduleMessageUser.setMid(message.getId());
							scheduleMessageUser.setUid(info_model.getCreatedId());
							scheduleMessageUserDao.delete(scheduleMessageUser);
							
							
							//通知消息 ：  删除消息
							///if(DateUtil.checkLongIsToday(message.getSendTime()) && message.getSendTime().longValue() > System.currentTimeMillis()){
							//if(message.getStatus() == (byte)1){
								Map<String, List<Long>> delMap = new HashMap<String, List<Long>>();
								
								List<Long> mids = new ArrayList<Long>();
								mids.add(message.getId());
								
								List<Long> muids = new ArrayList<Long>();
								muids.add(info_model.getCreatedId());
								
								delMap.put(SchedulePushMessTask.DEL_MAP_KEY_MID, mids);
								delMap.put(SchedulePushMessTask.DEL_MAP_KEY_MUID, muids);
								
								schedulePushMessTask.setHasDeled(delMap);
							//}
						}else{
							scheduleMessageDao.deleteById(message.getId());
							
							ScheduleMessageUser scheduleMessageUser = new ScheduleMessageUser();
							scheduleMessageUser.setMid(message.getId());
							scheduleMessageUserDao.delete(scheduleMessageUser);
							
							
							//通知消息 ：  删除消息
							//if(message.getStatus() == (byte)1){
								Map<String, List<Long>> delMap = new HashMap<String, List<Long>>();
								
								List<Long> mids = new ArrayList<Long>();
								mids.add(message.getId());
								
								delMap.put(SchedulePushMessTask.DEL_MAP_KEY_MID, mids);
								
								schedulePushMessTask.setHasDeled(delMap);
							//}
						}
						
					}
					
				}
				*/
			}
		});
		
	}

	
	
	/**
	 * 修改（日程 、、）操作完成后
	 * 消息同步修改
	 * ScheduleMessage    ScheduleMessageUser
	 * 
	 * @param scheduleInfo scheduleInfo.setMessageType("1.3.2") handler标识的值
	 * @param messageType ScheduleMessage表中type值
     */
	@Override
	public void operateMessageByUpdateInfo(Object scheduleInfo, String messageType){
		
		final Object info = scheduleInfo;
		final String mType = messageType;
		
		
		GalaxyThreadPool.getExecutorService().execute(new Runnable() {
			@Override
			public void run() {
				/*
				if(mType.startsWith("1")){
					
					//日程
					ScheduleInfo info_model = (ScheduleInfo) info;
					
					// 消息内容
					ScheduleMessage mq = new ScheduleMessage();
					//mq.setStatus((byte) 1);
					mq.setType(mType);
					mq.setRemarkId(info_model.getId());
					ScheduleMessage message =scheduleMessageDao.selectOne(mq);
					
					if( message!= null){
						
						if(message.getStatus().intValue() == 1){
							//主从判断
							Long info_pid = info_model.getParentId();
							if(info_pid != null){
								ScheduleMessageUser scheduleMessageUser = new ScheduleMessageUser();
								scheduleMessageUser.setMid(message.getId());
								scheduleMessageUser.setUid(info_model.getCreatedId());
								scheduleMessageUserDao.delete(scheduleMessageUser);
								
								
								//通知消息 ：  删除消息
								Map<String, List<Long>> delMap = new HashMap<String, List<Long>>();
								
								List<Long> mids = new ArrayList<Long>();
								mids.add(message.getId());
								
								List<Long> muids = new ArrayList<Long>();
								muids.add(info_model.getCreatedId());
								
								delMap.put(SchedulePushMessTask.DEL_MAP_KEY_MID, mids);
								delMap.put(SchedulePushMessTask.DEL_MAP_KEY_MUID, muids);
								
								schedulePushMessTask.setHasDeled(delMap);
							}else{
								scheduleMessageDao.deleteById(message.getId());
								
								ScheduleMessageUser scheduleMessageUser = new ScheduleMessageUser();
								scheduleMessageUser.setMid(message.getId());
								scheduleMessageUserDao.delete(scheduleMessageUser);
								
								
								//通知消息 ：  删除消息
								Map<String, List<Long>> delMap = new HashMap<String, List<Long>>();
								
								List<Long> mids = new ArrayList<Long>();
								mids.add(message.getId());
								
								delMap.put(SchedulePushMessTask.DEL_MAP_KEY_MID, mids);
								
								schedulePushMessTask.setHasDeled(delMap);
							}
							
							//新增消息
							ScheduleMessage messageAdd = messageGenerator.process(info);
							Long mid = scheduleMessageDao.insert(messageAdd);
							
							List<ScheduleMessageUser> toInserts = new ArrayList<ScheduleMessageUser>();
							for(ScheduleMessageUser toU : messageAdd.getToUsers()){
								toU.setIsSend((byte) 0);
								toU.setIsRead((byte) 0);
								toU.setIsDel((byte) 0);
								toU.setMid(mid);
								
								toInserts.add(toU);
							}
							scheduleMessageUserDao.insertInBatch(toInserts);
							
							//通知消息 ：  已经添加新的消息
							//if(DateUtil.checkLongIsToday(message.getSendTime())){
								messageAdd.setToUsers(toInserts);
								schedulePushMessTask.setHasSaved(messageAdd);
							//}
						}
						
					}else{
						//新增消息
						ScheduleMessage messageAdd = messageGenerator.process(info);
						Long mid = scheduleMessageDao.insert(messageAdd);
						
						List<ScheduleMessageUser> toInserts = new ArrayList<ScheduleMessageUser>();
						for(ScheduleMessageUser toU : messageAdd.getToUsers()){
							toU.setIsSend((byte) 0);
							toU.setIsRead((byte) 0);
							toU.setIsDel((byte) 0);
							toU.setMid(mid);
							
							toInserts.add(toU);
						}
						scheduleMessageUserDao.insertInBatch(toInserts);
						
						//通知消息 ：  已经添加新的消息
						//if(DateUtil.checkLongIsToday(message.getSendTime())){
							messageAdd.setToUsers(toInserts);
							schedulePushMessTask.setHasSaved(messageAdd);
						//}
					}
					
				}
*/
			}
		});
		
	}
	
	
}
