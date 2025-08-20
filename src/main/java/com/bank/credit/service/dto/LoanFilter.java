package com.bank.credit.service.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoanFilter {

    private Long customerId;
    private Integer numberOfInstallment;
    private Boolean isPaid;
}
