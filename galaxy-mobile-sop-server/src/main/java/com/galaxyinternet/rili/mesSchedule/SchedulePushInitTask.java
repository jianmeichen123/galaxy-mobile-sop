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


@Service(value = "com.galaxyinternet.rili.mesSchedule.SchedulePushInitTask")
public class SchedulePushInitTask extends BaseGalaxyTask { //extends BaseGalaxyTask  implements InitializingBean

	private final static Logger logger = LoggerFactory.getLogger(SchedulePushInitTask.class);

	public static final String CACHE_KEY_MESSAGE_TODAY_PUSH = "message_for_today_to_push"; 
	
	@Autowired
	private ScheduleMessageService scheduleMessageService;
	
	@Autowired
	Cache cache;
	
	
	/**
	 * 服务是否运行过
	 */
	public static boolean initTaskHasRuned = false;
	
	
	/**
	 * 每天 凌晨 00:01 点调用 ， 查询出今天需要推送的消息
	 */
	@Override
	protected void executeInteral() throws BusinessException {
		
		List<ScheduleMessage> sMessList = scheduleMessageService.queryTodayMessToSend();
		
		if(!SchedulePushInitTask.initTaskHasRuned) SchedulePushInitTask.initTaskHasRuned = true;
		
		if (sMessList != null && !sMessList.isEmpty()) {
			cache.set(SchedulePushInitTask.CACHE_KEY_MESSAGE_TODAY_PUSH, sMessList);
		}
	}

}
