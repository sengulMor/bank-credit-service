package com.bank.credit.service.service;

import com.bank.credit.service.dto.CustomerDto;
import com.bank.credit.service.mapper.CustomerMapper;
import com.bank.credit.service.model.Customer;
import com.bank.credit.service.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    /**
     * Creates a new customer from the provided DTO.
     *
     * @param dto the customer data transfer object
     * @return the created customer as a DTO
     */
    @Transactional
    public CustomerDto create(CustomerDto dto) {
        Customer customer = customerMapper.toEntity(dto);
        Customer savedCustomer = customerRepository.save(customer);
        log.info("Customer created with id: {}", savedCustomer.getId());
        return customerMapper.toDto(savedCustomer);
    }
}


