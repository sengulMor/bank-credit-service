package com.bank.credit.service.controller;

import com.bank.credit.service.dto.CustomerDto;
import com.bank.credit.service.service.CustomerService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    private static String asJsonString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateCustomer_whenValidRequest() throws Exception {
        CustomerDto requestDto = new CustomerDto(null, "Name", "Surname", new BigDecimal(30000), new BigDecimal(0));
        CustomerDto responseDto = new CustomerDto(1L, "Name", "Surname", new BigDecimal(30000), new BigDecimal(0));
        Mockito.when(customerService.create(any())).thenReturn(responseDto);
        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Name"))
                .andExpect(jsonPath("$.surname").value("Surname"))
                .andExpect(jsonPath("$.creditLimit").value("30000"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequest_whenMissingName() throws Exception {
        CustomerDto invalidDto = new CustomerDto(1L, "", "Surname", new BigDecimal(30000), new BigDecimal(0));
        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("name"))
                .andExpect(jsonPath("$[0].message").value("Name is required"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequest_whenMissingSurname() throws Exception {
        CustomerDto invalidDto = new CustomerDto(1L, "Hans", "", new BigDecimal(30000), new BigDecimal(0));
        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("surname"))
                .andExpect(jsonPath("$[0].message").value("Surname is required"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequest_whenMissingCreditLimit() throws Exception {
        CustomerDto invalidDto = new CustomerDto(1L, "Hans", "Surname", null, new BigDecimal(0));
        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("creditLimit"))
                .andExpect(jsonPath("$[0].message").value("Credit Limit is required"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequest_whenCreditLimitIsLess() throws Exception {
        CustomerDto invalidDto = new CustomerDto(1L, "Hans", "Surname", new BigDecimal(-3000), new BigDecimal(0));
        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("creditLimit"))
                .andExpect(jsonPath("$[0].message").value("Credit limit must be greater than or equal to 0.0"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequest_whenCreditLimitNotAcceptedDigit() throws Exception {
        CustomerDto invalidDto = new CustomerDto(1L, "Hans", "Surname", new BigDecimal("1234.567"), new BigDecimal(0));
        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("creditLimit"))
                .andExpect(jsonPath("$[0].message").value("Credit Limit must be a valid monetary amount (max 2 decimal places)"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequest_whenMissingUsedCreditLimit() throws Exception {
        CustomerDto invalidDto = new CustomerDto(1L, "Hans", "Surname", new BigDecimal(2000), null);
        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("usedCreditLimit"))
                .andExpect(jsonPath("$[0].message").value("Used Credit Limit is required"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequest_whenUsedCreditLimitIsLess() throws Exception {
        CustomerDto invalidDto = new CustomerDto(1L, "Hans", "Surname", new BigDecimal(30000), new BigDecimal(-2000));
        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("usedCreditLimit"))
                .andExpect(jsonPath("$[0].message").value("Used credit limit must be greater than or equal to 0.0"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequest_whenUsedCreditLimitNotAcceptedDigit() throws Exception {
        CustomerDto invalidDto = new CustomerDto(1L, "Hans", "Surname", new BigDecimal(30000), new BigDecimal("1234.567"));
        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("usedCreditLimit"))
                .andExpect(jsonPath("$[0].message").value("Used Credit Limit must be a valid monetary amount (max 2 decimal places)"));
    }

    @Test
    @WithMockUser(username = "user")
    void shouldReturnForbidden_whenUserRoleIsNotAdmin() throws Exception {
        CustomerDto requestDto = new CustomerDto(null, "Hans", "Meier", new BigDecimal(30000), new BigDecimal(0));

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().isForbidden());
    }
}
