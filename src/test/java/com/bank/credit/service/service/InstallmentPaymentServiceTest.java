package com.bank.credit.service.service;

import com.bank.credit.service.dto.InstallmentDto;
import com.bank.credit.service.dto.PayedInstallmentDto;
import com.bank.credit.service.exception.CustomerNotFoundException;
import com.bank.credit.service.exception.InvalidPaymentAmountException;
import com.bank.credit.service.exception.UnpaidInstallmentsNotFoundException;
import com.bank.credit.service.model.Customer;
import com.bank.credit.service.model.Loan;
import com.bank.credit.service.model.LoanInstallment;
import com.bank.credit.service.repository.CustomerRepository;
import com.bank.credit.service.repository.LoanInstallmentRepository;
import com.bank.credit.service.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class InstallmentPaymentServiceTest {
    private LoanInstallmentRepository loanInstallmentRepository;
    private CustomerRepository customerRepository;
    private LoanRepository loanRepository;
    private InstallmentPaymentService installmentPaymentService;

    @BeforeEach
    void setUp() {
        loanInstallmentRepository = mock(LoanInstallmentRepository.class);
        customerRepository = mock(CustomerRepository.class);
        loanRepository = mock(LoanRepository.class);
        installmentPaymentService = new InstallmentPaymentService(loanInstallmentRepository, customerRepository, loanRepository);
    }

    @Test
    void payInstallment_shouldReturnDto_whenPartialPaymentIsMade_andLoanIsNotFullyPaid() {
        // Given
        Long loanId = 1L;
        Loan loan = getLoan(loanId);
        InstallmentDto dto = getInstallmentDto(loanId, new BigDecimal("400"));
        Customer customer = getCustomer();
        List<LoanInstallment> installments = new ArrayList<>();
        installments.add(getLoanInstallment(loan, 9));
        installments.add(getLoanInstallment(loan, 10));
        installments.add(getLoanInstallment(loan, 11));

        when(loanInstallmentRepository.findByLoan_IdAndDueDateBetweenAndIsPaidFalse(any(), any(), any())).thenReturn(installments);
        when(customerRepository.findByLoans_Id(loanId)).thenReturn(Optional.of(customer));
        when(loanInstallmentRepository.existsByLoan_IdAndIsPaidFalse(loanId)).thenReturn(Boolean.TRUE);

        // When
        PayedInstallmentDto result = installmentPaymentService.payInstallment(dto);

        // Then
        assertEquals(1, result.getPayedInstallment());
        assertEquals(new BigDecimal(350), result.getTotalAmountSpent());
        assertFalse(result.isLoanPaymentComplate());
        verify(loanInstallmentRepository).findByLoan_IdAndDueDateBetweenAndIsPaidFalse(any(), any(), any());
        verify(loanInstallmentRepository).saveAll(any());
        verify(customerRepository).save(any());
        assertEquals(new BigDecimal(550), customer.getUsedCreditLimit());
    }

    @Test
    void payInstallment_shouldReturnDto_whenPartialPaymentCoversTwoInstallments() {
        // Given
        Long loanId = 1L;
        Loan loan = getLoan(loanId);
        InstallmentDto dto = getInstallmentDto(loanId, new BigDecimal("800"));
        Customer customer = getCustomer();
        List<LoanInstallment> installments = new ArrayList<>();
        installments.add(getLoanInstallment(loan, 9));
        installments.add(getLoanInstallment(loan, 10));
        installments.add(getLoanInstallment(loan, 11));

        when(loanInstallmentRepository.findByLoan_IdAndDueDateBetweenAndIsPaidFalse(any(), any(), any())).thenReturn(installments);
        when(customerRepository.findByLoans_Id(loanId)).thenReturn(Optional.of(customer));
        when(loanInstallmentRepository.existsByLoan_IdAndIsPaidFalse(loanId)).thenReturn(Boolean.TRUE);

        // When
        PayedInstallmentDto result = installmentPaymentService.payInstallment(dto);

        // Then
        assertEquals(2, result.getPayedInstallment());
        assertEquals(new BigDecimal(700), result.getTotalAmountSpent());
        assertFalse(result.isLoanPaymentComplate());
        verify(loanInstallmentRepository).findByLoan_IdAndDueDateBetweenAndIsPaidFalse(any(), any(), any());
        verify(loanInstallmentRepository).saveAll(any());
        verify(customerRepository).save(any());
        assertEquals(new BigDecimal(200), customer.getUsedCreditLimit());
    }

    @Test
    void payInstallment_shouldReturnDto_whenFinalInstallmentIsPaidAndLoanIsCompleted() {
        // Given
        Long loanId = 1L;
        Loan loan = getLoan(loanId);
        InstallmentDto dto = getInstallmentDto(loanId, new BigDecimal("350"));
        Customer customer = getCustomer();
        List<LoanInstallment> installments = new ArrayList<>();
        installments.add(getLoanInstallment(loan, 9));


        when(loanInstallmentRepository.findByLoan_IdAndDueDateBetweenAndIsPaidFalse(any(), any(), any())).thenReturn(installments);
        when(customerRepository.findByLoans_Id(loanId)).thenReturn(Optional.of(customer));
        when(loanInstallmentRepository.existsByLoan_IdAndIsPaidFalse(loanId)).thenReturn(Boolean.FALSE);

        // When
        PayedInstallmentDto result = installmentPaymentService.payInstallment(dto);

        // Then
        assertEquals(1, result.getPayedInstallment());
        assertEquals(new BigDecimal(350), result.getTotalAmountSpent());
        assertTrue(result.isLoanPaymentComplate());
        verify(loanInstallmentRepository).findByLoan_IdAndDueDateBetweenAndIsPaidFalse(any(), any(), any());
        verify(loanInstallmentRepository).saveAll(any());
        verify(customerRepository).save(any());
        verify(loanRepository).save(loan);
        assertEquals(new BigDecimal(550), customer.getUsedCreditLimit());
    }

    @Test
    void payInstallment_shouldThrowRuntimeException_whenLoanInstallmentRepositoryFails() {
        // Given
        Long loanId = 1L;
        InstallmentDto dto = getInstallmentDto(loanId, new BigDecimal("350"));
        when(loanInstallmentRepository.findByLoan_IdAndDueDateBetweenAndIsPaidFalse(any(), any(), any())).thenThrow(new RuntimeException("DB error"));

        // When
        RuntimeException ex = assertThrows(RuntimeException.class, () -> installmentPaymentService.payInstallment(dto));
        assertEquals("DB error", ex.getMessage());
        verifyNoMoreInteractions(customerRepository, loanRepository);
    }

    @Test
    void payInstallment_shouldFail_whenCustomerDoesNotExist() {
        // Given
        Long loanId = 1L;
        Loan loan = getLoan(loanId);
        InstallmentDto dto = getInstallmentDto(loanId, new BigDecimal("400"));
        List<LoanInstallment> installments = new ArrayList<>();
        installments.add(getLoanInstallment(loan, 9));

        when(loanInstallmentRepository.findByLoan_IdAndDueDateBetweenAndIsPaidFalse(any(), any(), any())).thenReturn(installments);
        when(customerRepository.findByLoans_Id(loanId)).thenReturn(Optional.empty());

        // When
        CustomerNotFoundException ex = assertThrows(CustomerNotFoundException.class, () -> {
            installmentPaymentService.payInstallment(dto);
        });
        assertEquals("No customer found", ex.getMessage());

        // Then
        verify(loanInstallmentRepository).saveAll(any());
    }

    @Test
    void payInstallment_shouldThrowInvalidPaymentAmountException_whenAmountIsTooSmall() {
        // Given
        Long loanId = 1L;
        Loan loan = getLoan(loanId);
        InstallmentDto dto = getInstallmentDto(loanId, new BigDecimal("100")); // Less than 350
        List<LoanInstallment> installments = List.of(getLoanInstallment(loan, 9));

        when(loanInstallmentRepository.findByLoan_IdAndDueDateBetweenAndIsPaidFalse(any(), any(), any()))
                .thenReturn(installments);

        // When / Then

        InvalidPaymentAmountException ex = assertThrows(InvalidPaymentAmountException.class, () -> {
            installmentPaymentService.payInstallment(dto);
        });
        assertEquals("Amount too small to cover any installment for loan by ID " + loanId, ex.getMessage());

    }

    @Test
    void payInstallment_shouldThrowUnpaidInstallmentsNotFoundException_whenNoUnpaidInstallmentsExist() {
        // Given
        Long loanId = 1L;
        InstallmentDto dto = getInstallmentDto(loanId, new BigDecimal("400"));
        when(loanInstallmentRepository.findByLoan_IdAndDueDateBetweenAndIsPaidFalse(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        // When / Then
        UnpaidInstallmentsNotFoundException ex = assertThrows(UnpaidInstallmentsNotFoundException.class, () -> {
            installmentPaymentService.payInstallment(dto);
        });
        assertEquals("No unpaid installments found for Loan ID " + loanId + " within the upcoming 3 calendar months.", ex.getMessage());
    }


    private Customer getCustomer() {
        Customer customer = new Customer();
        customer.setId(2L);
        customer.setUsedCreditLimit(new BigDecimal(900));
        return customer;
    }

    private InstallmentDto getInstallmentDto(Long loanId, BigDecimal amount) {
        InstallmentDto dto = new InstallmentDto();
        dto.setLoanId(loanId);
        dto.setAmount(amount);
        return dto;
    }

    private Loan getLoan(Long loanId) {
        Loan loan = new Loan();
        loan.setId(loanId);
        loan.setLoanAmount(new BigDecimal(3000));
        return loan;
    }

    private LoanInstallment getLoanInstallment(Loan loan, int month) {
        LoanInstallment installment = new LoanInstallment();
        installment.setAmount(new BigDecimal(350));
        installment.setDueDate(LocalDate.of(2025, month, 1));
        installment.setLoan(loan);
        return installment;
    }
}
