package org.aask.event;

import org.aask.bean.MockRequest;
import org.springframework.context.ApplicationEvent;

public class RecordRequestEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;

	private MockRequest request;

	public RecordRequestEvent(Object source, MockRequest request) {
		super(source);
		this.request = request;
	}

	public MockRequest getRequest() {
		return request;
	}

	public void setRequest(MockRequest request) {
		this.request = request;
	}

}
