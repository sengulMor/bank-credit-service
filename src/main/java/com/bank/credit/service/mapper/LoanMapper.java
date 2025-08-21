package com.bank.credit.service.mapper;

import com.bank.credit.service.dto.CreditDto;
import com.bank.credit.service.model.Customer;
import com.bank.credit.service.model.Loan;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface LoanMapper {

    @Mapping(target = "customerId", source = "customer.id")
    CreditDto toDto(Loan loan);

    @Mapping(target = "customer", expression = "java(customer)")
    @Mapping(target = "loanAmount", source = "totalAmount")
    Loan toEntity(CreditDto dto, @Context Customer customer, BigDecimal totalAmount);  //Use @Context to tell MapStruct not to look inside the DTO for this value — you’re injecting it manually.

}