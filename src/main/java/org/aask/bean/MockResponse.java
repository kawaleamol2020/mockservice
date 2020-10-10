package org.aask.bean;

import java.io.Serializable;
import java.util.Map;

public class MockResponse implements Serializable{

	private static final long serialVersionUID = 1L;

	private int statusCode;

	private String message;

	private String responseBody;

	private Map<String, String> headers;

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getResponseBody() {
		return responseBody;
	}

	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	@Override
	public String toString() {
		return "MockResponse [statusCode=" + statusCode + ", message=" + message + ", responseBody=" + responseBody
				+ ", headers=" + headers + "]";
	}

}
