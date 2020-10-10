package org.aask.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@Aspect
//@Configuration
public class LoggingAspect {

	private Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

	@Pointcut("within(org.elm..*)")
	protected void allMethod() {
	}

	@Around("allMethod()")
	public Object logging(ProceedingJoinPoint joinPoint) throws Throwable {
		logger.info("Class Name : "+joinPoint.getTarget().getClass().getName() +" Method Name : "+joinPoint.getSignature().getName() + " started ");
		Object responseObj = joinPoint.proceed(); 
		logger.info("Class Name : "+joinPoint.getTarget().getClass().getName() +" Method Name : "+joinPoint.getSignature().getName() + " ended ");
		return responseObj;	
	}
}
