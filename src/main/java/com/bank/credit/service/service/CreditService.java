package com.bank.credit.service.service;

import com.bank.credit.service.dto.CreditDto;
import com.bank.credit.service.dto.LoanFilter;
import com.bank.credit.service.exception.CustomerNotFoundException;
import com.bank.credit.service.mapper.LoanMapper;
import com.bank.credit.service.model.Customer;
import com.bank.credit.service.model.Loan;
import com.bank.credit.service.model.LoanInstallment;
import com.bank.credit.service.repository.CustomerRepository;
import com.bank.credit.service.repository.LoanRepository;
import com.bank.credit.service.specification.LoanSpecifications;
import com.bank.credit.service.util.LoanCalculator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;


/**
 * Service responsible for managing loan creation and retrieval operations.
 * <p>
 * This service handles the lifecycle of credit applications by:
 * <ul>
 *   <li>Creating new loans with associated installments</li>
 *   <li>Updating customer credit usage</li>
 *   <li>Retrieving loans using filtering and pagination</li>
 * </ul>
 * It relies on {@link LoanInstallmentService} for installment schedule generation
 * and delegates entity-to-DTO transformations to {@link LoanMapper}.
 */
@Service
public class CreditService {

    private final LoanRepository loanRepository;
    private final LoanMapper loanMapper;
    private final CustomerRepository  customerRepository;
    private final LoanInstallmentService loanInstallmentService;

    public CreditService(LoanRepository loanRepository,
                         LoanMapper loanMapper,
                         CustomerRepository  customerRepository,
                         LoanInstallmentService loanInstallmentService) {
        this.loanRepository = loanRepository;
        this.loanMapper = loanMapper;
        this.customerRepository = customerRepository;
        this.loanInstallmentService = loanInstallmentService;
    }

    /**
     * Creates a new loan for the specified customer and saves the corresponding loan installments.
     * Also updates the customer's used credit limit.
     *
     * @param dto the credit request details
     * @return the saved loan as a CreditDto
     * @throws CustomerNotFoundException if the customer does not exist
     */
    @Transactional
    public CreditDto create(CreditDto dto) {
        BigDecimal totalAmount = LoanCalculator.calculateTotalRepayment(dto.getLoanAmount(), dto.getInterestRate());
        Customer customer = getCustomer(dto);
        Loan loan = loanMapper.toEntity(dto, customer, totalAmount);
        List<LoanInstallment> installments = loanInstallmentService.buildLoanInstallments(loan);
        loan.getInstallments().addAll(installments);
        Loan savedLoan = loanRepository.save(loan);
        updateUsedCreditLimit(totalAmount, customer);
        return loanMapper.toDto(savedLoan);
    }

    /**
     * Updates the customer's used credit limit.
     *
     * @param totalAmount amount with interestRate
     * @param customer the customer details
     */
    private void updateUsedCreditLimit(BigDecimal totalAmount, Customer customer) {
        BigDecimal updatedUsedLimit = customer.getUsedCreditLimit().add(totalAmount);
        customer.setUsedCreditLimit(updatedUsedLimit);
        customerRepository.save(customer);
    }

    /**
     * Gets the customer
     *
     * @param dto the credit request details
     * @throws CustomerNotFoundException if the customer does not exist
     */
    private Customer getCustomer(CreditDto dto) {
       return customerRepository.findById(dto.getCustomerId())
               .orElseThrow(() -> new CustomerNotFoundException(dto.getCustomerId()));
    }

    /**
     * Retrieves a paginated list of loans for a specific customer, applying optional filters
     * for number of installments and payment status.
     *
     * @param filter   the loan filter containing optional criteria like number of installments and isPaid
     * @param pageable the pagination and sorting information
     * @return a page of {@link CreditDto} matching the given filters
     *
     * @throws IllegalArgumentException if customerId in the filter is null
     */
    @Transactional(readOnly = true)
    public Page<CreditDto> getLoanByCustomer(LoanFilter filter, Pageable pageable) {
        Specification<Loan> spec = LoanSpecifications.hasCustomerId(filter.getCustomerId());
        if (filter.getNumberOfInstallment() != null) {
            spec = spec.and(LoanSpecifications.hasNumberOfInstallment(filter.getNumberOfInstallment()));
        }
        if (filter.getIsPaid() != null) {
            spec = spec.and(LoanSpecifications.isPaid(filter.getIsPaid()));
        }
        Page<Loan> loans = loanRepository.findAll(spec, pageable);
        return loans.map(loanMapper::toDto);
    }
}
