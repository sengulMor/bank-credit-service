package com.bank.credit.service.dto;

import com.bank.credit.service.validation.InstallmentValue;
import com.bank.credit.service.validation.IsLimitAvailable;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IsLimitAvailable
public class CreditDto {

    private Long id;

    @NotNull(message = "Customer Id is required")
    private Long customerId;


    @NotNull(message = "Loan amount is required")
    @DecimalMin(value = "100")
    @Digits(integer = 10, fraction = 2, message = "Loan amount must be a valid monetary amount (max 2 decimal places)")
    private BigDecimal loanAmount;

    @InstallmentValue(values = {6, 9, 12, 24}, message = "Value must be one of 6, 9, 12, or 24")
    private int numberOfInstallment;

    @NotNull(message = "Interest Rate is required")
    @DecimalMin(value = "0.1")
    @DecimalMax(value = "0.5")
    @Digits(integer = 1, fraction = 2, message = "Interest rate must be a valid percentage (max 2 decimal places)")
    private BigDecimal interestRate;
}




