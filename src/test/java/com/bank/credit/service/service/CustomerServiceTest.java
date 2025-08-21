package com.bank.credit.service.service;

import com.bank.credit.service.dto.CustomerDto;
import com.bank.credit.service.mapper.CustomerMapper;
import com.bank.credit.service.model.Customer;
import com.bank.credit.service.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class CustomerServiceTest {

    private CustomerRepository customerRepository;
    private CustomerMapper customerMapper;
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        customerRepository = mock(CustomerRepository.class);
        customerMapper = mock(CustomerMapper.class);
        customerService = new CustomerService(customerRepository, customerMapper);
    }

    @Test
    void create_shouldSaveAndReturnCustomerDto() {
        // Given
        CustomerDto inputDto = new CustomerDto();
        Customer mappedEntity = new Customer();
        Customer savedEntity = new Customer();
        savedEntity.setId(123L);
        CustomerDto returnedDto = new CustomerDto();

        when(customerMapper.toEntity(inputDto)).thenReturn(mappedEntity);
        when(customerRepository.save(mappedEntity)).thenReturn(savedEntity);
        when(customerMapper.toDto(savedEntity)).thenReturn(returnedDto);

        // When
        CustomerDto result = customerService.create(inputDto);

        // Then
        assertNotNull(result);
        assertEquals(returnedDto, result);
        verify(customerMapper).toEntity(inputDto);
        verify(customerRepository).save(mappedEntity);
        verify(customerMapper).toDto(savedEntity);
    }
}
