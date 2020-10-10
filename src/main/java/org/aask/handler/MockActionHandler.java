package org.aask.handler;

import static org.aask.util.CommonUtils.generateChecksum;
import static org.aask.util.XmlUtils.removeNameSpaceAndHeaderFrom;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.conn.ConnectTimeoutException;
import org.aask.bean.MockConfigPropertyType;
import org.aask.bean.MockGlobalConfiguration;
import org.aask.bean.MockRequest;
import org.aask.bean.MockResponse;
import org.aask.exception.InvalidMockTypeException;
import org.aask.exception.MockDataNotFoundException;
import org.aask.service.ConfigurationService;
import org.aask.service.MockService;
import org.aask.util.CommonUtils;
import org.aask.util.MOCK_TYPE;
import org.aask.util.XmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.google.common.io.ByteStreams;

@Component
public class MockActionHandler {

	private Logger logger = LoggerFactory.getLogger(MockActionHandler.class);

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private MockService mockService;

	@Value("${mock.header.element.name}")
	String mockHeaderElementName;

	public MockResponse performMock(MockRequest request) throws Exception {

		logger.info("performMock start ");

		MockResponse response = new MockResponse();

		try {

			if (StringUtils.isEmpty(request) || StringUtils.isEmpty(request.getReqeustBody())) {
				response.setStatusCode(422);
				response.setMessage("Validation failed - invalid request body");
				return response;
			}
			
			MOCK_TYPE mockType = getMockType(request);

			logger.info("mockType =" + mockType);

			String requestCheckSumWithoutHeader = generateChecksum(
					removeNameSpaceAndHeaderFrom(request.getReqeustBody()));			
			request.setRequestCheckSum(requestCheckSumWithoutHeader);
			logger.info("requestCheckSumWithoutHeader =" + requestCheckSumWithoutHeader);
			
			switch (mockType) {
			case LIVE:
				response = mockService.triggerLiveService(request);
				break;
			case SOAP_ERROR:
				response = mockService.getErrorMockData(request);
				break;
			case HTTP_ERROR:
				throw new ConnectTimeoutException();	
			default:
				response = mockService.getMockDataByRequest(request);
			}

		} catch (InvalidMockTypeException me) {
			response.setStatusCode(422);
			response.setMessage("Validation failed - " + me.getMessage());
			logger.error("performMock error : " + CommonUtils.stackTraceToString(me));
		} catch (MockDataNotFoundException me) {
			try {
				logger.info("Live service triggered due to error : " + CommonUtils.stackTraceToString(me));
				response = mockService.triggerLiveService(request);
			} catch (Exception e) {
				logger.error("performMock error : " + CommonUtils.stackTraceToString(e));
				throw e;
			}
		} catch (Exception e) {
			logger.error("performMock error : " + CommonUtils.stackTraceToString(e));
			throw e;
		}

		logger.info("performMock end ");

		return response;
	}

	private MOCK_TYPE getMockType(MockRequest request) throws Exception {
		MOCK_TYPE mockType = null;

		String mockTypeFromHeader = XmlUtils.getElementValueFromXmlDocument(request.getReqeustBody(),
				mockHeaderElementName, true);

		if (null == mockTypeFromHeader) {
			mockType = extractMockTypeFromConfiguration(request);
		} else if (!StringUtils.isEmpty(mockTypeFromHeader) && !MOCK_TYPE.contains(mockTypeFromHeader)) {
			throw new InvalidMockTypeException("Mock Type not valid. Expected values are " + MOCK_TYPE.asList());
		} else {
			mockType = MOCK_TYPE.valueOf(mockTypeFromHeader.toUpperCase());
		}
		return mockType;
	}

	private MOCK_TYPE extractMockTypeFromConfiguration(MockRequest request) throws Exception {
		MOCK_TYPE mockType = MOCK_TYPE.MOCK;

		List<MockConfigPropertyType> configurationPropertyTypes = configurationService.getConfigurationPropertyTypes();
		if (!CollectionUtils.isEmpty(configurationPropertyTypes)) {

			for (MockConfigPropertyType propType : configurationPropertyTypes) {
				String path = propType.getPathReference();
				if (!StringUtils.isEmpty(path)) {
					String[] pathArray = path.split("/");
					if (pathArray.length > 1) {
						String requestPart = pathArray[0].toLowerCase();
						String elementPath = path.substring(requestPart.length() + 1, path.length());
						String elementValue = "";
						MOCK_TYPE propLvlMockType = null;
						switch (requestPart) {
						case "requestheader":
							Map<String, String> headers = request.getHeaders();
							if (!CollectionUtils.isEmpty(headers)) {
								for (Entry<String, String> entry : headers.entrySet()) {
									if (elementPath.equalsIgnoreCase(entry.getKey())) {
										elementValue = entry.getValue().toLowerCase();
										propLvlMockType = configurationService
												.getMockTypeFromPropLevelConfig(propType.getName(), elementValue);
										if (propLvlMockType != null)
											return propLvlMockType;
									}
								}
							}
							break;
						case "soapheader":
							elementValue = XmlUtils.getElementValueFromXmlDocument(request.getReqeustBody(),
									elementPath, true);
							propLvlMockType = configurationService.getMockTypeFromPropLevelConfig(propType.getName(),
									elementValue);
							if (propLvlMockType != null)
								return propLvlMockType;
							break;
						case "soapbody":
							elementValue = XmlUtils.getElementValueFromXmlDocument(request.getReqeustBody(),
									elementPath, false);
							propLvlMockType = configurationService.getMockTypeFromPropLevelConfig(propType.getName(),
									elementValue);
							if (propLvlMockType != null)
								return propLvlMockType;
							break;
						}
					}

				}
			}
		}

		MockGlobalConfiguration configuration = configurationService.getGlobalConfiguration();
		MOCK_TYPE mockTypeConfig = configuration.getMockType();
		if (mockTypeConfig != null)
			mockType = mockTypeConfig;

		return mockType;
	}

	public void getRequetDataFrom(HttpServletRequest httpServletRequest, MockRequest mockRequest) throws Exception {
		try (BufferedReader bufferReader = new BufferedReader(
				new InputStreamReader(httpServletRequest.getInputStream()))) {

			StringBuffer requestBody = new StringBuffer();
			String line = "";
			while ((line = bufferReader.readLine()) != null) {
				requestBody.append(line);
			}
			mockRequest.setReqeustBody(requestBody.toString());

			String soapAction = httpServletRequest.getHeader("SOAPAction");
			if (StringUtils.isEmpty(soapAction) || "\"\"".equals(soapAction.trim()))
				soapAction = "NA";

			mockRequest.setSoapAction(soapAction);

			Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
			Map<String, String> headers = new HashMap<>();
			while (headerNames.hasMoreElements()) {
				String headerName = headerNames.nextElement();
				headers.put(headerName, httpServletRequest.getHeader(headerName));
			}
			mockRequest.setHeaders(headers);

		} catch (Exception e) {
			throw e;
		}
	}

	public void writeResponseDataTo(HttpServletResponse httpServletResponse, MockResponse mockResponse)
			throws Exception {

		httpServletResponse.setStatus(mockResponse.getStatusCode());

		if (!CollectionUtils.isEmpty(mockResponse.getHeaders()))
			mockResponse.getHeaders().forEach((name, value) -> httpServletResponse.addHeader(name, value));

		try (ServletOutputStream out = httpServletResponse.getOutputStream()) {
			if (mockResponse.getResponseBody() != null)
				ByteStreams.copy(new ByteArrayInputStream(mockResponse.getResponseBody().getBytes()), out);
			else if (mockResponse.getMessage() != null) {
				ByteStreams.copy(new ByteArrayInputStream(mockResponse.getMessage().getBytes()), out);
			}
			out.flush();
		} catch (IOException e) {
			throw e;
		}
	}
}
