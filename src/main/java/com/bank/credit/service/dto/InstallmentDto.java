package com.bank.credit.service.dto;

import com.bank.credit.service.model.Loan;
import com.bank.credit.service.validation.IsLimitAvailable;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InstallmentDto {

    @NotNull(message = "Loan Id is required")
    private Long loanId;

    @Column(precision = 15, scale = 2)
    @NotNull(message = "Loan amount is required")
    @DecimalMin(value = "100")
    private BigDecimal amount;

    private BigDecimal paidAmount;

    private LocalDate dueDate;

    private LocalDate paymentDate;

    private boolean isPaid;

}
