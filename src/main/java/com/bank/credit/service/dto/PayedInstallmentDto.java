package com.bank.credit.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayedInstallmentDto {

    private int payedInstallment;

    private BigDecimal totalAmountSpent;

    private boolean loanPaymentComplate;
}
