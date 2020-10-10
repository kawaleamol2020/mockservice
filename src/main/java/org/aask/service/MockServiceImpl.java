package org.aask.service;

import static org.aask.util.CommonUtils.generateChecksum;
import static org.aask.util.XmlUtils.removeNameSpaceAndHeaderFrom;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.transaction.Transactional;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.aask.bean.ContentTypeHeader;
import org.aask.bean.MockData;
import org.aask.bean.MockRequest;
import org.aask.bean.MockResponse;
import org.aask.constant.HttpResponseCodes;
import org.aask.constant.MockConstant;
import org.aask.entity.MockRequestData;
import org.aask.entity.MockResponseData;
import org.aask.entity.ResponseHeader;
import org.aask.exception.MockDataNotFoundException;
import org.aask.repository.MockRequestRepository;
import org.aask.repository.MockResponseRepository;
import org.aask.util.CommonUtils;
import org.aask.util.Recording;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;


@Service
public class MockServiceImpl implements MockService {

	@Autowired
	private MockResponseRepository mockResponseRespository;

	@Autowired
	private MockRequestRepository mockRequestRespository;

	@Autowired
	ConfigurationService configurationService;

	Logger logger = LoggerFactory.getLogger(MockServiceImpl.class);

	private static List<String> responseHeaderIgnoreList = Arrays.asList("date", "transfer-encoding");
	private static List<String> requestHeaderIgnoreList = Arrays.asList("content-length");

	@Override
	@Recording
	public MockResponse triggerLiveService(MockRequest request) throws Exception {

		logger.info("triggerLiveService start");

		MockResponse response = new MockResponse();

		try {

			HttpClient client = HttpClientBuilder.create().build();
			HttpPost post = new HttpPost(configurationService.getEndPointURL());

			ContentTypeHeader contentTypeHeader = ContentTypeHeader.getContentTypeHeader(request.getHeaders());
			ContentType  contentType = ContentType.create(contentTypeHeader.mimeTypePart(), contentTypeHeader.encodingPart().or("utf-8"));

			setHeaders(request, post);
			 
			//post.setEntity(new StringEntity(request.getReqeustBody()));
			 post.setEntity(new InputStreamEntity(new ByteArrayInputStream(request.getReqeustBody().getBytes()), -1, contentType));

			logger.info("client request :" + post);
			logger.info("client request Entity :" + post.getEntity());

			HttpResponse liveServiceResponse = client.execute(post);

			logger.info("client response :" + liveServiceResponse);

			logger.info("Live Serivce response code:" + liveServiceResponse.getStatusLine().getStatusCode());

			BufferedReader bufferReader = new BufferedReader(
					new InputStreamReader(liveServiceResponse.getEntity().getContent()));

			StringBuffer resposneString = new StringBuffer();
			String line = "";
			while ((line = bufferReader.readLine()) != null) {
				resposneString.append(line);
			}
			bufferReader.close();
			response.setResponseBody(resposneString.toString());
			response.setStatusCode(liveServiceResponse.getStatusLine().getStatusCode());
			response.setMessage(liveServiceResponse.getStatusLine().getReasonPhrase());
			Map<String, String> headers = new HashMap<>();
			for (Header header : liveServiceResponse.getAllHeaders()) {
				if (!responseHeaderIgnoreList.contains(header.getName().toLowerCase()))
					headers.put(header.getName(), header.getValue());
			}

			String mockId = null;
			if (response.getStatusCode() == 200)
				mockId = CommonUtils.generatePrimaryKey(request.getSoapAction(), response.getStatusCode(),
						request.getRequestCheckSum());
			else
				mockId = CommonUtils.generatePrimaryKey(request.getSoapAction(), HttpResponseCodes.HTTP_INTERNAL_ERROR,
						request.getRequestCheckSum());
			headers.put(MockConstant.RESPONSE_KEY, mockId);
			response.setHeaders(headers);

			liveServiceResponse.getEntity().getContent().close();

		} catch (Exception e) {
			logger.error("triggerLiveService error " + e);
			throw e;
		}

		logger.info("triggerLiveService end");

		return response;
	}

	@Override
	public MockResponse getMockDataByRequest(MockRequest request) throws Exception {
		logger.info("getMockDataByRequest start");

		MockResponse response = new MockResponse();

		String mockId = CommonUtils.generatePrimaryKey(request.getSoapAction(), HttpResponseCodes.HTTP_OK,
				request.getRequestCheckSum());

		Optional<MockResponseData> optionalMockResponseData = mockResponseRespository.findById(mockId);
		if (optionalMockResponseData.isPresent()) {
			MockResponseData mockResponseData = optionalMockResponseData.get();
			response.setResponseBody(mockResponseData.getResponseData());
			response.setStatusCode(mockResponseData.getStatusCode());
			Map<String, String> headers = new HashMap<>();
			mockResponseData.getResponseHeaders().forEach(header -> headers.put(header.getName(), header.getValue()));
			headers.put(MockConstant.RESPONSE_KEY, mockId);
			response.setHeaders(headers);
		} else {
			logger.error("mock data not found for given reqeust");
			throw new MockDataNotFoundException("mock data not found for given reqeust");
		}

		logger.info("getMockDataByRequest end");
		return response;
	}

	@Override
	public MockResponse getErrorMockData(MockRequest request) throws Exception {

		logger.info("getErrorMockData start");

		MockResponse response = new MockResponse();

		String mockId = CommonUtils.generatePrimaryKey(request.getSoapAction(), HttpResponseCodes.HTTP_INTERNAL_ERROR,
				request.getRequestCheckSum());

		Optional<MockResponseData> optionalMockResponseData = mockResponseRespository.findById(mockId);
		if (optionalMockResponseData.isPresent()) {
			MockResponseData mockResponseData = optionalMockResponseData.get();
			if (mockResponseData.getExceptionObject() != null) {
				byte[] exceptionObject = mockResponseData.getExceptionObject();
				ByteArrayInputStream bais = new ByteArrayInputStream(exceptionObject);
				ObjectInputStream ois = new ObjectInputStream(bais);
				Exception exception = (Exception) ois.readObject();
				throw exception;
			} else {
				response.setResponseBody(mockResponseData.getResponseData());
				response.setStatusCode(mockResponseData.getStatusCode());
				Map<String, String> headers = new HashMap<>();
				mockResponseData.getResponseHeaders()
						.forEach(header -> headers.put(header.getName(), header.getValue()));
				headers.put(MockConstant.RESPONSE_KEY, mockId);
				response.setHeaders(headers);
			}
		} else {
			logger.error("mock data not found for given reqeust");
			throw new MockDataNotFoundException("mock data not found for given reqeust");
		}

		logger.info("getErrorMockData end");

		return response;
	}

	@Override
	public List<MockData> getMockedDataByPage() throws Exception {
		logger.info("loadMockedData start");
		List<MockData> mockDataList = new ArrayList<>();

		Page<MockResponseData> pageData = mockResponseRespository
				.findAllActiveData(PageRequest.of(0, configurationService.getGlobalConfiguration().getNoOfRowsDisplay(),
						Sort.by(Sort.Direction.DESC, "insertedDate")));
		List<MockResponseData> list = pageData.getContent();

		for (MockResponseData mockData : list) {
			MockData mockDataBean = new MockData();
			BeanUtils.copyProperties(mockData, mockDataBean);
			mockDataBean.setRequestData(mockData.getMockRequestData().getRequestData());
			mockDataList.add(mockDataBean);
		}
		logger.info("loadMockedData end");
		return mockDataList;
	}

	@Override
	public void deleteMockData(String id) throws Exception {
		logger.info("deleteMockData start");
		mockResponseRespository.deleteById(id);
		logger.info("deleteMockData end");
	}

	@Override
	public MockData getMockedDataById(String id) throws Exception {
		logger.info("getMockedDataById start");
		MockData mockDataBean = new MockData();
		Optional<MockResponseData> optionalMockData = mockResponseRespository.findById(id);
		if (optionalMockData.isPresent()) {
			MockResponseData mockData = optionalMockData.get();
			BeanUtils.copyProperties(mockData, mockDataBean);
			mockDataBean.setRequestData(mockData.getMockRequestData().getRequestData());
		}
		logger.info("getMockedDataById end");
		return mockDataBean;
	}

	@Override
	public void updateMockData(MockData mockDataBean) throws Exception {
		logger.info("insertOrUpdateMockData start");

		if (!StringUtils.isEmpty(mockDataBean.getMockId())) {
			MockResponseData mockResponseData = new MockResponseData();
			String newMockId = mockDataBean.getMockId() + "_" + UUID.randomUUID();
			Timestamp currentTimeStamp = Timestamp.valueOf(LocalDateTime.now());

			Optional<MockResponseData> mockResponseDBData = mockResponseRespository.findById(mockDataBean.getMockId());
			if (mockResponseDBData.isPresent()) {

				Optional<MockRequestData> mockRequestDBData = mockRequestRespository.findById(mockDataBean.getMockId());

				if (mockRequestDBData.isPresent()) {
					MockRequestData mockRequestDataOld = mockRequestDBData.get();

					MockRequestData mockRequestData = new MockRequestData();
					BeanUtils.copyProperties(mockRequestDataOld, mockRequestData);
					mockRequestData.setMockId(newMockId);
					mockRequestData.setArchivedDate(currentTimeStamp);
					mockRequestRespository.save(mockRequestData);

					mockRequestDataOld.setInsertedDate(currentTimeStamp);
					mockRequestRespository.save(mockRequestDataOld);
				}

				MockResponseData mockResponseDataOld = mockResponseDBData.get();
				BeanUtils.copyProperties(mockResponseDataOld, mockResponseData);
				mockResponseData.setMockId(newMockId);
				mockResponseData.setArchivedDate(currentTimeStamp);
				Set<ResponseHeader> responseHeaders = new HashSet<ResponseHeader>();
				mockResponseDataOld.getResponseHeaders().stream().forEach(header -> responseHeaders.add(header));
				mockResponseData.setResponseHeaders(responseHeaders);
				mockResponseRespository.save(mockResponseData);

				mockResponseDataOld.setResponseData(mockDataBean.getResponseData());
				mockResponseDataOld.setInsertedDate(currentTimeStamp);
				mockResponseRespository.save(mockResponseDataOld);
			}
		}

		logger.info("insertOrUpdateMockData end");
	}

	@Override
	public MockData searchMockData(String soapAction, int statusCode, String requestData) throws Exception {
		logger.info("searchMockData start");
		MockData searchData = new MockData();
		String requestCheckSumWithoutHeader = generateChecksum(removeNameSpaceAndHeaderFrom(requestData));
		String mockId = CommonUtils.generatePrimaryKey(soapAction, statusCode, requestCheckSumWithoutHeader);
		logger.info("mockId =" + mockId);

		Optional<MockResponseData> opationalMockData = mockResponseRespository.findById(mockId);
		if (opationalMockData.isPresent()) {
			MockResponseData mockData = opationalMockData.get();
			BeanUtils.copyProperties(mockData, searchData);
			searchData.setRequestData(mockData.getMockRequestData().getRequestData());
		} else {
			throw new MockDataNotFoundException("Mock data not found for request");
		}

		return searchData;
	}

	private void setHeaders(MockRequest request, HttpPost post) {

		if (CollectionUtils.isEmpty(request.getHeaders())) {
			post.setHeader("User-Agent", "Mock Service");
			post.setHeader("Content-Type", "text/xml;charset=UTF-8");
		} else {
			for (Entry<String, String> entry : request.getHeaders().entrySet()) {
				if (!requestHeaderIgnoreList.contains(entry.getKey().toLowerCase()))
					post.setHeader(entry.getKey(), entry.getValue());
			}

			if (!request.getHeaders().containsKey("Content-Type"))
				post.setHeader("Content-Type", "text/xml;charset=UTF-8");
			if (!request.getHeaders().containsKey("User-Agent"))
				post.setHeader("User-Agent", "Mock Service");
		}
	}

	@Override
	@Transactional
	public void clearArchivedMockData(Date date) {
		logger.info("clearArchivedMockData start");
		logger.info("date "+date);
		mockResponseRespository.deleteAllByArchivedDateNotNullAndArchivedDateLessThanEqual(new Timestamp(date.getTime()));
		logger.info("clearArchivedMockData end");
	}
}
