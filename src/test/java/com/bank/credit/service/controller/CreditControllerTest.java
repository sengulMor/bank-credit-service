package com.bank.credit.service.controller;

import com.bank.credit.service.dto.CreditDto;
import com.bank.credit.service.model.Customer;
import com.bank.credit.service.repository.CustomerRepository;
import com.bank.credit.service.service.CreditService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
class CreditControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreditService creditService;

    @MockitoBean
    private CustomerRepository customerRepository;

    private static String asJsonString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateCredit_whenValidRequest() throws Exception {
        // Given
        Long customerId = 1L;
        BigDecimal creditAmount = new BigDecimal("3000");
        int installment = 6;
        BigDecimal interestRate = new BigDecimal("0.1");

        CreditDto creditDto = new CreditDto(3L, customerId, creditAmount, installment, interestRate);
        Customer customer = new Customer("name", "surname", new BigDecimal("10000"), new BigDecimal("1000"), null);

        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        Mockito.when(creditService.create(any())).thenReturn(creditDto);

        // When & Then
        mockMvc.perform(post("/credits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(creditDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").value(customerId))
                .andExpect(jsonPath("$.numberOfInstallment").value(installment));
    }

    @Test
    @WithMockUser(username = "user")
    void shouldReturnUnauthorized_whenUserIsNotAuthenticatedForCreateCredit() throws Exception {
        // Given
        Long customerId = 1L;
        BigDecimal creditAmount = new BigDecimal("3000");
        int installment = 6;
        BigDecimal interestRate = new BigDecimal("0.1");

        CreditDto creditDto = new CreditDto(3L, customerId, creditAmount, installment, interestRate);
        Customer customer = new Customer("name", "surname", new BigDecimal("10000"), new BigDecimal("1000"), null);

        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        Mockito.when(creditService.create(any())).thenReturn(creditDto);

        mockMvc.perform(post("/credits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(creditDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequest_whenRequestBodyIsNull() throws Exception {
        // When & Then
        mockMvc.perform(post("/credits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(null)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldNotCreateCredit_whenCustomerNotExist() throws Exception {
        // Given
        Long customerId = 1L;
        BigDecimal creditAmount = new BigDecimal("30000");
        int installment = 6;
        BigDecimal interestRate = new BigDecimal("0.1");

        CreditDto creditDto = new CreditDto(3L, customerId, creditAmount, installment, interestRate);
        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(post("/credits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(creditDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("customerId"))
                .andExpect(jsonPath("$[0].message").value("Customer not found with ID: " + customerId));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldNotCreateCredit_whenLimitNotAvailable() throws Exception {
        // Given
        Long customerId = 1L;
        BigDecimal creditAmount = new BigDecimal("30000");
        int installment = 6;
        BigDecimal interestRate = new BigDecimal("0.1");

        CreditDto creditDto = new CreditDto(3L, customerId, creditAmount, installment, interestRate);
        Customer customer = new Customer("name", "surname", new BigDecimal("10000"), new BigDecimal("1000"), null);

        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        // When & Then
        mockMvc.perform(post("/credits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(creditDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("loanAmount"))
                .andExpect(jsonPath("$[0].message").value("Credit limit is not sufficient"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldNotCreateCredit_whenCustomerNotFound() throws Exception {
        // Given;
        Long customerId = 1L;
        BigDecimal creditAmount = new BigDecimal("30000");
        int installment = 6;
        BigDecimal interestRate = new BigDecimal("0.1");

        CreditDto creditDto = new CreditDto(3L, customerId, creditAmount, installment, interestRate);

        // When & Then
        mockMvc.perform(post("/credits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(creditDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("customerId"))
                .andExpect(jsonPath("$[0].message").value("Customer not found with ID: " + customerId));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldNotCreateCredit_whenCreditAmountIsUnderLimit() throws Exception {
        // Given;
        Long customerId = 1L;
        BigDecimal creditAmount = new BigDecimal("30");
        int installment = 6;
        BigDecimal interestRate = new BigDecimal("0.1");

        Customer customer = new Customer("name", "surname", new BigDecimal("10000"), new BigDecimal("1000"), null);
        CreditDto creditDto = new CreditDto(3L, customerId, creditAmount, installment, interestRate);

        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        // When & Then
        mockMvc.perform(post("/credits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(creditDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("loanAmount"))
                .andExpect(jsonPath("$[0].message").value("must be greater than or equal to 100"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequest_whenCreditAmountHasTooManyDecimalPlaces() throws Exception {
        // Given;
        Long customerId = 1L;
        BigDecimal creditAmount = new BigDecimal("300.994321");
        int installment = 6;
        BigDecimal interestRate = new BigDecimal("0.1");

        Customer customer = new Customer("name", "surname", new BigDecimal("10000"), new BigDecimal("1000"), null);
        CreditDto creditDto = new CreditDto(3L, customerId, creditAmount, installment, interestRate);

        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        // When & Then
        mockMvc.perform(post("/credits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(creditDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("loanAmount"))
                .andExpect(jsonPath("$[0].message").value("Loan amount must be a valid monetary amount (max 2 decimal places)"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldRejectCredit_whenInstallmentCountIsNotAllowed() throws Exception {
        // Given;
        Long customerId = 1L;
        BigDecimal creditAmount = new BigDecimal("300");
        int installment = 23;
        BigDecimal interestRate = new BigDecimal("0.1");

        Customer customer = new Customer("name", "surname", new BigDecimal("10000"), new BigDecimal("1000"), null);
        CreditDto creditDto = new CreditDto(3L, customerId, creditAmount, installment, interestRate);

        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        // When & Then
        mockMvc.perform(post("/credits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(creditDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("numberOfInstallment"))
                .andExpect(jsonPath("$[0].message").value("Value must be one of 6, 9, 12, or 24"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequest_whenInterestRateExceedsMaximum() throws Exception {
        // Given;
        Long customerId = 1L;
        BigDecimal creditAmount = new BigDecimal("300");
        int installment = 6;
        BigDecimal interestRate = new BigDecimal("0.9");

        Customer customer = new Customer("name", "surname", new BigDecimal("10000"), new BigDecimal("1000"), null);
        CreditDto creditDto = new CreditDto(3L, customerId, creditAmount, installment, interestRate);

        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        // When & Then
        mockMvc.perform(post("/credits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(creditDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("interestRate"))
                .andExpect(jsonPath("$[0].message").value("must be less than or equal to 0.5"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequest_whenInterestRateExceedsMinimum() throws Exception {
        // Given;
        Long customerId = 1L;
        BigDecimal creditAmount = new BigDecimal("300");
        int installment = 6;
        BigDecimal interestRate = new BigDecimal("0.0");

        Customer customer = new Customer("name", "surname", new BigDecimal("10000"), new BigDecimal("1000"), null);
        CreditDto creditDto = new CreditDto(3L, customerId, creditAmount, installment, interestRate);

        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        // When & Then
        mockMvc.perform(post("/credits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(creditDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("interestRate"))
                .andExpect(jsonPath("$[0].message").value("must be greater than or equal to 0.1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequest_whenInterestRateHasTooManyDecimalPlaces() throws Exception {
        // Given;
        Long customerId = 1L;
        BigDecimal creditAmount = new BigDecimal("300");
        int installment = 6;
        BigDecimal interestRate = new BigDecimal("0.1567");

        Customer customer = new Customer("name", "surname", new BigDecimal("10000"), new BigDecimal("1000"), null);
        CreditDto creditDto = new CreditDto(3L, customerId, creditAmount, installment, interestRate);

        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        // When & Then
        mockMvc.perform(post("/credits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(creditDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("interestRate"))
                .andExpect(jsonPath("$[0].message").value("Interest rate must be a valid percentage (max 2 decimal places)"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnFilteredLoans_whenValidCustomerIdAndFiltersProvided() throws Exception {
        // Given
        Long customerId = 1L;
        int numberOfInstallment = 6;
        boolean isPaid = false;

        CreditDto credit1 = new CreditDto(3L, customerId, new BigDecimal("1000"), 6, new BigDecimal("0.1"));
        CreditDto credit2 = new CreditDto(3L, customerId, new BigDecimal("2000"), 6, new BigDecimal("0.1"));

        List<CreditDto> creditList = List.of(credit1, credit2);
        Page<CreditDto> creditPage = new PageImpl<>(creditList);

        Mockito.when(creditService.getLoanByCustomer(any(), any(Pageable.class)))
                .thenReturn(creditPage);
        // When & Then
        mockMvc.perform(get("/credits")
                        .param("customerId", customerId.toString())
                        .param("numberOfInstallment", String.valueOf(numberOfInstallment))
                        .param("isPaid", String.valueOf(isPaid)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].customerId").value(customerId))
                .andExpect(jsonPath("$.content[1].loanAmount").value(2000));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnFilteredLoans_withPaginationAndSorting() throws Exception {
        // Given
        Long customerId = 1L;
        int numberOfInstallment = 6;
        boolean isPaid = false;

        CreditDto credit1 = new CreditDto(3L, customerId, new BigDecimal("1000"), 6, new BigDecimal("0.1"));
        CreditDto credit2 = new CreditDto(3L, customerId, new BigDecimal("2000"), 6, new BigDecimal("0.1"));

        List<CreditDto> creditList = List.of(credit1, credit2);
        Page<CreditDto> creditPage = new PageImpl<>(creditList);

        Mockito.when(creditService.getLoanByCustomer(any(), any(Pageable.class)))
                .thenReturn(creditPage);
        // When & Then
        mockMvc.perform(get("/credits")
                        .param("customerId", customerId.toString())
                        .param("numberOfInstallment", String.valueOf(numberOfInstallment))
                        .param("isPaid", String.valueOf(isPaid))
                        .param("page", "1")
                        .param("size", "10")
                        .param("sort", "loanAmount,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].customerId").value(customerId))
                .andExpect(jsonPath("$.content[1].loanAmount").value(2000));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequest_whenCustomerIdIsMissing() throws Exception {
        mockMvc.perform(get("/credits")
                        .param("numberOfInstallment", "6")
                        .param("isPaid", "true"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Missing required parameter: customerId"));
    }

    @Test
    @WithMockUser(username = "user")
    void shouldReturnUnauthorized_whenUserIsNotAuthenticated() throws Exception {
        mockMvc.perform(get("/credits")
                        .param("customerId", "1"))
                .andExpect(status().isForbidden());
    }
}
