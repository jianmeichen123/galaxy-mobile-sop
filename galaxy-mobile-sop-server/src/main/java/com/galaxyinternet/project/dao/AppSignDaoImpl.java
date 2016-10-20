package com.galaxyinternet.project.dao;

import org.springframework.stereotype.Repository;

import com.galaxyinternet.dao.project.AppDeleteDao;
import com.galaxyinternet.framework.core.dao.impl.BaseDaoImpl;
import com.galaxyinternet.model.project.AppDelete;


@Repository("appDeleteDao")
public class AppSignDaoImpl extends BaseDaoImpl<AppDelete, Long> implements AppDeleteDao {


}