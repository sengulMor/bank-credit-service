package com.bank.credit.service.service;

import com.bank.credit.service.mapper.InstallmentMapper;
import com.bank.credit.service.model.Customer;
import com.bank.credit.service.model.Loan;
import com.bank.credit.service.model.LoanInstallment;
import com.bank.credit.service.repository.LoanInstallmentRepository;
import com.bank.credit.service.util.LoanCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class LoanInstallmentServiceTest {

    private LoanInstallmentRepository loanInstallmentRepository;
    private InstallmentMapper installmentMapper;
    private LoanInstallmentService loanInstallmentService;

    @BeforeEach
    void setUp() {
        loanInstallmentRepository = mock(LoanInstallmentRepository.class);
        installmentMapper = mock(InstallmentMapper.class);
        loanInstallmentService = new LoanInstallmentService(loanInstallmentRepository, installmentMapper);
    }

    @Test
    void buildLoanInstallments_shouldReturn9Installments() {
        // Given
        Customer customer = new Customer("name", "surname", new BigDecimal("10000"), new BigDecimal("1000"), null);
        Loan loan = getLoan(customer);

        // When
        List<LoanInstallment> result = loanInstallmentService.buildLoanInstallments(loan);

        // Then
        assertNotNull(result);
        assertEquals(9, result.size());
        BigDecimal expectedAmount = LoanCalculator.calculateInstallmentAmount(loan.getLoanAmount(), loan.getNumberOfInstallment());
        assertTrue(result.stream().allMatch(i -> i.getAmount().compareTo(expectedAmount) == 0));
        assertTrue(result.stream().allMatch(i -> i.getLoan() == loan));
        LocalDate firstOfNextMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfNextMonth());
        for (int i = 0; i < result.size(); i++) {
            assertEquals(firstOfNextMonth.plusMonths(i), result.get(i).getDueDate());
        }
    }

    private Loan getLoan(Customer customer) {
        Loan loan = new Loan();
        loan.setCustomer(customer);
        loan.setLoanAmount(new BigDecimal("11000"));
        loan.setPaid(false);
        loan.setInterestRate(new BigDecimal("0.1"));
        loan.setNumberOfInstallment(9);
        return loan;
    }
}
