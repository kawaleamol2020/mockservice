package org.aask.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "CONFIG_GLOBAL")
public class GlobalConfiguration {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "END_POINT_URL")
	private String endPointURL;

	@ManyToOne
	@JoinColumn(name = "MOCK_TYPE_ID")
	private MockType mockType;

	private Integer noOfRowsDisplay = 10;

	private Timestamp updatedDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEndPointURL() {
		return endPointURL;
	}

	public void setEndPointURL(String endPointURL) {
		this.endPointURL = endPointURL;
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

	public Integer getNoOfRowsDisplay() {
		return noOfRowsDisplay;
	}

	public void setNoOfRowsDisplay(Integer noOfRowsDisplay) {
		this.noOfRowsDisplay = noOfRowsDisplay;
	}
}
