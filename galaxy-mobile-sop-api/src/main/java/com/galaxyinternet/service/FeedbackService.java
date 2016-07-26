package com.galaxyinternet.service;

import com.galaxyinternet.framework.core.service.BaseService;
import com.galaxyinternet.model.feedback.Feedback;

public interface FeedbackService extends BaseService<Feedback> {
	
	Long insertFeedback(Feedback feedback);
	
}
