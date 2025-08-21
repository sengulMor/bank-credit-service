package com.bank.credit.service.mapper;

import com.bank.credit.service.dto.CustomerDto;
import com.bank.credit.service.model.Customer;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomerMapperTest {

    // Create an instance of CustomerMapper
    private final CustomerMapper customerMapper = Mappers.getMapper(CustomerMapper.class);

    @Test
    void shouldMapCustomerToDto() {
        // Given
        Customer customer = new Customer();
        customer.setName("Name");
        customer.setSurname("Surname");
        customer.setCreditLimit(new BigDecimal(30000));
        customer.setUsedCreditLimit(new BigDecimal(5000));

        // When
        CustomerDto customerDto = customerMapper.toDto(customer);

        // Then
        assertEquals(customer.getName(), customerDto.getName());
        assertEquals(customer.getSurname(), customerDto.getSurname());
        assertEquals(customer.getCreditLimit(), customerDto.getCreditLimit());
        assertEquals(customer.getUsedCreditLimit(), customerDto.getUsedCreditLimit());
    }

    @Test
    void shouldMapDtoToCustomer() {
        // Given
        CustomerDto customerDto = new CustomerDto();
        customerDto.setName("Name");
        customerDto.setSurname("Surname");
        customerDto.setCreditLimit(new BigDecimal(30000));
        customerDto.setUsedCreditLimit(new BigDecimal(5000));

        // When
        Customer customer = customerMapper.toEntity(customerDto);

        // Then
        assertEquals(customerDto.getName(), customer.getName());
        assertEquals(customerDto.getSurname(), customer.getSurname());
        assertEquals(customerDto.getCreditLimit(), customer.getCreditLimit());
        assertEquals(customerDto.getUsedCreditLimit(), customer.getUsedCreditLimit());
    }
}
