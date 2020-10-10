package org.aask.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "MOCK_REQUEST_DATA")
public class MockRequestData implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String mockId;

	private String requestChecksum;

	@Lob
	private String requestData;

	private Timestamp insertedDate;

	private Timestamp archivedDate;
	
	@OneToOne(mappedBy="mockRequestData")
	private MockResponseData mockResponseData;

	public String getMockId() {
		return mockId;
	}

	public void setMockId(String mockId) {
		this.mockId = mockId;
	}

	public String getRequestChecksum() {
		return requestChecksum;
	}

	public void setRequestChecksum(String requestChecksum) {
		this.requestChecksum = requestChecksum;
	}

	public String getRequestData() {
		return requestData;
	}

	public void setRequestData(String requestData) {
		this.requestData = requestData;
	}

	public Timestamp getInsertedDate() {
		return insertedDate;
	}

	public void setInsertedDate(Timestamp insertedDate) {
		this.insertedDate = insertedDate;
	}

	public Timestamp getArchivedDate() {
		return archivedDate;
	}

	public void setArchivedDate(Timestamp archivedDate) {
		this.archivedDate = archivedDate;
	}

	public MockResponseData getMockResponseData() {
		return mockResponseData;
	}

	public void setMockResponseData(MockResponseData mockResponseData) {
		this.mockResponseData = mockResponseData;
	}
}
