package org.aask.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="CONFIG_PROPERTY_LEVEL")
public class PropertyLevelConfiguration {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "CONFIG_PROPERTY_TYPE_ID")
	private ConfigurationPropertyType configurationPropertyType;
	
	private String value;

	@ManyToOne
	@JoinColumn(name = "MOCK_TYPE_ID")
	private MockType mockType;
	
	private Timestamp updatedDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ConfigurationPropertyType getConfigurationPropertyType() {
		return configurationPropertyType;
	}

	public void setConfigurationPropertyType(ConfigurationPropertyType configurationPropertyType) {
		this.configurationPropertyType = configurationPropertyType;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public MockType getMockType() {
		return mockType;
	}

	public void setMockType(MockType mockType) {
		this.mockType = mockType;
	}

	public Timestamp getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Timestamp updatedDate) {
		this.updatedDate = updatedDate;
	}
}
