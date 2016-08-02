package com.galaxyinternet.feedback.dao;

import org.springframework.stereotype.Repository;

import com.galaxyinternet.dao.feedback.FeedbackDao;
import com.galaxyinternet.framework.core.dao.impl.BaseDaoImpl;
import com.galaxyinternet.model.feedback.Feedback;
@Repository
public class FeedbackDaoImpl extends BaseDaoImpl<Feedback, Long>implements FeedbackDao {


}
