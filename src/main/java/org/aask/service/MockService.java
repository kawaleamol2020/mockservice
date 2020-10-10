package org.aask.service;

import java.util.Date;
import java.util.List;

import org.aask.bean.MockData;
import org.aask.bean.MockRequest;
import org.aask.bean.MockResponse;


public interface MockService {

	public MockResponse triggerLiveService(MockRequest request) throws Exception;

	public MockResponse getMockDataByRequest(MockRequest request) throws Exception;

	public MockResponse getErrorMockData(MockRequest request) throws Exception;

	public List<MockData> getMockedDataByPage() throws Exception;

	public void deleteMockData(String id) throws Exception;

	public MockData getMockedDataById(String id) throws Exception;

	public void updateMockData(MockData mockDataBean) throws Exception;

	public MockData searchMockData(String soapAction, int statusCode, String requestData) throws Exception;

	public void clearArchivedMockData(Date date);
}
