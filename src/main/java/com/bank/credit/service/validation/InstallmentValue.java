package com.bank.credit.service.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = InstallmentValueValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface InstallmentValue {
    int[] values();
    String message() default "Value is not allowed";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
