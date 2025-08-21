package com.bank.credit.service.controller;

import com.bank.credit.service.dto.CreditDto;
import com.bank.credit.service.dto.LoanFilter;
import com.bank.credit.service.service.CreditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/credits")
public class CreditController {

    private final CreditService creditService;

    public CreditController(CreditService creditService) {
        this.creditService = creditService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CreditDto> create(@Validated @RequestBody CreditDto dto) {
        log.info("Credit for customer id {} create", dto.getCustomerId());
        return new ResponseEntity<>(creditService.create(dto), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Page<CreditDto> getFilteredLoansByCustomer(@RequestParam Long customerId,
                                                      @RequestParam(required = false) Integer numberOfInstallment,
                                                      @RequestParam(required = false) Boolean isPaid,
                                                      @PageableDefault(sort = "loanAmount", direction = Sort.Direction.ASC) Pageable pageable) {
        log.info("Get all loans for customer {}, filters: numberOfInstallment={}, isPaid={}",
                customerId, numberOfInstallment, isPaid);
        LoanFilter filter = LoanFilter.builder()
                .customerId(customerId)
                .numberOfInstallment(numberOfInstallment)
                .isPaid(isPaid)
                .build();
        return creditService.getLoanByCustomer(filter, pageable);
    }

}