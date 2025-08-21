package com.bank.credit.service.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleCustomerNotFoundException_shouldReturnNotFoundMessage() {
        Long id = 1L;
        CustomerNotFoundException ex = new CustomerNotFoundException(id);
        ResponseEntity<String> response = handler.handleCustomerNotFound(ex);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Customer with ID " + id + " not found", response.getBody());
    }

    @Test
    void handleInvalidPaymentAmount_shouldReturnNotFoundMessage() {
        Long id = 1L;
        InvalidPaymentAmountException ex = new InvalidPaymentAmountException(id);
        ResponseEntity<String> response = handler.handleInvalidPaymentAmountException(ex);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Amount too small to cover any installment for loan by ID " + id, response.getBody());
    }


    @Test
    void handleUnpaidInstallmentsNotFound_shouldReturnNotFoundMessage() {
        Long id = 1L;
        UnpaidInstallmentsNotFoundException ex = new UnpaidInstallmentsNotFoundException(id);
        ResponseEntity<String> response = handler.handleUnpaidInstallmentsNotFoundException(ex);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("No unpaid installments found for Loan ID " + id + " within the upcoming 3 calendar months.", response.getBody());
    }
}

