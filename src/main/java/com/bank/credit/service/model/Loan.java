package com.bank.credit.service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Loan extends BaseEntity {

    
    @OneToMany(mappedBy = "loan", cascade = CascadeType.PERSIST)
    @NotEmpty(message = "Loan must have at least six installment")
    private final List<LoanInstallment> installments = new ArrayList<>();
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
    //Maps the foreign key column to the customer table.
    private Customer customer;

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }

    public void setNumberOfInstallment(Integer numberOfInstallment) {
        this.numberOfInstallment = numberOfInstallment;
    }

    public List<LoanInstallment> getInstallments() {
        return Collections.unmodifiableList(installments);
    }

    public void addInstallments(List<LoanInstallment> newInstallments) {
        if (newInstallments != null && !newInstallments.isEmpty()) {
            this.installments.addAll(newInstallments);
        }
    }

}