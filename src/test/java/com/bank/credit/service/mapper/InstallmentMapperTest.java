package com.bank.credit.service.mapper;

import com.bank.credit.service.dto.InstallmentDto;
import com.bank.credit.service.model.LoanInstallment;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InstallmentMapperTest {

    // Create an instance of InstallmentMapper
    private final InstallmentMapper installmentMapper = Mappers.getMapper(InstallmentMapper.class);

    @Test
    void shouldMapLoanInstallmentToDto() {
        // Given
        List<LoanInstallment> installments = new ArrayList<>();
        LoanInstallment loanInstallment = new LoanInstallment();
        loanInstallment.setAmount(new BigDecimal("100.00"));
        loanInstallment.setDueDate(LocalDate.of(2025, 10, 1));
        installments.add(loanInstallment);

        // When
        List<InstallmentDto> response = installmentMapper.toDtoList(installments);

        // Then
        assertEquals(response.get(0).getAmount(), installments.get(0).getAmount());
        assertEquals(response.get(0).getDueDate(), installments.get(0).getDueDate());
        assertEquals(response.get(0).getPaidAmount(), installments.get(0).getPaidAmount());
    }
}
