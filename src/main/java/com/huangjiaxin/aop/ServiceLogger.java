package com.huangjiaxin.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * 使用注解定义切面
 */
@Aspect
public class ServiceLogger {
	private static final Logger log = LoggerFactory.getLogger(ServiceLogger.class);
	
	@Pointcut("execution(* com.huangjiaxin.service..*(..))")
	public void pointcut() {}

	@Before("pointcut()")
	public void before(JoinPoint jp) {
		log.info("调用 " + jp.getTarget() + " 的 " + jp.getSignature().getName()
				+ " 方法。方法入参：" + Arrays.toString(jp.getArgs())+"           前置增强");
	}

	@AfterReturning(pointcut = "pointcut()", returning = "returnValue")
	public void afterReturning(JoinPoint jp, Object returnValue) {
		log.info("调用 " + jp.getTarget() + " 的 " + jp.getSignature().getName()
				+ " 方法。方法返回值：" + returnValue+"           后置增强");
	}

}