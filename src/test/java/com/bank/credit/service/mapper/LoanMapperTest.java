package com.bank.credit.service.mapper;

import com.bank.credit.service.dto.CreditDto;
import com.bank.credit.service.model.Customer;
import com.bank.credit.service.model.Loan;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class LoanMapperTest {

    // Create an instance of LoanMapper
    private final LoanMapper loanMapper = Mappers.getMapper(LoanMapper.class);


    @Test
    void toDto_shouldMapLoanToCreditDtoWithCustomerId() {
        // Given
        Customer customer = new Customer();
        customer.setId(42L);
        customer.setName("John");
        customer.setSurname("Doe");
        customer.setCreditLimit(new BigDecimal("10000"));
        customer.setUsedCreditLimit(new BigDecimal("2000"));

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setLoanAmount(new BigDecimal("5000"));
        loan.setCustomer(customer);

        // When
        CreditDto dto = loanMapper.toDto(loan);

        // Then
        assertNotNull(dto);
        assertEquals(42L, dto.getCustomerId());
        assertEquals(loan.getLoanAmount(), dto.getLoanAmount());
        assertEquals(loan.getId(), dto.getId()); // if mapped
    }

    @Test
    void toEntity_shouldMapCreditDtoToLoanWithCustomerAndTotalAmount() {
        // Given
        CreditDto dto = new CreditDto();
        dto.setNumberOfInstallment(12);

        Customer customer = new Customer();
        customer.setId(100L);
        customer.setName("Alice");
        customer.setSurname("Smith");

        BigDecimal totalAmount = new BigDecimal("7500.00");

        // When
        Loan loan = loanMapper.toEntity(dto, customer, totalAmount);

        // Then
        assertNotNull(loan);
        assertEquals(totalAmount, loan.getLoanAmount());
        assertEquals(customer, loan.getCustomer());
        assertEquals(dto.getNumberOfInstallment(), loan.getNumberOfInstallment());
    }
}
