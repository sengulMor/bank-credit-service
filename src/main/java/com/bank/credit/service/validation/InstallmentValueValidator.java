package com.bank.credit.service.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;


@Component
public class InstallmentValueValidator implements ConstraintValidator<InstallmentValue, Integer> {

    private int[] allowedValues;

    @Override
    public void initialize(InstallmentValue constraintAnnotation) {
        this.allowedValues = constraintAnnotation.values();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return Arrays.stream(allowedValues).anyMatch(v -> v == value);
    }
}
