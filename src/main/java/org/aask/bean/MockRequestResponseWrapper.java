package org.aask.bean;

import java.io.Serializable;

public class MockRequestResponseWrapper implements Serializable {

	private static final long serialVersionUID = 2019062710L;

	private boolean isErrorResponse = false;
	
	private MockRequest mockRequest;
	
	private MockResponse mockResponse;
	
	private Throwable mockException;

	public boolean isErrorResponse() {
		return isErrorResponse;
	}

	public void setErrorResponse(boolean isErrorResponse) {
		this.isErrorResponse = isErrorResponse;
	}

	public MockRequest getMockRequest() {
		return mockRequest;
	}

	public void setMockRequest(MockRequest mockRequest) {
		this.mockRequest = mockRequest;
	}

	public MockResponse getMockResponse() {
		return mockResponse;
	}

	public void setMockResponse(MockResponse mockResponse) {
		this.mockResponse = mockResponse;
	}

	public Throwable getMockException() {
		return mockException;
	}

	public void setMockError(Throwable mockException) {
		this.mockException = mockException;
	}
}
