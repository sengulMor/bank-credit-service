package com.bank.credit.service.mapper;

import com.bank.credit.service.dto.CustomerDto;
import com.bank.credit.service.model.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    CustomerDto toDto(Customer customer);

    Customer toEntity(CustomerDto dto);

}
