package com.bank.credit.service.exception;

public class UnpaidInstallmentsNotFoundException extends RuntimeException {

    public UnpaidInstallmentsNotFoundException(Long loanId) {
        super("No unpaid installments found for Loan ID " + loanId + " within the upcoming 3 calendar months.");
    }
}
