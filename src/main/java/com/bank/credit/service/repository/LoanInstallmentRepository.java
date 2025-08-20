package com.bank.credit.service.repository;

import com.bank.credit.service.model.LoanInstallment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanInstallmentRepository extends JpaRepository<LoanInstallment, Long> {

    List<LoanInstallment> findByLoan_Id(Long loan_id);

    List<LoanInstallment> findByLoan_IdAndDueDateBetweenAndIsPaidFalse(Long loanId, LocalDate start, LocalDate end);

    boolean existsByLoan_IdAndIsPaidFalse(Long loanId);
}
