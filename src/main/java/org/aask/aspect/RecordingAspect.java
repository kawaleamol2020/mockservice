package org.aask.aspect;

import org.aask.bean.MockRequest;
import org.aask.bean.MockRequestResponseWrapper;
import org.aask.bean.MockResponse;
import org.aask.event.RecordingEvent;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;

@Aspect
@Configuration
public class RecordingAspect {

	private Logger logger = LoggerFactory.getLogger(RecordingAspect.class);

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	@Around("@annotation(org.aask.util.Recording)")
	public Object recording(ProceedingJoinPoint joinPoint) throws Throwable {
		logger.info("recording - start recording for " + joinPoint.getSignature().getName() + " method ");
		MockRequest request = null;
		try {
			Object[] args = joinPoint.getArgs();
			if (args.length > 0 && args[0] instanceof MockRequest) {
				request = (MockRequest) args[0];
				logger.info("Request Data : " + request.toString());
			}

			Object responseObj = joinPoint.proceed();
			if (responseObj != null && responseObj instanceof MockResponse) {
				MockResponse response = (MockResponse) responseObj;
				MockRequestResponseWrapper wrapper = new MockRequestResponseWrapper();
				wrapper.setMockRequest(request);
				wrapper.setMockResponse(response);
				applicationEventPublisher.publishEvent(new RecordingEvent(this, wrapper));
				logger.info("recording response event triggered");
				logger.info("Response Data : " + response.toString());
			}

			logger.info("recording - end recording for " + joinPoint.getSignature().getName() + " method");

			return responseObj;
		} catch (Throwable throwable) {
			MockRequestResponseWrapper wrapper = new MockRequestResponseWrapper();
			wrapper.setMockRequest(request);
			wrapper.setMockError(throwable);
			wrapper.setErrorResponse(true);
			applicationEventPublisher.publishEvent(new RecordingEvent(this, wrapper));
			logger.info("recording error event triggered");
			throw throwable;
		}
	}
}
