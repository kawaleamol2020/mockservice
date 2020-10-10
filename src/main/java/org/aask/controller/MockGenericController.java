package org.aask.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aask.bean.MockRequest;
import org.aask.bean.MockResponse;
import org.aask.handler.MockActionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MockGenericController {

	private Logger logger = LoggerFactory.getLogger(MockGenericController.class);

	@Autowired
	MockActionHandler mockActionHandler;

	@RequestMapping(value = "/mockrest/**",consumes=MediaType.ALL_VALUE)
	public void doMock(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
			throws Exception {
		logger.info("doMock start");
		
		MockRequest request = new MockRequest();
		mockActionHandler.getRequetDataFrom(httpServletRequest,request);

		MockResponse mockResponse = mockActionHandler.performMock(request);

		mockActionHandler.writeResponseDataTo(httpServletResponse, mockResponse);
		
		logger.info("doMock end");
	}

}
