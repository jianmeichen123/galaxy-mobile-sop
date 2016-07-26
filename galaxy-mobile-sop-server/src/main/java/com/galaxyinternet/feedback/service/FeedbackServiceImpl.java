package com.galaxyinternet.feedback.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.galaxyinternet.dao.feedback.FeedbackDao;
import com.galaxyinternet.framework.core.dao.BaseDao;
import com.galaxyinternet.framework.core.service.impl.BaseServiceImpl;
import com.galaxyinternet.model.feedback.Feedback;
import com.galaxyinternet.service.FeedbackService;

@Service
public class FeedbackServiceImpl extends BaseServiceImpl<Feedback> implements  FeedbackService{
	
	@Autowired
	private FeedbackDao feedbackDao;
	
	
	
	@Override
	protected BaseDao<Feedback, Long> getBaseDao() {
		return this.feedbackDao;
	}
	
	/**
	 * 插入反馈
	 */
	@Override
	public Long insertFeedback(Feedback feedback) {
		return feedbackDao.insert(feedback);
	}
	
}
