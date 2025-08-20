package com.bank.credit.service.validation;

import com.bank.credit.service.dto.CreditDto;
import com.bank.credit.service.model.Customer;
import com.bank.credit.service.repository.CustomerRepository;
import com.bank.credit.service.util.LoanCalculator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class LimitIsAvailableValidator implements ConstraintValidator<IsLimitAvailable, CreditDto>  {

    private final CustomerRepository customerRepository;

    public LimitIsAvailableValidator(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public boolean isValid(CreditDto dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true;
        }
        Customer customer = getCustomer(dto.getCustomerId(), context);
        if (customer == null) {
            return false;
        }
        BigDecimal available = LoanCalculator.calculateAvailableLimit(customer.getCreditLimit(), customer.getUsedCreditLimit());
        BigDecimal totalAmount = LoanCalculator.calculateTotalRepayment(dto.getLoanAmount(), dto.getInterestRate());
        if (available.compareTo(totalAmount) < 0) {
            addViolation(context, "loanAmount", "{loan.amount.limit}");
            return false;
        }
        return true;
    }

    private Customer getCustomer(Long customerId, ConstraintValidatorContext context) {
        return customerRepository.findById(customerId).orElseGet(() -> {
            addViolation(context, "customerId","Customer not found with ID: " + customerId);
            return null;
        });
    }

    private void addViolation(ConstraintValidatorContext context, String field, String messageKey) {
        context.disableDefaultConstraintViolation(); // <--- Required!
        context.buildConstraintViolationWithTemplate(messageKey)
                .addPropertyNode(field)
                .addConstraintViolation();
    }
}
