package com.bank.credit.service.controller;

import com.bank.credit.service.dto.InstallmentDto;
import com.bank.credit.service.dto.PayedInstallmentDto;
import com.bank.credit.service.service.InstallmentPaymentService;
import com.bank.credit.service.service.LoanInstallmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/installment")
public class InstallmentController {

    private final LoanInstallmentService installmentService;
    private final InstallmentPaymentService installmentPaymentService;


    public InstallmentController(LoanInstallmentService installmentService,
                                 InstallmentPaymentService installmentPaymentService) {
        this.installmentService = installmentService;
        this.installmentPaymentService = installmentPaymentService;
    }

    /**
     * Retrieves all installments for a specific loan.
     *
     * @param loanId the ID of the loan
     * @return list of installments
     */
    @GetMapping("/{loanId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<InstallmentDto>> getInstallmentsByLoan(@PathVariable("loanId") Long loanId) {
        log.info("Getting all installments for loan id {}", loanId);
        List<InstallmentDto> installments = installmentService.getByLoan(loanId);
        if (installments.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        return ResponseEntity.ok(installments);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PayedInstallmentDto> payInstallment(@Validated @RequestBody InstallmentDto dto) {
        log.info("Paying installment for loan id {}", dto.getLoanId());
        return new ResponseEntity<>(installmentPaymentService.payInstallment(dto), HttpStatus.CREATED);
    }
}