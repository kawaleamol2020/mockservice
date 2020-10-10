package org.aask.bean;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import org.aask.util.MOCK_TYPE;
import org.hibernate.validator.constraints.Range;

public class MockGlobalConfiguration implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@NotBlank(message = "End Point URL required")
	private String endPointURL;

	private MOCK_TYPE mockType;

	@Range(min = 1, max = 100, message = "Invalid Number")
	private Integer noOfRowsDisplay = 10;

	public String getEndPointURL() {
		return endPointURL;
	}

	public void setEndPointURL(String endPointURL) {
		this.endPointURL = endPointURL;
	}

	public MOCK_TYPE getMockType() {
		return mockType;
	}

	public void setMockType(MOCK_TYPE mockType) {
		this.mockType = mockType;
	}

	public Integer getNoOfRowsDisplay() {
		return noOfRowsDisplay;
	}

	public void setNoOfRowsDisplay(Integer noOfRowsDisplay) {
		if(noOfRowsDisplay == null)
			this.noOfRowsDisplay=10;
		else
			this.noOfRowsDisplay = noOfRowsDisplay;
	}

	@Override
	public String toString() {
		return "MockGlobalConfiguration [endPointURL=" + endPointURL + ", mockType=" + mockType + ", noOfRowsDisplay="
				+ noOfRowsDisplay + "]";
	}
}
