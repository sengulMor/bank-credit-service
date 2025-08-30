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

    // or I can also stop the execution of the method call, loging, handle exceptions, execution time
    // This pointcut matches all public methods inside service package
    // “I wrap this method like a sandwich — I can do things before, after, or even replace it.”
    // joinPoint is here this --> com.bank.credit.service.service..*(..)
    // to summarize the purpose of @Around is --> "here will all done before method call and after, I can modify the input parameters"

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

    //Before every method execution in com.bank.credit.service.service.CustomerService.* it should run this method here,
    // I log method name and the method parameter in this method,
    // additional it can do other checks before this method execution like security (has the use the role ADMIN)
    // joinPoint is here this --> com.bank.credit.service.service.CustomerService.*(..)
    // to summarize the purpose of @Before is --> "Hey, do this first every time this method is called."

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

    //every time after finishing a method in one of this services here com.bank.credit.service.service.CustomerService.*, run this method
    // here only the method name will printed in console
    // but I can also manipulate the result if is nessesary
    // joinPoint is here this --> com.bank.credit.service.service.CustomerService.*(..)
    // to summarize the purpose of @AfterReturning is --> “After this method finishes successfully, do this next.”

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

    //every time after finishing a method in one of this services here com.bank.credit.service.service.CustomerService.*, run this method
    // here only the method name will printed in console
    // but I can also do some clean up operations or logging
    // joinPoint is here this --> com.bank.credit.service.service.CustomerService.*(..)
    // to summarize the purpose of @After is --> "Hey, do this after every time this method is finished."

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

    //only when a exception is throwed in the methods of com.bank.credit.service.service.CustomerService.*(..),
    // then will this method run,
    // I can also do here some other handlings send a alert(like create a event and send via docker, send a email), clean up some resources
    // joinPoint is here this --> com.bank.credit.service.service.CustomerService.*(..)
    // to summarize the purpose of @AfterThrowing is --> “If this method crashes, do this.”

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
