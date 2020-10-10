package org.aask.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "MOCK_RESPONSE_DATA")
public class MockResponseData implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String mockId;

	private Integer statusCode;

	private String soapAction;

	@Lob
	private String responseData;

	@Lob
	private byte[] exceptionObject;

	private Timestamp insertedDate;

	private Timestamp archivedDate;

	@ElementCollection
	@CollectionTable(name="RESPONSE_HEADERS", joinColumns = @JoinColumn(name="mock_id"))
	private Set<ResponseHeader> responseHeaders;
	
	@OneToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name="mock_id", referencedColumnName="mock_id")
	private MockRequestData mockRequestData;

	public String getMockId() {
		return mockId;
	}

	public void setMockId(String mockId) {
		this.mockId = mockId;
	}

	public Integer getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}

	public String getSoapAction() {
		return soapAction;
	}

	public void setSoapAction(String soapAction) {
		this.soapAction = soapAction;
	}

	public String getResponseData() {
		return responseData;
	}

	public void setResponseData(String responseData) {
		this.responseData = responseData;
	}

	public byte[] getExceptionObject() {
		return exceptionObject;
	}

	public void setExceptionObject(byte[] exceptionObject) {
		this.exceptionObject = exceptionObject;
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

	public Set<ResponseHeader> getResponseHeaders() {
		return responseHeaders;
	}

	public void setResponseHeaders(Set<ResponseHeader> responseHeaders) {
		this.responseHeaders = responseHeaders;
	}

	public MockRequestData getMockRequestData() {
		return mockRequestData;
	}

	public void setMockRequestData(MockRequestData mockRequestData) {
		this.mockRequestData = mockRequestData;
	}
}
