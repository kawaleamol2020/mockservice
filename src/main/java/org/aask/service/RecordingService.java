package org.aask.service;

import org.aask.bean.MockRequest;
import org.aask.bean.MockResponse;


public interface RecordingService {
	
	public void recordRequestAndResponse(MockRequest request,MockResponse response);
	
	public void recordRequestAndErrorResponse(MockRequest request, Throwable exception);
}
