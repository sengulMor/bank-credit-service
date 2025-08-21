package com.bank.credit.service.service;

import com.bank.credit.service.dto.InstallmentDto;
import com.bank.credit.service.dto.PayedInstallmentDto;
import com.bank.credit.service.exception.CustomerNotFoundException;
import com.bank.credit.service.exception.InvalidPaymentAmountException;
import com.bank.credit.service.exception.UnpaidInstallmentsNotFoundException;
import com.bank.credit.service.model.Customer;
import com.bank.credit.service.model.Loan;
import com.bank.credit.service.model.LoanInstallment;
import com.bank.credit.service.repository.CustomerRepository;
import com.bank.credit.service.repository.LoanInstallmentRepository;
import com.bank.credit.service.repository.LoanRepository;
import com.bank.credit.service.util.LoanCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Service responsible for managing loan installment payments.
 * <p>
 * This includes processing installment payments, updating customer credit limit
 * and determining if a loan has been fully paid.
 * <p>
 * The service coordinates with repositories and delegates financial calculations
 * to {@link LoanCalculator}.
 */
@Service
public class InstallmentPaymentService {

    private final LoanInstallmentRepository loanInstallmentRepository;
    private final CustomerRepository customerRepository;
    private final LoanRepository loanRepository;

    public InstallmentPaymentService(LoanInstallmentRepository loanInstallmentRepository,
                                     CustomerRepository customerRepository,
                                     LoanRepository loanRepository) {
        this.loanInstallmentRepository = loanInstallmentRepository;
        this.customerRepository = customerRepository;
        this.loanRepository = loanRepository;
    }

    /**
     * Processes a loan installment payment.
     * <p>
     * It calculates how many installments the given amount can cover, marks them as paid,
     * updates the customer's used credit limit, and checks if the loan is now fully paid.
     *
     * @param dto the installment payment data containing loan ID and payment amount
     * @return a {@link PayedInstallmentDto} containing summary info about the payment
     * @throws InvalidPaymentAmountException       if the amount cannot cover at least one installment
     * @throws UnpaidInstallmentsNotFoundException if no unpaid installments are found in the next 3 months
     */
    @Transactional
    public PayedInstallmentDto payInstallment(InstallmentDto dto) {
        Long loanId = dto.getLoanId();
        LocalDate paymentDate = LocalDate.now();
        List<LoanInstallment> unpaidInstallments = getUnpaidInstallments(loanId, paymentDate);

        BigDecimal installmentAmount = unpaidInstallments.get(0).getAmount();
        int numToPay = LoanCalculator.calculateInstallmentsToPay(dto.getAmount(), installmentAmount);
        if (numToPay == 0) {
            throw new InvalidPaymentAmountException(loanId);
        }

        List<LoanInstallment> toPay = prepareInstallmentsAsPaid(unpaidInstallments, numToPay, paymentDate);
        loanInstallmentRepository.saveAll(toPay);

        BigDecimal totalPayment = LoanCalculator.calculateTotalPayment(installmentAmount, toPay.size());
        updateCustomerCreditLimit(loanId, totalPayment);
        boolean paymentCompleted = loanPaymentCompleted(loanId, unpaidInstallments);

        return buildPaymentInstallmentDto(toPay.size(), totalPayment, paymentCompleted);
    }

    /**
     * Retrieves unpaid installments for a given loan within the next 3 months.
     *
     * @param loanId      the loan ID
     * @param paymentDate the reference date (usually today)
     * @return a list of unpaid {@link LoanInstallment}s
     * @throws UnpaidInstallmentsNotFoundException if no unpaid installments are found in the period
     */
    private List<LoanInstallment> getUnpaidInstallments(Long loanId, LocalDate paymentDate) {
        List<LoanInstallment> unpaidInstallments = loanInstallmentRepository
                .findByLoan_IdAndDueDateBetweenAndIsPaidFalse(loanId, paymentDate, paymentDate.plusMonths(3));
        if (unpaidInstallments.isEmpty()) {
            throw new UnpaidInstallmentsNotFoundException(loanId);
        }
        return unpaidInstallments;
    }

    /**
     * Marks the specified number of unpaid installments as paid with today's date.
     *
     * @param installments the list of unpaid installments
     * @param numToPay     how many installments to mark as paid
     * @param paymentDate  the date of payment
     * @return a list of installments marked as paid
     */
    private List<LoanInstallment> prepareInstallmentsAsPaid(List<LoanInstallment> installments, int numToPay, LocalDate paymentDate) {
        return installments.stream()
                .limit(numToPay)
                .peek(inst -> {
                    inst.setPaidAmount(inst.getAmount());
                    inst.setPaymentDate(paymentDate);
                    inst.setPaid(true);
                })
                .toList();
    }

    /**
     * Updates the customer's used credit limit by subtracting the total payment.
     *
     * @param loanId       the loan ID whose customer will be updated
     * @param totalPayment the total amount paid toward installments
     */
    private void updateCustomerCreditLimit(Long loanId, BigDecimal totalPayment) {
        Customer customer = customerRepository.findByLoans_Id(loanId)
                .orElseThrow(CustomerNotFoundException::new);

        BigDecimal newLimit = customer.getUsedCreditLimit().subtract(totalPayment);
        customer.setUsedCreditLimit(newLimit);
        customerRepository.save(customer);
    }

    /**
     * Checks if the loan has been fully paid.
     * <p>
     * If no unpaid installments remain, the loan is marked as fully paid.
     *
     * @param loanId             the loan ID
     * @param unpaidInstallments the previously fetched unpaid installments
     * @return true if the loan is now fully paid, false otherwise
     */
    private boolean loanPaymentCompleted(Long loanId, List<LoanInstallment> unpaidInstallments) {
        boolean stillUnpaid = loanInstallmentRepository.existsByLoan_IdAndIsPaidFalse(loanId);
        if (!stillUnpaid) {
            Loan loan = unpaidInstallments.get(0).getLoan();
            loan.setPaid(true);
            loanRepository.save(loan);
        }
        return !stillUnpaid;
    }

    /**
     * Builds a response DTO summarizing the installment payment.
     *
     * @param numPaid      number of installments that were paid
     * @param totalPayment total amount paid
     * @param allPaid      whether the entire loan has been paid off
     * @return a {@link PayedInstallmentDto} DTO
     */
    private PayedInstallmentDto buildPaymentInstallmentDto(int numPaid, BigDecimal totalPayment, boolean allPaid) {
        PayedInstallmentDto dto = new PayedInstallmentDto();
        dto.setPayedInstallment(numPaid);
        dto.setTotalAmountSpent(totalPayment);
        dto.setLoanPaymentComplate(allPaid);
        return dto;
    }
}
