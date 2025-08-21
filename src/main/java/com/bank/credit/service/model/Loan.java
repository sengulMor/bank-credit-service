package com.bank.credit.service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Loan extends BaseEntity {

    @NotNull
    @Column(precision = 15, scale = 2)
    private BigDecimal loanAmount;

    @NotNull
    private Integer numberOfInstallment;

    @NotNull
    @Column(precision = 7, scale = 6)
    private BigDecimal interestRate;

    private boolean isPaid;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    @NotEmpty(message = "Loan must have at least six installment")
    private List<LoanInstallment> installments = new ArrayList<>();
}