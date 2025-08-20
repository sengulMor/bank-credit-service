package com.bank.credit.service.exception;

public class InvalidPaymentAmountException extends RuntimeException {

    public InvalidPaymentAmountException(Long loanId) {
        super("Amount too small to cover any installment for loan by ID " + loanId);
    }
}
