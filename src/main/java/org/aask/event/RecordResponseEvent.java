package org.aask.event;

import org.aask.bean.MockRequest;
import org.aask.bean.MockResponse;
import org.springframework.context.ApplicationEvent;

public class RecordResponseEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;

	private MockRequest request;
	private MockResponse response;

	public RecordResponseEvent(Object source, MockRequest request, MockResponse response) {
		super(source);
		this.request = request;
		this.response = response;
	}

	public MockRequest getRequest() {
		return request;
	}

	public void setRequest(MockRequest request) {
		this.request = request;
	}

	public MockResponse getResponse() {
		return response;
	}

	public void setResponse(MockResponse response) {
		this.response = response;
	}
}
