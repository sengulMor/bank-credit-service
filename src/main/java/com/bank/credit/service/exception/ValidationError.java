package com.bank.credit.service.exception;

public record ValidationError(String field, String message) {
}
