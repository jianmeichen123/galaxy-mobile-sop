package com.galaxyinternet.rili.mesSchedule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.galaxyinternet.framework.cache.Cache;
import com.galaxyinternet.framework.core.exception.BusinessException;
import com.galaxyinternet.framework.core.thread.GalaxyThreadPool;
import com.galaxyinternet.rili.dao.ScheduleMessageDao;
import com.galaxyinternet.rili.dao.ScheduleMessageUserDao;
import com.galaxyinternet.rili.model.ScheduleMessage;
import com.galaxyinternet.rili.model.ScheduleMessageUser;
import com.galaxyinternet.rili.service.ScheduleMessageService;
import com.galaxyinternet.rili.util.UtilOper;
import com.galaxyinternet.scheduling.BaseGalaxyTask;
import com.tencent.xinge.XGPush;


/**
 * 每 30 秒调用，发送消息
 */
@Service(value = "com.galaxyinternet.rili.mesSchedule.SchedulePushMessTask")
public class SchedulePushMessTask extends BaseGalaxyTask { //extends BaseGalaxyTask  implements InitializingBean

	private final static Logger logger = LoggerFactory.getLogger(SchedulePushMessTask.class);
	
	
	
	/**
	 * 定义跳出 runForMess . for 的超时时间， 默认0秒
	 */
	private static final long TO_BREAK_SENDFOR_TIME = (long)  0 * 1000;
	
	/**
	 * 定义消息 可以延后发送的时间， 00:1分钟发送 + 延后 5分钟
	 */
	private static final long TO_LAZY_TIME_BY_MESSAGE = (long) 5 * 60 * 1000;
	
	
	/**
	 * 服务 是否正在在检测   
	 */
	private static boolean hasRunedToCheck = false;
	/**
	 * 等待服务 运行时间 5毫秒
	 */
	private static long waitServerTime = 5;
	
	
	/**
	 * mess删除 - map.key
	 */
	public static final String DEL_MAP_KEY_MID = "mid";
	public static final String DEL_MAP_KEY_MUID = "muid";
	
	
	
	@Autowired
	Cache cache;
	@Autowired
	private ScheduleMessageService scheduleMessageService;
	@Autowired
	private ScheduleMessageDao scheduleMessageDao;
	@Autowired
	private ScheduleMessageUserDao scheduleMessageUserDao;
	
	
	
	
	/**
	 * 是否有新增处理 外部调用， 赋值
	 */
	@SuppressWarnings("unchecked")
	public synchronized void setHasSaved(ScheduleMessage addMess) {
		while (SchedulePushMessTask.hasRunedToCheck) { // 服务是否正在处理
			try {
				Thread.sleep(SchedulePushMessTask.waitServerTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		SchedulePushMessTask.hasRunedToCheck = true;
		try {
			List<ScheduleMessage> sMessList = null;
			Object ms = cache.get(SchedulePushInitTask.CACHE_KEY_MESSAGE_TODAY_PUSH);
			if(ms != null){
				sMessList = (List<ScheduleMessage>) ms;
				sMessList.add(addMess);
				
				Collections.sort(sMessList, new Comparator<ScheduleMessage>() {
					public int compare(ScheduleMessage arg0, ScheduleMessage arg1) {
						return (int) (arg0.getSendTime().longValue() - arg1.getSendTime().longValue());
					}
				});
				
				cache.set(SchedulePushInitTask.CACHE_KEY_MESSAGE_TODAY_PUSH, sMessList);
			}
		}finally{
			SchedulePushMessTask.hasRunedToCheck = false;
		}
		
	}
	
	
	
	/**
	 * 是否有删除处理 
	 * 外部调用， 赋值
	 */
	@SuppressWarnings("unchecked")
	public synchronized void setHasDeled(Map<String, List<Long>> delMap) {
		while (SchedulePushMessTask.hasRunedToCheck) { // 服务是否正在处理
			try {
				Thread.sleep(SchedulePushMessTask.waitServerTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		SchedulePushMessTask.hasRunedToCheck = true;
		
		try {
			List<ScheduleMessage> sMessList = null;
			Object ms = cache.get(SchedulePushInitTask.CACHE_KEY_MESSAGE_TODAY_PUSH);
			if(ms != null){
				sMessList = (List<ScheduleMessage>) ms;

				List<Long> mids = delMap.get(SchedulePushMessTask.DEL_MAP_KEY_MID);
				Long mid = mids.get(0);
				List<Long> muids = delMap.get(SchedulePushMessTask.DEL_MAP_KEY_MUID);

				for (ScheduleMessage tempM : sMessList) {
					if (tempM.getId().longValue() == mid.longValue()) {
						
						if(muids != null && !muids.isEmpty()) {
							for(Long muid : muids){
								for (ScheduleMessageUser tempU : tempM.getToUsers()) {
									if (tempU.getUid().longValue() == muid.longValue()) {
										tempM.getToUsers().remove(tempU);
										break;
									}
								}
							}
						}else{
							sMessList.remove(tempM);
						}
						
						break;
					}
				}
				
				cache.set(SchedulePushInitTask.CACHE_KEY_MESSAGE_TODAY_PUSH, sMessList);
			}
		} finally{
			SchedulePushMessTask.hasRunedToCheck = false;
		}
		
	}

	
	
	
	/**
	 * 每 30 秒调用，发送消息
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void executeInteral() throws BusinessException {
		
		try {
			Date a = new Date();
			throw new Exception("schedule - send - mess -  beging - time " + a);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		while (SchedulePushMessTask.hasRunedToCheck) { // 服务是否正在处理
			try {
				Thread.sleep(SchedulePushMessTask.waitServerTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		SchedulePushMessTask.hasRunedToCheck = true;
		
		try {
			
			//初始化补充
			try {
				if(!SchedulePushInitTask.initTaskHasRuned){
					List<ScheduleMessage> initList = scheduleMessageService.queryTodayMessToSend();
					
					SchedulePushInitTask.initTaskHasRuned = true;
					
					cache.set(SchedulePushInitTask.CACHE_KEY_MESSAGE_TODAY_PUSH, initList);
					/*if (initList != null && !initList.isEmpty()) {
						
						List<ScheduleMessage> sMessList = null;
						Object ms = cache.get(SchedulePushInitTask.CACHE_KEY_MESSAGE_TODAY_PUSH);
						if(ms != null){
							sMessList = (List<ScheduleMessage>) ms;
							sMessList.addAll(initList);
							
							Collections.sort(sMessList, new Comparator<ScheduleMessage>() {
								public int compare(ScheduleMessage arg0, ScheduleMessage arg1) {
									return (int) (arg0.getSendTime().longValue() - arg1.getSendTime().longValue());
								}
							});
							
							cache.set(SchedulePushInitTask.CACHE_KEY_MESSAGE_TODAY_PUSH, sMessList);
						}else{
							cache.set(SchedulePushInitTask.CACHE_KEY_MESSAGE_TODAY_PUSH, initList);
						}
					}*/
				}
			} catch (Exception e) {}
			
			
			long current = System.currentTimeMillis();
			
			List<ScheduleMessage> sMessList = null;
			Object ms = cache.get(SchedulePushInitTask.CACHE_KEY_MESSAGE_TODAY_PUSH);
			if(ms != null){
				sMessList = (List<ScheduleMessage>) ms;
				
				List<ScheduleMessage> thisTimeToSend = new ArrayList<ScheduleMessage>();
				for (int i = 0; i < sMessList.size();) {
					
					ScheduleMessage mess = sMessList.get(i);
					
					if(mess.getSendTime().longValue() - current <= SchedulePushMessTask.TO_BREAK_SENDFOR_TIME){
						thisTimeToSend.add(mess);
						sMessList.remove(i);
					}else{
						break;
					}
				}
				
				if(!thisTimeToSend.isEmpty()){
					final List<ScheduleMessage> toSend = thisTimeToSend;
					cache.set(SchedulePushInitTask.CACHE_KEY_MESSAGE_TODAY_PUSH, sMessList);
					
					GalaxyThreadPool.getExecutorService().execute(new Runnable() {
						public void run() {
							runForMess(toSend);
						}
					});
					/*new Thread(){ 
						public void run() {
							runForMess(toSend);
						}
					}.start();*/
				}
			}
		}finally{
			SchedulePushMessTask.hasRunedToCheck = false;
		}
		
	}

	
	
	//发送消息
	public void runForMess(List<ScheduleMessage> thisTimeToSend) {

		final XGPush xinge = XGPush.getInstance();
		
		
		for(ScheduleMessage tempMess : thisTimeToSend){
			
			
			
			try {
				Date a = new Date();
				throw new Exception("for - to - send - mess -  beging - time " + a);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			final ScheduleMessage mess = tempMess;
			
			// 发送时间  《  当前时间+lazy tm  跳过不发
			boolean toContinue = false;
			if (mess.getSendTime().longValue() < (System.currentTimeMillis() - SchedulePushMessTask.TO_LAZY_TIME_BY_MESSAGE)) {
				toContinue = true;
			}
			
			// 统一修改 消息内容可用
			mess.setStatus((byte) 0);
			scheduleMessageDao.updateById(mess);
			if (toContinue || mess.getToUsers()==null || mess.getToUsers().isEmpty()) {
				continue;
			}

			
			try {
				Date a = new Date();
				throw new Exception("thread - to - send - mess -  beging - time " + a);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 消息推送到移动端
			GalaxyThreadPool.getExecutorService().execute(new Runnable() {
				
				@Override
				public void run() {
					
					// 消息标题
					String mesTitle = null;
					if (mess.getType().equals("1.3")) {
						mesTitle = "日程提醒";
					}

					// 消息接收人id
					List<String> uIds = new ArrayList<String>();

					List<ScheduleMessageUser> toUsers = mess.getToUsers();
					for (ScheduleMessageUser tempU : toUsers) {
						uIds.add(String.valueOf(tempU.getUid()));
					}
					
					// 消息内容
					String conts = UtilOper.getMessContent(mess);
					
					// 消息发送
					org.json.JSONObject result = xinge.pushAccountList(uIds, mesTitle, conts);
					
					
					try {
						Date a = new Date();
						throw new Exception("thread - to - send - mess -  end - time " + a);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					
					// 消息发送结果
					if (result != null) {
						String backStr = result.toString();
						String iosmarkV = backStr.substring(backStr.indexOf("ret_code\":") + 10,
								backStr.indexOf("ret_code\":") + 11);
						String andriodmarkV = backStr.substring(backStr.lastIndexOf("ret_code\":") + 10,
								backStr.lastIndexOf("ret_code\":") + 11);
						if (!iosmarkV.equals("0") || !andriodmarkV.equals("0")) {
							logger.error("SchedulePushMessTask . xingge 推送失败 " + backStr);
						}else {
							ScheduleMessageUser toU = new ScheduleMessageUser();
							toU.setMid(mess.getId());
							toU.setIsSend((byte) 1);
							scheduleMessageUserDao.updateByIdSelective(toU);
						}
					}
				}
			});
		}
	}


}


