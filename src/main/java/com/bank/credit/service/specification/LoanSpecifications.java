package com.bank.credit.service.specification;

import com.bank.credit.service.model.Loan;
import org.springframework.data.jpa.domain.Specification;

/**
 * Utility class containing static methods to build JPA Specifications
 * for filtering {@link Loan} entities based on various criteria.
 */
public final class LoanSpecifications {

    private LoanSpecifications() {
    }

    public static Specification<Loan> hasCustomerId(Long customerId) {
        return (root, query, cb) -> cb.equal(root.get("customer").get("id"), customerId);
    }

    public static Specification<Loan> hasNumberOfInstallment(int numberOfInstallment) {
        return (root, query, cb) -> cb.equal(root.get("numberOfInstallment"), numberOfInstallment);
    }

    public static Specification<Loan> isPaid(Boolean paid) {
        return (root, query, cb) -> cb.equal(root.get("isPaid"), paid);
    }
}
