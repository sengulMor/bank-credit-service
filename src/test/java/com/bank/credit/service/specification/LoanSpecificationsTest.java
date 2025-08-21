package com.bank.credit.service.specification;

import com.bank.credit.service.model.Loan;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class LoanSpecificationsTest {

    @Test
    void hasCustomerId_shouldReturnCorrectPredicate() {
        // Given
        Long customerId = 1L;
        Root<Loan> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Path<Object> customerPath = mock(Path.class);
        Path<Object> idPath = mock(Path.class);
        Predicate predicate = mock(Predicate.class);

        when(root.get("customer")).thenReturn(customerPath);
        when(customerPath.get("id")).thenReturn(idPath);
        when(cb.equal(idPath, customerId)).thenReturn(predicate);

        // When
        Specification<Loan> specification = LoanSpecifications.hasCustomerId(customerId);
        Predicate result = specification.toPredicate(root, query, cb);

        // Then
        assertEquals(predicate, result);
        verify(cb).equal(idPath, customerId);
    }

    @Test
    void hasNumberOfInstallment_shouldReturnCorrectPredicate() {
        // Given
        int numberOfInstallments = 12;
        Root<Loan> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Path<Object> path = mock(Path.class);
        Predicate predicate = mock(Predicate.class);

        when(root.get("numberOfInstallment")).thenReturn(path);
        when(cb.equal(path, numberOfInstallments)).thenReturn(predicate);

        // When
        Specification<Loan> specification = LoanSpecifications.hasNumberOfInstallment(numberOfInstallments);
        Predicate result = specification.toPredicate(root, query, cb);

        // Then
        assertEquals(predicate, result);
        verify(cb).equal(path, numberOfInstallments);
    }

    @Test
    void isPaid_shouldReturnCorrectPredicate() {
        // Given
        boolean isPaid = true;
        Root<Loan> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Path<Object> path = mock(Path.class);
        Predicate predicate = mock(Predicate.class);

        when(root.get("isPaid")).thenReturn(path);
        when(cb.equal(path, isPaid)).thenReturn(predicate);

        // When
        Specification<Loan> specification = LoanSpecifications.isPaid(isPaid);
        Predicate result = specification.toPredicate(root, query, cb);

        // Then
        assertEquals(predicate, result);
        verify(cb).equal(path, isPaid);
    }
}
