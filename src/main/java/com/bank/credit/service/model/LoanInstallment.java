package com.bank.credit.service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class LoanInstallment extends BaseEntity {

    @NotNull
    private BigDecimal amount;

    private BigDecimal paidAmount;  //bezahlterBetrag

    @NotNull
    private LocalDate dueDate;   //faelligkeitsdatum

    private LocalDate paymentDate;  //zahlungsdatum

    private boolean isPaid;

    @ManyToOne(optional = false)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

}
