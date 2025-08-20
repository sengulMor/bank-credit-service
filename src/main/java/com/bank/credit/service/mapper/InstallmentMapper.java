package com.bank.credit.service.mapper;

import com.bank.credit.service.dto.InstallmentDto;
import com.bank.credit.service.model.LoanInstallment;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InstallmentMapper {

    List<InstallmentDto> toDtoList(List<LoanInstallment> installments);

}
