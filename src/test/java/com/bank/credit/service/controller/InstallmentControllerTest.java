package com.bank.credit.service.controller;

import com.bank.credit.service.dto.InstallmentDto;
import com.bank.credit.service.dto.PayedInstallmentDto;
import com.bank.credit.service.service.InstallmentPaymentService;
import com.bank.credit.service.service.LoanInstallmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
class InstallmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoanInstallmentService installmentService;

    @MockitoBean
    private InstallmentPaymentService installmentPaymentService;

    private static String asJsonString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnInstallmentsList_whenLoanIdExists() throws Exception {
        // Given
        Long loanId = 1L;
        List<InstallmentDto> installments = List.of(
                new InstallmentDto(loanId, new BigDecimal("100.00"), new BigDecimal("100.00"), LocalDate.of(2024, 9, 1), LocalDate.now(), false),
                new InstallmentDto(loanId, new BigDecimal("100.00"), null, LocalDate.of(2024, 10, 1), null, false)
        );

        Mockito.when(installmentService.getByLoan(loanId)).thenReturn(installments);

        // When & Then
        mockMvc.perform(get("/installments")
                        .param("loanId", loanId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].loanId").value(loanId))
                .andExpect(jsonPath("$[0].amount").value(100.00))
                .andExpect(jsonPath("$[0].dueDate").value("2024-09-01"))
                .andExpect(jsonPath("$[0].paid").value(false));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnInstallmentsList_whenInstallmentNotExist() throws Exception {
        // Given
        Long loanId = 1L;
        Mockito.when(installmentService.getByLoan(loanId)).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/installments")
                        .param("loanId", loanId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user")
    void shouldReturnForbidden_whenUserIsNotAuthenticated() throws Exception {
        mockMvc.perform(get("/installments")
                        .param("loanId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldPayInstallment_whenValidRequest() throws Exception {
        // Given
        InstallmentDto installmentDto = new InstallmentDto(1L, new BigDecimal(1000), null, null, null, false);
        PayedInstallmentDto payedInstallmentDto = new PayedInstallmentDto(2, new BigDecimal("550"), false);

        Mockito.when(installmentPaymentService.payInstallment(any())).thenReturn(payedInstallmentDto);

        // When & Then
        mockMvc.perform(post("/installments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(installmentDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.payedInstallment").value(2))
                .andExpect(jsonPath("$.totalAmountSpent").value(550))
                .andExpect(jsonPath("$.loanPaymentComplate").value(false));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequest_whenLoanIdIsMissing() throws Exception {
        // Given
        InstallmentDto installmentDto = new InstallmentDto(null, new BigDecimal(1000), null, null, null, false);

        // When & Then
        mockMvc.perform(post("/installments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(installmentDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("loanId"))
                .andExpect(jsonPath("$[0].message").value("Loan Id is required"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequest_whenAmountIsMissing() throws Exception {
        // Given
        InstallmentDto installmentDto = new InstallmentDto(1L, null, null, null, null, false);

        // When & Then
        mockMvc.perform(post("/installments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(installmentDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("amount"))
                .andExpect(jsonPath("$[0].message").value("Loan amount is required"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequest_whenAmountLessAsMaximum() throws Exception {
        // Given
        InstallmentDto installmentDto = new InstallmentDto(1L, new BigDecimal(30), null, null, null, false);

        // When & Then
        mockMvc.perform(post("/installments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(installmentDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("amount"))
                .andExpect(jsonPath("$[0].message").value("must be greater than or equal to 100"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequest_whenAmountHasTooManyDecimalPlaces() throws Exception {
        // Given
        InstallmentDto installmentDto = new InstallmentDto(1L, new BigDecimal("300.67532"), null, null, null, false);

        // When & Then
        mockMvc.perform(post("/installments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(installmentDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("amount"))
                .andExpect(jsonPath("$[0].message").value("Amount must be a valid percentage (max 2 decimal places)"));
    }

    @Test
    @WithMockUser(roles = "USER")
        // Not ADMIN
    void shouldReturnForbidden_whenUserNotAdmin() throws Exception {
        InstallmentDto installmentDto = new InstallmentDto(1L, new BigDecimal(1000), null, null, null, false);

        mockMvc.perform(post("/installments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(installmentDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequest_whenRequestBodyIsNull() throws Exception {
        mockMvc.perform(post("/installments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }
}
