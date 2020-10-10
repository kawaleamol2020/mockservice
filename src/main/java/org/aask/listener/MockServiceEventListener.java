package org.aask.listener;

import org.aask.constant.MockConstant;
import org.aask.event.RecordErrorResponseEvent;
import org.aask.event.RecordResponseEvent;
import org.aask.event.RecordingEvent;
import org.aask.service.RecordingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class MockServiceEventListener {

	Logger logger = LoggerFactory.getLogger(MockServiceEventListener.class);
	
	@Autowired
	RecordingService recordingService;
	
	@Autowired
    private JmsTemplate jmsTemplate;
	
	@EventListener
	@Async
	@Order(10)
	public void recordMockData(RecordingEvent recordingEvent) {
		
		logger.info("recordMockData start");
		
		jmsTemplate.convertAndSend(MockConstant.RECORDING_QUEUE,recordingEvent.getRequestResponseWrapper());
		
		logger.info("recordMockData end");
	}
	
	@EventListener
	@Async
	@Order(10)
	public void recordMockResponseData(RecordResponseEvent recordResponseEvent) {
		
		logger.info(" recordMockResponseData start");
		
		recordingService.recordRequestAndResponse(recordResponseEvent.getRequest(), recordResponseEvent.getResponse());
		
		logger.info(" recordMockResponseData end");
	}
	
	@EventListener
	@Async
	@Order(10)
	public void recordMockErrorResponseData(RecordErrorResponseEvent recordErrorResponseEvent) {
		
		logger.info(" recordMockErrorResponseData start");
		
		recordingService.recordRequestAndErrorResponse(recordErrorResponseEvent.getRequest(), recordErrorResponseEvent.getException());
		
		logger.info(" recordMockErrorResponseData end");
	}
}
