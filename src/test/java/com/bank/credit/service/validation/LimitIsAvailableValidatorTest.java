package com.bank.credit.service.validation;

import com.bank.credit.service.dto.CreditDto;
import com.bank.credit.service.model.Customer;
import com.bank.credit.service.repository.CustomerRepository;
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
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
class LimitIsAvailableValidatorTest {

    @Autowired
    private MockMvc mockMvc;

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
    void shouldReturnBadRequest_whenRequestBodyIsNull() throws Exception {
        // When & Then
        mockMvc.perform(post("/credits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(null)))
                .andExpect(status().isBadRequest());
    }

}

