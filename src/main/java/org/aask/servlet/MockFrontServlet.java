package org.aask.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aask.bean.MockRequest;
import org.aask.bean.MockResponse;
import org.aask.handler.MockActionHandler;
import org.aask.service.RecordingServiceImpl;
import org.aask.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@WebServlet(urlPatterns = "/mockservlet/*", loadOnStartup = 1)
public class MockFrontServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	Logger logger = LoggerFactory.getLogger(RecordingServiceImpl.class);
	
	@Autowired
	MockActionHandler mockActionHandler;

	@Override
	protected void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
			throws ServletException, IOException {
		logger.info("service start");
		try {

			MockRequest request = new MockRequest();
			mockActionHandler.getRequetDataFrom(httpServletRequest,request);

			MockResponse mockResponse = mockActionHandler.performMock(request);

			mockActionHandler.writeResponseDataTo(httpServletResponse, mockResponse);

		} catch (Exception e) {
			logger.info("service error"+CommonUtils.stackTraceToString(e));
			ServletException servletException = new ServletException();
			servletException.initCause(e);
			throw servletException;
		}
		logger.info("service end");
	}
}
