package com.huangjiaxin.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 通过注解实现最终增强
 */
@Aspect
public class AfterLogger {
	private static final Logger log = LoggerFactory.getLogger(AfterLogger.class);

	@After("execution(* com.huangjiaxin.controller..*(..))")
	public void afterLogger(JoinPoint jp) {
		log.info(jp.getSignature().getName() + " 环绕增强      方法结束执行。");
	}

}