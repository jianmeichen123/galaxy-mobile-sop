package com.galaxyinternet.rili.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.galaxyinternet.framework.core.exception.BusinessException;
import com.galaxyinternet.framework.core.thread.GalaxyThreadPool;
import com.galaxyinternet.rili.dao.ScheduleMessageDao;
import com.galaxyinternet.rili.dao.ScheduleMessageUserDao;
import com.galaxyinternet.rili.model.ScheduleMessage;
import com.galaxyinternet.rili.model.ScheduleMessageUser;
import com.galaxyinternet.rili.service.ScheduleMessageService;
import com.galaxyinternet.scheduling.BaseGalaxyTask;
import com.tencent.xinge.XGPush;


@Component(value="com.galaxyinternet.rili.util.SchedulePushMessTask")
public class SchedulePushMessTask extends BaseGalaxyTask{

	
	
	
	
	//今日需要发送的消息  存入redies
	//public static final String CACHE_SCHEDULE_MESS_TODAY_TOSEND = "SCHEDULE_MESS_TODAY_TOSEND";   
	
	
	//定义消息 可以延后发送的时间，  00:1分钟发送  + 延后 5分钟  
	public static final long TO_LAZY_TIME_BY_MESSAGE = (long) 5 * 60 * 1000 ;
	
	
	//定义消息推送线程的name
	public static final String THREAD_NAME_SCHEDULE_MESS_TODAY_TOSEND = "THREAD_NAME_SCHEDULE_MESS_TODAY_TOSEND";
	
	
	//今日需要发送的消息  存入map
	private static LinkedHashMap<Long,ScheduleMessage> mid_mess_map = new LinkedHashMap<Long,ScheduleMessage>(); // mid : message
	
	public static LinkedHashMap<Long, ScheduleMessage> getMid_mess_map() {
		return mid_mess_map;
	}

	public static void setMid_mess_map(LinkedHashMap<Long, ScheduleMessage> mid_mess_map) {
		SchedulePushMessTask.mid_mess_map = mid_mess_map;
	}

	
	
	
	@Autowired
	private ScheduleMessageService scheduleMessageService;
	
	@Autowired
	private ScheduleMessageDao scheduleMessageDao;
	@Autowired
	private ScheduleMessageUserDao scheduleMessageUserDao;
	
	
	
	
	/**
	 *  每天 凌晨   00:01 点调用 ， 查询出今天需要推送的消息, 然后循环
	 */
	@Override
	protected void executeInteral() throws BusinessException {
		System.err.println("------------------------------------------  task begin --------------------------------------");
		Thread sendUtil = null ;
		
		if(SchedulePushMessTask.mid_mess_map !=null && !SchedulePushMessTask.mid_mess_map.isEmpty()){
			Iterator<Thread> iterator = Thread.getAllStackTraces().keySet().iterator();
			
			int i = 0;
			while(iterator.hasNext()){
				i += 1;
				Thread t = (Thread)iterator.next();
				if(t.getName().equals(SchedulePushMessTask.THREAD_NAME_SCHEDULE_MESS_TODAY_TOSEND)){
					sendUtil = t ;
					System.err.println("------------------------------------------ "+i+" --------------------------------------");
					break ;
				}
			}
			
			if(sendUtil!=null){
				sendUtil.interrupt();
			}
		}
		
		SchedulePushMessTask.mid_mess_map = scheduleMessageService.queryTodayMessToSend();
		if(SchedulePushMessTask.mid_mess_map != null && !SchedulePushMessTask.mid_mess_map.isEmpty()){
			sendUtil = new Thread(new ToSendMessage(scheduleMessageDao,scheduleMessageUserDao),SchedulePushMessTask.THREAD_NAME_SCHEDULE_MESS_TODAY_TOSEND);
			sendUtil.start();
			
		}
		
		System.err.println("------------------------------------------  task end --------------------------------------");
	}
}


class ToSendMessage implements Runnable{

	private final static Logger logger = LoggerFactory.getLogger(ToSendMessage.class);
	
	private ScheduleMessageDao scheduleMessageDao;
	private ScheduleMessageUserDao scheduleMessageUserDao;
	
	public ToSendMessage(ScheduleMessageDao scheduleMessageDao,ScheduleMessageUserDao scheduleMessageUserDao){
		this.scheduleMessageDao = scheduleMessageDao;
		this.scheduleMessageUserDao = scheduleMessageUserDao;
	}
	
	
	@Override
	public void run() {
		System.err.println("------------------------------------------ run start --------------------------------------");
		
		//Thread sendUtil = new Thread(new ToSendMessage(),SchedulePushMessTask.THREAD_NAME_SCHEDULE_MESS_TODAY_TOSEND);
		final XGPush xinge = XGPush.getInstance();

		
		//Iterator遍历LinkedHashMap
		Iterator<Map.Entry<Long,ScheduleMessage>> iterator = SchedulePushMessTask.getMid_mess_map().entrySet().iterator();
		while (iterator.hasNext()) {
			
			boolean toContinue = false;
			
			Map.Entry<Long,ScheduleMessage> entry = iterator.next();
			final ScheduleMessage mess = entry.getValue();
			
			//Thread.sleep() 等待
			long current = System.currentTimeMillis();
			if(mess.getSendTime().longValue() -(long) 2 > current){   //发送时间  》 当前时间    等待
				try {
					Thread.sleep(mess.getSendTime().longValue() -(long) 2 - current);
				} catch (InterruptedException e) {
					logger.error("ToSendMessage . run Interrupted", e.getMessage());
				}
			}else if(mess.getSendTime().longValue() < (current - SchedulePushMessTask.TO_LAZY_TIME_BY_MESSAGE)){  //发送时间  《 当前时间+ lazy tm  跳过不发
				toContinue = true;
			}
			
			
			mess.setStatus((byte) 0);
			scheduleMessageDao.updateById(mess);
			if(toContinue){
				toContinue = false;
				continue;
			}
			
			
			GalaxyThreadPool.getExecutorService().execute(new Runnable() {
				@Override
				public void run() {
					String mesTitle = null;
					if(mess.getType().equals("1.3")){
						mesTitle = "日程提醒";
					}
					
					List<String> uIds = new ArrayList<String>();
					
					List<ScheduleMessageUser> toUsers = mess.getToUsers();
					for(ScheduleMessageUser tempU : toUsers){
						uIds.add(String.valueOf(tempU.getUid()));
					}
					
					String conts =  UtilOper.getMessContent(mess);
					org.json.JSONObject result = xinge.pushAccountList(uIds, mesTitle, conts);
					if(result!=null){
						String backStr = result.toString();
						String iosmarkV = backStr.substring(backStr.indexOf("ret_code\":")+10, backStr.indexOf("ret_code\":")+11);
						String andriodmarkV = backStr.substring(backStr.lastIndexOf("ret_code\":")+10, backStr.lastIndexOf("ret_code\":")+11);
						if(!iosmarkV.equals("0") || !andriodmarkV.equals("0")){
							logger.error("xingge 推送失败 "+backStr);
						}
					}
				}
			});
			
			
			if(!iterator.hasNext()){
				SchedulePushMessTask.setMid_mess_map(null);
			}
		}
		
		System.err.println("------------------------------------------ run end --------------------------------------");
	}
	
	
}




