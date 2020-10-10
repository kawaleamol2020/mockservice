package org.aask.service;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.transaction.Transactional;

import org.aask.bean.MockRequest;
import org.aask.bean.MockResponse;
import org.aask.constant.HttpResponseCodes;
import org.aask.entity.MockRequestData;
import org.aask.entity.MockResponseData;
import org.aask.entity.ResponseHeader;
import org.aask.repository.MockRequestRepository;
import org.aask.repository.MockResponseRepository;
import org.aask.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class RecordingServiceImpl implements RecordingService {

	@Autowired
	private MockResponseRepository mockResponseRespository;

	@Autowired
	private MockRequestRepository mockRequestRespository;

	@Autowired
	ConfigurationService configurationService;

	Logger logger = LoggerFactory.getLogger(RecordingServiceImpl.class);

	@Override
	@Transactional
	public void recordRequestAndResponse(MockRequest request, MockResponse response) {
		logger.info("recordRequestAndResponse start");
		try {

			String mockId = null;
			if (response.getStatusCode() == 200)
				mockId = CommonUtils.generatePrimaryKey(request.getSoapAction(), response.getStatusCode(),
						request.getRequestCheckSum());
			else
				mockId = CommonUtils.generatePrimaryKey(request.getSoapAction(), HttpResponseCodes.HTTP_INTERNAL_ERROR,
						request.getRequestCheckSum());
			String newMockId = mockId + "_" + UUID.randomUUID();
			Timestamp currentTimeStamp = Timestamp.valueOf(LocalDateTime.now());

			MockResponseData mockResponseData = new MockResponseData();
			Optional<MockResponseData> mockResponseDBData = mockResponseRespository.findById(mockId);
			if (mockResponseDBData.isPresent()) {
				MockResponseData mockResponseDataOld = mockResponseDBData.get();

				String generateChecksumDB = CommonUtils.generateChecksum(mockResponseDataOld.getResponseData());
				String generateChecksumResponse = CommonUtils.generateChecksum(response.getResponseBody());
				if(generateChecksumDB.equals(generateChecksumResponse)) {
					logger.info("recordRequestAndResponse - skipped updating record as no changes in response body");
					return;
				}	
				BeanUtils.copyProperties(mockResponseDataOld, mockResponseData);
				mockResponseData.setMockId(newMockId);
				mockResponseData.setArchivedDate(currentTimeStamp);
				Set<ResponseHeader> responseHeaders = new HashSet<ResponseHeader>();
				mockResponseDataOld.getResponseHeaders().stream().forEach(header -> responseHeaders.add(header));
				mockResponseData.setResponseHeaders(responseHeaders);
				mockResponseRespository.save(mockResponseData);

				mockResponseDataOld.setResponseData(response.getResponseBody());
				mockResponseDataOld.setStatusCode(response.getStatusCode());
				mockResponseDataOld.setSoapAction(request.getSoapAction());
				mockResponseDataOld.setInsertedDate(currentTimeStamp);
				if (!CollectionUtils.isEmpty(response.getHeaders())) {
					Set<ResponseHeader> headers = new HashSet<>();
					response.getHeaders().forEach((name, value) -> headers.add(new ResponseHeader(name, value)));
					mockResponseDataOld.setResponseHeaders(headers);
				}

				mockResponseRespository.save(mockResponseDataOld);

			} else {
				mockResponseData.setMockId(mockId);
				mockResponseData.setResponseData(response.getResponseBody());
				mockResponseData.setStatusCode(response.getStatusCode());
				mockResponseData.setSoapAction(request.getSoapAction());
				mockResponseData.setInsertedDate(currentTimeStamp);

				if (!CollectionUtils.isEmpty(response.getHeaders())) {
					Set<ResponseHeader> headers = new HashSet<>();
					response.getHeaders().forEach((name, value) -> headers.add(new ResponseHeader(name, value)));
					mockResponseData.setResponseHeaders(headers);
				}

				mockResponseRespository.save(mockResponseData);
			}
			
			saveAndArchiveRequestData(request, mockId, newMockId, currentTimeStamp);

		} catch (Exception e) {
			logger.error("recordRequestAndResponse error" + CommonUtils.stackTraceToString(e));
		}
		logger.info("recordRequestAndResponse end");
	}

	@Override
	@Transactional
	public void recordRequestAndErrorResponse(MockRequest request, Throwable exception) {
		logger.info("recordRequestAndErrorResponse start");
		try {

			String mockId = CommonUtils.generatePrimaryKey(request.getSoapAction(), HttpResponseCodes.HTTP_INTERNAL_ERROR,
					request.getRequestCheckSum());
			String newMockId = mockId + "_" + UUID.randomUUID();

			Timestamp currentTimeStamp = Timestamp.valueOf(LocalDateTime.now());

			MockResponseData mockResponseData = new MockResponseData();
			Optional<MockResponseData> mockResponseDBData = mockResponseRespository.findById(mockId);
			if (mockResponseDBData.isPresent()) {
				MockResponseData mockResponseDataOld = mockResponseDBData.get();

				BeanUtils.copyProperties(mockResponseDataOld, mockResponseData);
				mockResponseData.setMockId(newMockId);
				mockResponseData.setArchivedDate(currentTimeStamp);
				mockResponseRespository.save(mockResponseData);

				mockResponseDataOld.setResponseData(CommonUtils.stackTraceToString(exception));
				mockResponseDataOld.setStatusCode(HttpResponseCodes.HTTP_INTERNAL_ERROR);
				mockResponseDataOld.setSoapAction(request.getSoapAction());
				mockResponseDataOld.setInsertedDate(currentTimeStamp);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(baos);
				oos.writeObject(exception);
				mockResponseDataOld.setExceptionObject(baos.toByteArray());
				mockResponseRespository.save(mockResponseDataOld);

			} else {
				mockResponseData.setMockId(mockId);
				mockResponseData.setResponseData(CommonUtils.stackTraceToString(exception));
				mockResponseData.setStatusCode(HttpResponseCodes.HTTP_INTERNAL_ERROR);
				mockResponseData.setSoapAction(request.getSoapAction());
				mockResponseData.setInsertedDate(currentTimeStamp);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(baos);
				oos.writeObject(exception);
				mockResponseData.setExceptionObject(baos.toByteArray());				
				mockResponseRespository.save(mockResponseData);
			}
			
			saveAndArchiveRequestData(request, mockId, newMockId, currentTimeStamp);

		} catch (Exception e) {
			logger.error("recordRequestAndErrorResponse error" + CommonUtils.stackTraceToString(e));
		}

		logger.info("recordRequestAndErrorResponse end");
	}

	private void saveAndArchiveRequestData(MockRequest request, String mockId,
			String newMockId, Timestamp currentTimeStamp) {
		MockRequestData mockRequestData = new MockRequestData();
		Optional<MockRequestData> mockRequestDBData = mockRequestRespository.findById(mockId);

		if (mockRequestDBData.isPresent()) {
			MockRequestData mockRequestDataOld = mockRequestDBData.get();

			BeanUtils.copyProperties(mockRequestDataOld, mockRequestData);
			mockRequestData.setMockId(newMockId);
			mockRequestData.setArchivedDate(currentTimeStamp);
			mockRequestRespository.save(mockRequestData);

			mockRequestDataOld.setRequestChecksum(request.getRequestCheckSum());
			mockRequestDataOld.setRequestData(request.getReqeustBody());
			mockRequestDataOld.setInsertedDate(currentTimeStamp);
			mockRequestRespository.save(mockRequestDataOld);

		} else {
			mockRequestData.setMockId(mockId);
			mockRequestData.setRequestChecksum(request.getRequestCheckSum());
			mockRequestData.setRequestData(request.getReqeustBody());
			mockRequestData.setInsertedDate(currentTimeStamp);

			mockRequestRespository.save(mockRequestData);
		}
	}
}
