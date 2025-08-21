package com.bank.credit.service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InstallmentDto {

    @NotNull(message = "Loan Id is required")
    private Long loanId;

    @NotNull(message = "Loan amount is required")
    @DecimalMin(value = "100")
    @Digits(integer = 10, fraction = 2, message = "Amount must be a valid percentage (max 2 decimal places)")
    private BigDecimal amount;

    private BigDecimal paidAmount;

    private LocalDate dueDate;

    private LocalDate paymentDate;

    private boolean isPaid;

}
