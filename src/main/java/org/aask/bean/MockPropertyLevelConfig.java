package org.aask.bean;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.validation.constraints.NotBlank;

import org.aask.util.MOCK_TYPE;

public class MockPropertyLevelConfig implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Long id;

	private MockConfigPropertyType configurationPropertyType;

	@NotBlank(message = "Value required")
	private String value;

	private MOCK_TYPE mockType;

	private Timestamp updatedDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public MockConfigPropertyType getConfigurationPropertyType() {
		return configurationPropertyType;
	}

	public void setConfigurationPropertyType(MockConfigPropertyType configurationPropertyType) {
		this.configurationPropertyType = configurationPropertyType;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public MOCK_TYPE getMockType() {
		return mockType;
	}

	public void setMockType(MOCK_TYPE mockType) {
		this.mockType = mockType;
	}

	public Timestamp getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Timestamp updatedDate) {
		this.updatedDate = updatedDate;
	}

	@Override
	public String toString() {
		return "MockPropertyLevelConfig [id=" + id + ", configurationPropertyType=" + configurationPropertyType
				+ ", value=" + value + ", mockType=" + mockType + ", updatedDate=" + updatedDate + "]";
	}
}
