package com.bank.credit.service.dto;

import com.bank.credit.service.validation.InstallmentValue;
import com.bank.credit.service.validation.IsLimitAvailable;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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

    @NotNull(message = "Customer Id is required")
    private Long customerId;

    @Column(precision = 15, scale = 2)
    @NotNull(message = "Loan amount is required")
    @DecimalMin(value = "100")
    private BigDecimal loanAmount;

    @InstallmentValue(values = {6, 9, 12, 24}, message = "Value must be one of 6, 9, 12, or 24")
    private int numberOfInstallment;

    @NotNull(message = "Interest Rate is required")
    @DecimalMin(value = "0.1")
    @DecimalMax(value = "0.5")
    private BigDecimal interestRate;
}




