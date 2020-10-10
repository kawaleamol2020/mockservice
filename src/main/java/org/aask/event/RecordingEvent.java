package org.aask.event;

import org.aask.bean.MockRequestResponseWrapper;
import org.springframework.context.ApplicationEvent;

public class RecordingEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;

	private MockRequestResponseWrapper requestResponseWrapper;

	public RecordingEvent(Object source, MockRequestResponseWrapper requestResponseWrapper) {
		super(source);
		this.requestResponseWrapper = requestResponseWrapper;
	}

	public MockRequestResponseWrapper getRequestResponseWrapper() {
		return requestResponseWrapper;
	}

	public void setRequestResponseWrapper(MockRequestResponseWrapper requestResponseWrapper) {
		this.requestResponseWrapper = requestResponseWrapper;
	}

}