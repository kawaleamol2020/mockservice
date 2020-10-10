package org.aask.event;

import org.aask.bean.MockRequest;
import org.springframework.context.ApplicationEvent;

public class RecordErrorResponseEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;

	private MockRequest request;
	private Throwable exception;

	public RecordErrorResponseEvent(Object source, MockRequest request,Throwable exception) {
		super(source);
		this.request = request;
		this.exception = exception;
	}

	public MockRequest getRequest() {
		return request;
	}

	public void setRequest(MockRequest request) {
		this.request = request;
	}

	public Throwable getException() {
		return exception;
	}

	public void setException(Throwable exception) {
		this.exception = exception;
	}
}
