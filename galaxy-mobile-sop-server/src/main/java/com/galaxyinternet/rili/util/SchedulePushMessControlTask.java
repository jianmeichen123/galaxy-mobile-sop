package com.galaxyinternet.rili.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.galaxyinternet.framework.core.exception.BusinessException;
import com.galaxyinternet.rili.service.ScheduleMessageService;
import com.galaxyinternet.scheduling.BaseGalaxyTask;



@Component(value="com.galaxyinternet.rili.util.SchedulePushMessControlTask")
public class SchedulePushMessControlTask extends BaseGalaxyTask{

	
	//@Autowired
	//private ScheduleMessageService scheduleMessageService;
	
	@Autowired
	private SchedulePushMessTask schedulePushMessTask;
	
	
	
	
	// 是否在校验
	private static boolean hasChecked = false;
	
	// 等待校验时间（ executeInteral 运行时间）
	private static long waitTime = 20;
	
	
	
	
	// 是否改变
	private static boolean hasChanged = false;
	public static boolean isHasChanged() {
		return hasChanged;
	}
	public synchronized static void setHasChanged(boolean hasChanged) {
		
		while(hasChecked){
			try {
				Thread.sleep(SchedulePushMessControlTask.waitTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		SchedulePushMessControlTask.hasChanged = hasChanged;
	}




	/**
	 * 每2分钟调用 ：
	 * 		1.检查当天需要发送的消息是否改变
	 * 		2.如果改变，调用消息处理方法
	 */
	@Override
	protected void executeInteral() throws BusinessException {
		SchedulePushMessControlTask.hasChecked = true;
		
		if(SchedulePushMessControlTask.hasChanged){
			SchedulePushMessControlTask.hasChanged = false;
			schedulePushMessTask.executeInteral();
		}
		
		SchedulePushMessControlTask.hasChecked = false;
	}

	
	

}
