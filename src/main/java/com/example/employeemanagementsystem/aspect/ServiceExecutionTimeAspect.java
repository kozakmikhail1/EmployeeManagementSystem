package com.example.employeemanagementsystem.aspect;

import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceExecutionTimeAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceExecutionTimeAspect.class);

    @Around("execution(public * com.example.employeemanagementsystem.service..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.nanoTime();
        try {
            return joinPoint.proceed();
        } finally {
            if (LOGGER.isInfoEnabled()) {
                long elapsedMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
                LOGGER.info("Service method {} executed in {} ms",
                        joinPoint.getSignature().toShortString(),
                        elapsedMs);
            }
        }
    }
}
