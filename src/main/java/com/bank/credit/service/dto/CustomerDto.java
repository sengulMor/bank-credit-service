package com.bank.credit.service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
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
public class CustomerDto {

    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Surname is required")
    private String surname;

    @NotNull(message = "Credit Limit is required")
    @DecimalMin(value = "0.0", message = "Credit limit must be greater than or equal to 0.0")
    @Digits(integer = 10, fraction = 2, message = "Credit Limit must be a valid monetary amount (max 2 decimal places)")
    private BigDecimal creditLimit;

    @NotNull(message = "Used Credit Limit is required")
    @DecimalMin(value = "0.0", message = "Used credit limit must be greater than or equal to 0.0")
    @Digits(integer = 10, fraction = 2, message = "Used Credit Limit must be a valid monetary amount (max 2 decimal places)")
    private BigDecimal usedCreditLimit;

}



