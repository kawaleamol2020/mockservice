package org.aask.listener;

import org.aask.bean.MockRequestResponseWrapper;
import org.aask.constant.MockConstant;
import org.aask.service.RecordingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class MockServiceMessageListener {

	@Autowired
	RecordingService recordingService;
	
	@JmsListener(destination = MockConstant.RECORDING_QUEUE, containerFactory = "connectionFactory")
	public void receiveMessage(@Payload MockRequestResponseWrapper requestResponseWrapper){
		
		if(!requestResponseWrapper.isErrorResponse()){
			recordingService.recordRequestAndResponse(requestResponseWrapper.getMockRequest(), requestResponseWrapper.getMockResponse());
		} else {
			recordingService.recordRequestAndErrorResponse(requestResponseWrapper.getMockRequest(), requestResponseWrapper.getMockException());
			
		}
	}
}
