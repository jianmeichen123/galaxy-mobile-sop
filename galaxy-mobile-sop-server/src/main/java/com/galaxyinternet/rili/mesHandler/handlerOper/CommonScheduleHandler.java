package com.galaxyinternet.rili.mesHandler.handlerOper;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.galaxyinternet.rili.mesHandler.ScheduleMessageHandler;
import com.galaxyinternet.rili.model.ScheduleInfo;
import com.galaxyinternet.rili.model.ScheduleMessage;
import com.galaxyinternet.rili.model.ScheduleMessageUser;
import com.galaxyinternet.rili.util.UtilOper;


@Component
public class CommonScheduleHandler implements ScheduleMessageHandler
{
	private static final long serialVersionUID = 1L;
	final Logger logger = LoggerFactory.getLogger(CommonScheduleHandler.class);
	
	
	// 普通日程 （ 其它日程）
	public static final String COMMON_SCHEDULE = "common_schedule";
	


	public boolean support(Object info) {
		ScheduleInfo message = (ScheduleInfo) info;
		return message != null && message.getMessageType()!= null && message.getMessageType().equals(CommonScheduleHandler.COMMON_SCHEDULE);
	}

	
	
	// 您有一个日程将于①明日（2017-12-12） ②3:00开始，日程名称“③XXXXX”。
	public void handle(ScheduleMessage message,Object info) {
		
		ScheduleInfo model = (ScheduleInfo) info;
		
		String startTime = model.getStartTime();
		byte isAllday = model.getIsAllday()== null ? (byte) 0 : (byte) 1; //是否全天 0:否 1:是
		Long dictId = model.getWakeupId();
		
		
		//消息内容
		message.setCreatedUid(model.getCreatedId());
		message.setCreatedUname(model.getUserName());
		
		
		StringBuffer content = new StringBuffer();
		content.append("您有一个日程将于 ");
		content.append("<time>").append(startTime).append("</time>");
		content.append(" 开始，");
		content.append("日程名称\"").append("<name>").append(model.getName()).append("</name>\"。");
		content.append("<id>").append(model.getId()).append("</id>");
		content.append("<type>schedule</type>");
		
		message.setContent(content.toString());
		
		
		//消息推送时间
		try {
			message.setSendTime(UtilOper.getSendTimeBy(startTime, isAllday, dictId));
		} catch (ParseException e) {
			message.setSendTime(null);
			logger.error("CommonScheduleHandler . handle 异常 ",e.getMessage());
		}
		
		//消息接收人
		List<ScheduleMessageUser> toUsers = new ArrayList<ScheduleMessageUser>();
		ScheduleMessageUser toU = new ScheduleMessageUser();
		toU.setUid(message.getCreatedUid());
		toU.setUname(message.getCreatedUname());
		toUsers.add(toU);
		
		message.setToUsers(toUsers);
	}
	
	

}

