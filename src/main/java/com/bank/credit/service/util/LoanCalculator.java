package com.bank.credit.service.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class LoanCalculator {

    private LoanCalculator() {
    }

    /**
     * Calculates the total loan repayment amount (principal + interest).
     * Formula: total = principal × (1 + interestRate)
     *
     * @param principal    the loan amount
     * @param interestRate the interest rate (e.g., 0.2 for 20%)
     * @return total repayment amount
     */
    public static BigDecimal calculateTotalRepayment(BigDecimal principal, BigDecimal interestRate) {
        return principal.multiply(interestRate.add(BigDecimal.ONE));
    }

    /**
     * Calculates the equal installment amount by dividing total repayment over the number of installments.
     *
     * @param totalAmount          the full repayment amount
     * @param numberOfInstallments number of installments (e.g., 6, 12, 24)
     * @return installment amount (rounded to 2 decimal places)
     */
    public static BigDecimal calculateInstallmentAmount(BigDecimal totalAmount, int numberOfInstallments) {
        return totalAmount.divide(BigDecimal.valueOf(numberOfInstallments), 2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates the customer's available credit limit.
     *
     * @param creditLimit the total credit limit
     * @param usedLimit   the already used credit
     * @return available credit (maybe zero or negative if overused)
     */
    public static BigDecimal calculateAvailableLimit(BigDecimal creditLimit, BigDecimal usedLimit) {
        return creditLimit.subtract(usedLimit);
    }

    /**
     * Calculates how many installments can be paid with the given amount.
     *
     * @param paymentAmount     the amount the customer is paying
     * @param installmentAmount the fixed amount of a single installment
     * @return number of installments that can be fully paid
     */
    public static int calculateInstallmentsToPay(BigDecimal paymentAmount, BigDecimal installmentAmount) {
        return paymentAmount.divideToIntegralValue(installmentAmount).intValue();
    }

    /**
     * Calculates the total amount for a number of installments.
     *
     * @param installmentAmount the amount of one installment
     * @param count             number of installments
     * @return total amount
     */
    public static BigDecimal calculateTotalPayment(BigDecimal installmentAmount, int count) {
        return installmentAmount.multiply(BigDecimal.valueOf(count));
    }
}
