package com.galaxyinternet.rili.dao;
import org.springframework.stereotype.Repository;

import com.galaxyinternet.framework.core.dao.impl.BaseDaoImpl;
import com.galaxyinternet.rili.model.ScheduleContacts;

@Repository("ScheduleContactsDao")
public class ScheduleContactsDaoImpl extends BaseDaoImpl<ScheduleContacts, Long> implements ScheduleContactsDao{

}
	