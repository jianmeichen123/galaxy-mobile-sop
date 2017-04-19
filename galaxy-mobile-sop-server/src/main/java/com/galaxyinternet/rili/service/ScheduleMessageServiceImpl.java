package com.galaxyinternet.rili.service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.galaxyinternet.framework.core.dao.BaseDao;
import com.galaxyinternet.framework.core.model.PageRequest;
import com.galaxyinternet.framework.core.service.impl.BaseServiceImpl;
import com.galaxyinternet.framework.core.thread.GalaxyThreadPool;
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

	
	
	@Override
	public List<ScheduleMessageUser> queryAndConvertList(ScheduleMessageUser query, PageRequest pageable) {
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
