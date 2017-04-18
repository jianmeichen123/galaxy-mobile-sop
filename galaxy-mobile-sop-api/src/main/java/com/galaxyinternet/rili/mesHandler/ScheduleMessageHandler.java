package com.galaxyinternet.rili.mesHandler;

import java.io.Serializable;

import com.galaxyinternet.rili.model.ScheduleMessage;



public interface ScheduleMessageHandler extends Serializable
{
	public boolean support(Object info);
	public void handle(ScheduleMessage message,Object info);
}
