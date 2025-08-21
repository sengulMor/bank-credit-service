package com.bank.credit.service.controller;

import com.bank.credit.service.dto.CustomerDto;
import com.bank.credit.service.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {           //constructer dependency injection
        this.customerService = customerService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerDto> create(@Validated @RequestBody CustomerDto dto) {
        log.info("Saving customer with name: {}", dto.getName());
        return new ResponseEntity<>(customerService.create(dto), HttpStatus.CREATED);
    }
}
