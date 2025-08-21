package com.bank.credit.service.service;

import com.bank.credit.service.dto.InstallmentDto;
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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    void buildLoanInstallments_shouldReturnCorrectInstallmentsWithAmountAndDueDates() {
        // Given
        Customer customer = new Customer("name", "surname", new BigDecimal("10000"), new BigDecimal("1000"), null);
        Loan loan = getLoan(customer);

        LocalDate today = LocalDate.now();
        LocalDate firstOfNextMonth = today.with(TemporalAdjusters.firstDayOfNextMonth());

        // When
        List<LoanInstallment> result = loanInstallmentService.buildLoanInstallments(loan);

        // Then
        assertNotNull(result);
        assertEquals(9, result.size());
        BigDecimal expectedAmount = LoanCalculator.calculateInstallmentAmount(
                loan.getLoanAmount(), loan.getNumberOfInstallment());
        assertTrue(result.stream().allMatch(i -> i.getAmount().compareTo(expectedAmount) == 0));
        assertTrue(result.stream().allMatch(i -> i.getLoan() == loan));
        for (int i = 0; i < result.size(); i++) {
            assertEquals(firstOfNextMonth.plusMonths(i), result.get(i).getDueDate());
        }
        assertEquals(firstOfNextMonth, result.get(0).getDueDate());
        assertEquals(firstOfNextMonth.plusMonths(8), result.get(8).getDueDate());
    }

    @Test
    void getByLoan_shouldReturnMappedInstallments_whenInstallmentsExist() {
        // Given
        Long loanId = 1L;
        List<LoanInstallment> installments = List.of(
                getLoanInstallment(9),
                getLoanInstallment(10)
        );

        List<InstallmentDto> expectedDtos = List.of(
                getInstallmentDto(loanId, new BigDecimal("350")),
                getInstallmentDto(loanId, new BigDecimal("350"))
        );

        when(loanInstallmentRepository.findByLoan_Id(loanId)).thenReturn(installments);
        when(installmentMapper.toDtoList(installments)).thenReturn(expectedDtos);

        // When
        List<InstallmentDto> result = loanInstallmentService.getByLoan(loanId);

        // Then
        assertEquals(expectedDtos, result);
        verify(loanInstallmentRepository).findByLoan_Id(loanId);
        verify(installmentMapper).toDtoList(installments);
    }

    @Test
    void getByLoan_shouldReturnEmptyList_whenNoInstallmentsExist() {
        // Given
        Long loanId = 2L;
        List<LoanInstallment> emptyInstallments = Collections.emptyList();

        when(loanInstallmentRepository.findByLoan_Id(loanId)).thenReturn(emptyInstallments);
        when(installmentMapper.toDtoList(emptyInstallments)).thenReturn(Collections.emptyList());

        // When
        List<InstallmentDto> result = loanInstallmentService.getByLoan(loanId);

        // Then
        assertTrue(result.isEmpty());
        verify(loanInstallmentRepository).findByLoan_Id(loanId);
        verify(installmentMapper).toDtoList(emptyInstallments);
    }

    @Test
    void getByLoan_shouldThrowException_whenRepositoryFails() {
        // Given
        Long loanId = 1L;
        when(loanInstallmentRepository.findByLoan_Id(loanId)).thenThrow(new RuntimeException("DB error"));

        // When / Then
        assertThrows(RuntimeException.class, () -> loanInstallmentService.getByLoan(loanId));
    }


    private LoanInstallment getLoanInstallment(int month) {
        LoanInstallment inst = new LoanInstallment();
        inst.setAmount(new BigDecimal("350"));
        inst.setDueDate(LocalDate.of(2025, month, 1));
        return inst;
    }

    private InstallmentDto getInstallmentDto(Long loanId, BigDecimal amount) {
        InstallmentDto dto = new InstallmentDto();
        dto.setLoanId(loanId);
        dto.setAmount(amount);
        return dto;
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
