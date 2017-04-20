package com.galaxyinternet.rili.mesHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.OrderComparator;
import org.springframework.stereotype.Component;

import com.galaxyinternet.rili.model.ScheduleMessage;


@Component
public class ScheduleMessageGenerator implements InitializingBean,ApplicationContextAware
{
	private ApplicationContext context;
	private List<ScheduleMessageHandler> handlers;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
	{
		this.context = applicationContext;
	}

	
	@Override
	public void afterPropertiesSet() throws Exception
	{
		 Map<String, ScheduleMessageHandler> map = BeanFactoryUtils.beansOfTypeIncludingAncestors(context, ScheduleMessageHandler.class, true, false);
		 if(map != null)
		 {
			 handlers = new ArrayList<ScheduleMessageHandler>(map.values());
			 //OrderComparator.sort(handlers);
		 }
		
	}

	public ScheduleMessage process(Object info)
	{
		if(handlers != null) {
			
			for(ScheduleMessageHandler handler : handlers) {
				
				if(handler.support(info)) {
					
					ScheduleMessage message = new ScheduleMessage();
					
					message.setCategory((byte) 0);  // 0:操作消息  1:系统消息
					message.setType("1.3");      // 消息类型  日程(1:会议 2:拜访 3:其它)
					message.setStatus((byte) 1);    // 0:可用 1:禁用
					
					handler.handle(message,info);
					return message;
				}
			}
		}
		return null;
	}
	
	
	/*public OperationMessage generate(OperationType type, User user, Map<String, Object> map)
	{
		
		
		return null;
	}*/
}
