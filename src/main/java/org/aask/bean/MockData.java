package org.aask.bean;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.validation.constraints.NotBlank;

public class MockData implements Serializable {

	private static final long serialVersionUID = 1L;

	private String mockId;

	private String requestData;

	@NotBlank(message = "Response Data required")
	private String responseData;

	private int statusCode;

	private Timestamp insertedDate;

	public String getMockId() {
		return mockId;
	}

	public void setMockId(String mockId) {
		this.mockId = mockId;
	}

	public String getRequestData() {
		return requestData;
	}

	public void setRequestData(String requestData) {
		this.requestData = requestData;
	}

	public String getResponseData() {
		return responseData;
	}

	public void setResponseData(String responseData) {
		this.responseData = responseData;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public Timestamp getInsertedDate() {
		return insertedDate;
	}

	public void setInsertedDate(Timestamp insertedDate) {
		this.insertedDate = insertedDate;
	}
}
