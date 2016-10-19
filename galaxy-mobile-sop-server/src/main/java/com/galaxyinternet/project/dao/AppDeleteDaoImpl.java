package com.galaxyinternet.project.dao;

import org.springframework.stereotype.Repository;

import com.galaxyinternet.dao.project.AppSignDao;
import com.galaxyinternet.framework.core.dao.impl.BaseDaoImpl;
import com.galaxyinternet.model.project.AppSign;


@Repository("appSignDao")
public class AppDeleteDaoImpl extends BaseDaoImpl<AppSign, Long> implements AppSignDao {


}