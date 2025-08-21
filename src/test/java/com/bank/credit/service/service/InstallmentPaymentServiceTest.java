package com.bank.credit.service.service;

import com.bank.credit.service.dto.InstallmentDto;
import com.bank.credit.service.dto.PayedInstallmentDto;
import com.bank.credit.service.repository.CustomerRepository;
import com.bank.credit.service.repository.LoanInstallmentRepository;
import com.bank.credit.service.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

@Disabled
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
    void create_shouldReturnDto_whenSuccess() {
        // Given


        // When
        PayedInstallmentDto result = installmentPaymentService.payInstallment(new InstallmentDto());

        // Then

    }

}
