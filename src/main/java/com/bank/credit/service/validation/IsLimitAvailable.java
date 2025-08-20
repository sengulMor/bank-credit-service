package com.bank.credit.service.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that the requested loan amount does not exceed the customer's available credit limit.
 */
@Documented
@Constraint(validatedBy = LimitIsAvailableValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface IsLimitAvailable {
    String message() default  "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}