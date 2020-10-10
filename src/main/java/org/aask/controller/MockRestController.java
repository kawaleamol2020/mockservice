package org.aask.controller;

import org.aask.bean.MockRequest;
import org.aask.bean.MockResponse;
import org.aask.handler.MockActionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MockRestController {

	private Logger logger = LoggerFactory.getLogger(MockRestController.class);

	@Autowired
	MockActionHandler mockAction;

	@GetMapping(value = "/mockservice")
	public String helloMock() {
		return "<center><h1>Welcome in mock service world !!!</h1></center>";
	}

	@PostMapping(value = "/mockservice")
	public MockResponse doMock(@RequestBody MockRequest request) throws Exception {
		logger.info("doMock start");
		return mockAction.performMock(request);
	}

}
