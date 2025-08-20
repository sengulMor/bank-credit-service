package com.bank.credit.service.service;

import com.bank.credit.service.dto.InstallmentDto;
import com.bank.credit.service.mapper.InstallmentMapper;
import com.bank.credit.service.model.Loan;
import com.bank.credit.service.model.LoanInstallment;
import com.bank.credit.service.repository.LoanInstallmentRepository;
import com.bank.credit.service.util.LoanCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Service responsible for managing loan installment operations.
 * <p>
 * This includes retrieving installments for a loan and generating initial installment schedules.
 * <p>
 * The service coordinates with repositories and delegates financial calculations
 * to {@link LoanCalculator}.
 */
@Service
public class LoanInstallmentService {

    private  final LoanInstallmentRepository loanInstallmentRepository;
    private  final InstallmentMapper installmentMapper;

    public LoanInstallmentService(LoanInstallmentRepository loanInstallmentRepository,
                                  InstallmentMapper installmentMapper){
        this.loanInstallmentRepository = loanInstallmentRepository;
        this.installmentMapper = installmentMapper;
    }

    @Transactional(readOnly = true)
    public List<InstallmentDto> getByLoan(Long loanId) {
        List<LoanInstallment> installments = loanInstallmentRepository.findByLoan_Id(loanId);
        return installmentMapper.toDtoList(installments);
    }

    /**
     * Builds a list of loan installments starting from the first day of the next month.
     * Each installment has an equal amount and incrementing due date.
     *
     * @param loan the loan entity the installments belong to
     * @return list of {@link LoanInstallment} objects
     */
    public List<LoanInstallment> buildLoanInstallments(Loan loan) {
        BigDecimal amount = LoanCalculator.calculateInstallmentAmount(loan.getLoanAmount(), loan.getNumberOfInstallment());
        LocalDate firstOfNextMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfNextMonth());

        return IntStream.range(0, loan.getNumberOfInstallment())
                .mapToObj(i -> {
                    LoanInstallment installment = new LoanInstallment();
                    installment.setAmount(amount);
                    installment.setDueDate(firstOfNextMonth.plusMonths(i));
                    installment.setLoan(loan);
                    return installment;
                })
                .toList();
    }
}
