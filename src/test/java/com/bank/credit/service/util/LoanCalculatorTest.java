package com.bank.credit.service.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoanCalculatorTest {

    @Test
    void calculateTotalRepayment_shouldReturnPrincipalPlusInterest() {
        BigDecimal principal = new BigDecimal("1000");
        BigDecimal interestRate = new BigDecimal("0.2");

        BigDecimal result = LoanCalculator.calculateTotalRepayment(principal, interestRate);

        assertEquals(new BigDecimal("1200.0"), result);
    }

    @Test
    void calculateInstallmentAmount_shouldReturnDividedAmountWithRounding() {
        BigDecimal total = new BigDecimal("1000");
        int installments = 3;

        BigDecimal result = LoanCalculator.calculateInstallmentAmount(total, installments);

        assertEquals(new BigDecimal("333.33"), result);
    }

    @Test
    void calculateAvailableLimit_shouldSubtractUsedFromTotal() {
        BigDecimal creditLimit = new BigDecimal("5000");
        BigDecimal usedLimit = new BigDecimal("1200");

        BigDecimal result = LoanCalculator.calculateAvailableLimit(creditLimit, usedLimit);

        assertEquals(new BigDecimal("3800"), result);
    }

    @Test
    void calculateInstallmentsToPay_shouldReturnIntegerDivisionResult() {
        BigDecimal paymentAmount = new BigDecimal("750");
        BigDecimal installmentAmount = new BigDecimal("250");

        int result = LoanCalculator.calculateInstallmentsToPay(paymentAmount, installmentAmount);

        assertEquals(3, result);
    }

    @Test
    void calculateTotalPayment_shouldMultiplyInstallmentAmountByCount() {
        BigDecimal installmentAmount = new BigDecimal("400");
        int count = 4;

        BigDecimal result = LoanCalculator.calculateTotalPayment(installmentAmount, count);

        assertEquals(new BigDecimal("1600"), result);
    }
}
