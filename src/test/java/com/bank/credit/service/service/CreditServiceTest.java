package com.bank.credit.service.service;

import com.bank.credit.service.dto.CreditDto;
import com.bank.credit.service.dto.LoanFilter;
import com.bank.credit.service.exception.CustomerNotFoundException;
import com.bank.credit.service.mapper.LoanMapper;
import com.bank.credit.service.model.Customer;
import com.bank.credit.service.model.Loan;
import com.bank.credit.service.model.LoanInstallment;
import com.bank.credit.service.repository.CustomerRepository;
import com.bank.credit.service.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreditServiceTest {

    private LoanRepository loanRepository;
    private LoanMapper loanMapper;
    private CustomerRepository customerRepository;
    private LoanInstallmentService loanInstallmentService;
    private CreditService creditService;

    @BeforeEach
    void setUp() {
        loanRepository = mock(LoanRepository.class);
        loanMapper = mock(LoanMapper.class);
        customerRepository = mock(CustomerRepository.class);
        loanInstallmentService = mock(LoanInstallmentService.class);
        creditService = new CreditService(loanRepository, loanMapper, customerRepository, loanInstallmentService);
    }

    @Test
    void create_shouldReturnDto_whenSuccess() {
        // Given
        CreditDto creditDto = getCreditDto(new BigDecimal("1000"));
        Customer customer = new Customer("name", "surname", new BigDecimal("10000"), new BigDecimal("1000"), null);
        Loan loan = getLoan(customer);
        LoanInstallment loanInstallment = getLoanInstallment();
        Loan savedLoan = getLoan(customer);
        savedLoan.addInstallments(List.of(loanInstallment));
        CreditDto resultDto = getCreditDto(new BigDecimal("1100"));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(loanMapper.toEntity(Mockito.eq(creditDto), Mockito.any(Customer.class), Mockito.any(BigDecimal.class))).thenReturn(loan);
        when(loanInstallmentService.buildLoanInstallments(any())).thenReturn(List.of(loanInstallment));
        when(loanRepository.save(loan)).thenReturn(savedLoan);
        when(loanMapper.toDto(any())).thenReturn(resultDto);

        // When
        CreditDto result = creditService.create(creditDto);

        // Then
        ArgumentCaptor<BigDecimal> totalAmountCaptor = ArgumentCaptor.forClass(BigDecimal.class);
        verify(loanMapper).toEntity(Mockito.eq(creditDto), Mockito.any(Customer.class), totalAmountCaptor.capture());
        BigDecimal capturedTotalAmount = totalAmountCaptor.getValue();
        assertEquals(new BigDecimal("1100.0"), capturedTotalAmount);
        assertEquals(resultDto, result);
        verify(customerRepository).findById(1L);
        verify(loanInstallmentService).buildLoanInstallments(any());
        verify(loanRepository).save(loan);
        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(customerCaptor.capture());
        Customer savedCustomer = customerCaptor.getValue();
        assertEquals(new BigDecimal("2100.0"), savedCustomer.getUsedCreditLimit());
    }

    @Test
    void create_shouldThrowCustomerNotFoundException_whenCustomerNotFound() {
        // Given
        CreditDto creditDto = getCreditDto(new BigDecimal("1000"));
        when(customerRepository.findById(creditDto.getCustomerId())).thenReturn(Optional.empty());

        // When & Then
        CustomerNotFoundException ex = assertThrows(CustomerNotFoundException.class, () -> {
            creditService.create(creditDto);
        });
        assertEquals("Customer with ID 1 not found", ex.getMessage());


        verify(loanMapper, never()).toEntity(any(), any(), any());
        verify(loanInstallmentService, never()).buildLoanInstallments(any());
        verify(loanRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowException_whenLoanSaveFails() {
        // Given
        CreditDto creditDto = getCreditDto(new BigDecimal("1000"));
        Customer customer = new Customer("name", "surname", new BigDecimal("10000"), new BigDecimal("1000"), null);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(loanMapper.toEntity(any(), any(), any())).thenReturn(getLoan(customer));
        when(loanInstallmentService.buildLoanInstallments(any())).thenReturn(List.of(getLoanInstallment()));
        when(loanRepository.save(any())).thenThrow(new RuntimeException("DB error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> creditService.create(creditDto));
        verify(customerRepository).findById(1L);
        verify(loanInstallmentService).buildLoanInstallments(any());
    }

    @Test
    void create_shouldThrowException_whenFindAllLOan() {
        // Given
        LoanFilter filter = getLoanFilter();
        Pageable pageable = PageRequest.of(0, 10);

        // When & Then
        when(loanRepository.findAll(any(Specification.class), eq(pageable))).thenThrow(new RuntimeException("DB error"));
        assertThrows(RuntimeException.class, () -> creditService.getLoanByCustomer(filter, pageable));
    }

    @Test
    void getLoanByCustomer_shouldReturnMappedPage() {
        // Given
        LoanFilter filter = getLoanFilter();
        Pageable pageable = PageRequest.of(0, 10);
        Loan loan = getLoan(new Customer());
        Page<Loan> loanPage = new PageImpl<>(List.of(loan), pageable, 1);
        when(loanRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(loanPage);
        when(loanMapper.toDto(any(Loan.class))).thenReturn(new CreditDto());

        // When
        Page<CreditDto> result = creditService.getLoanByCustomer(filter, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        ArgumentCaptor<Specification<Loan>> specCaptor = ArgumentCaptor.forClass(Specification.class);
        verify(loanRepository).findAll(specCaptor.capture(), eq(pageable));
        verify(loanMapper, times(loanPage.getContent().size())).toDto(any(Loan.class));
    }

    private LoanFilter getLoanFilter() {
        return LoanFilter.builder()
                .customerId(1L)
                .numberOfInstallment(9)
                .isPaid(false).build();
    }


    private LoanInstallment getLoanInstallment() {
        LocalDate firstOfNextMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfNextMonth());
        LoanInstallment loanInstallment = new LoanInstallment();
        loanInstallment.setAmount(new BigDecimal("122"));
        loanInstallment.setDueDate(firstOfNextMonth);
        return loanInstallment;
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


    private CreditDto getCreditDto(BigDecimal amount) {
        CreditDto dto = new CreditDto();
        dto.setCustomerId(1L);
        dto.setInterestRate(new BigDecimal("0.1"));
        dto.setNumberOfInstallment(9);
        dto.setLoanAmount(amount);
        return dto;
    }
}

