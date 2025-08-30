package com.bank.credit.service.service.aop;

import com.bank.credit.service.dto.CustomerDto;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect  // declare a class for AOP, creating an Aspect
@Component
public class LoggingAspect {

    /**
     * Around advice that wraps the execution of all public methods in the service package.
     * Measures execution time and logs it.
     * Can also modify input parameters or return value if needed.
     *
     * @param joinPoint the join point representing the method execution
     * @return the result of the method execution
     * @throws Throwable if the underlying method throws an exception
     */
    @Around("execution(* com.bank.credit.service.service..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        // Proceed with the actual method
        Object result = joinPoint.proceed();

        long elapsed = System.currentTimeMillis() - start;
        log.info("Executed {} in {} ms", joinPoint.getSignature(), elapsed);

        return result;
    }

    /**
     * Before advice for all methods in CustomerService.
     * Logs method name and parameters before execution.
     * Can be extended to perform validation or security checks.
     *
     * @param joinPoint the join point representing the method execution
     */
    @Before("execution(* com.bank.credit.service.service.CustomerService.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Calling method: {} with args {}", joinPoint.getSignature(), Arrays.toString(joinPoint.getArgs()));
    }

    /**
     * AfterReturning advice for all methods in CustomerService.
     * Runs only if the method completes successfully.
     * Can manipulate the returned object if needed.
     *
     * @param result the returned value from the method
     */
    @AfterReturning(pointcut = "execution(* com.bank.credit.service.service.CustomerService.*(..))", returning = "result")
    public void returnUpdateAfter(Object result) {
        if (result instanceof CustomerDto dto) {
            dto.setName("updateName");
            log.info("Method returned: {}", dto.getName());
        }
    }

    /**
     * After advice for all methods in CustomerService.
     * Runs after the method finishes, regardless of success or exception.
     * Suitable for logging, cleanup, or resource release.
     *
     * @param joinPoint the join point representing the method execution
     */
    @After("execution(* com.bank.credit.service.service.CustomerService.*(..))")
    public void logAfter(JoinPoint joinPoint) {
        log.info("Method returned: {}", joinPoint.getSignature().getName());
    }

    /**
     * AfterThrowing advice for all methods in CustomerService.
     * Executes only if the method throws an exception.
     * Can be used for error logging, alerting, or cleanup.
     *
     * @param joinPoint the join point representing the method execution
     * @param exception the exception thrown by the method
     */
    @AfterThrowing(value = "execution(* com.bank.credit.service.service.CustomerService.*(..))", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        log.error("Calling method: {} with exception {}", joinPoint.getSignature(), exception.getMessage());
    }
}
