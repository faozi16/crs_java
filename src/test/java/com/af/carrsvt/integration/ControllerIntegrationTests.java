package com.af.carrsvt.integration;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.af.carrsvt.dto.CustomerDto;
import com.af.carrsvt.dto.PaymentMethodDto;
import com.af.carrsvt.repository.CustomerRepository;
import com.af.carrsvt.repository.PaymentMethodRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(ContainerConfiguration.class)
class ControllerIntegrationTests {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(Objects.requireNonNull(context)).build();
        paymentMethodRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @Test
    void testCreateCustomerViaController() throws Exception {
        CustomerDto dto = new CustomerDto();
        dto.setUsername("apiuser");
        dto.setPassword("apipass123");
        dto.setEmail("apiuser@test.com");
        dto.setPhoneNumber("555-1111");
        dto.setStatus("A");

        String requestJson = Objects.requireNonNull(objectMapper.writeValueAsString(dto));

        mockMvc.perform(post("/api/customers/create")
                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username", Objects.requireNonNull(equalTo("apiuser"))))
            .andExpect(jsonPath("$.email", Objects.requireNonNull(equalTo("apiuser@test.com"))))
            .andExpect(jsonPath("$.customerId", Objects.requireNonNull(notNullValue())));
    }

    @Test
    void testCustomerValidationErrors() throws Exception {
        CustomerDto dto = new CustomerDto();
        dto.setUsername("user");
        // password too short
        dto.setPassword("123");
        // email invalid
        dto.setEmail("not-an-email");

        String requestJson = Objects.requireNonNull(objectMapper.writeValueAsString(dto));

        mockMvc.perform(post("/api/customers/create")
                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .content(requestJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors", Objects.requireNonNull(notNullValue())));
    }

    @Test
    void testGetCustomerById() throws Exception {
        // First create a customer
        CustomerDto dto = new CustomerDto();
        dto.setUsername("queryuser");
        dto.setPassword("querypass123");
        dto.setEmail("query@test.com");
        dto.setPhoneNumber("555-2222");
        dto.setStatus("A");

        String requestJson = Objects.requireNonNull(objectMapper.writeValueAsString(dto));

        String response = mockMvc.perform(post("/api/customers/create")
            .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
            .content(requestJson))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        CustomerDto created = objectMapper.readValue(response, CustomerDto.class);
        Long customerId = created.getCustomerId();

        // Now retrieve it
        mockMvc.perform(get("/api/customers/" + customerId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username", Objects.requireNonNull(equalTo("queryuser"))))
            .andExpect(jsonPath("$.customerId", Objects.requireNonNull(equalTo(customerId.intValue()))));
    }

    @Test
    void testCreatePaymentMethodViaController() throws Exception {
        // Create customer first
        CustomerDto custDto = new CustomerDto();
        custDto.setUsername("pmuser");
        custDto.setPassword("pmpass123");
        custDto.setEmail("pm@test.com");
        custDto.setPhoneNumber("555-3333");
        custDto.setStatus("A");

        String customerRequestJson = Objects.requireNonNull(objectMapper.writeValueAsString(custDto));

        String custResponse = mockMvc.perform(post("/api/customers/create")
            .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
            .content(customerRequestJson))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        CustomerDto created = objectMapper.readValue(custResponse, CustomerDto.class);
        Long customerId = created.getCustomerId();

        // Create payment method
        PaymentMethodDto pmDto = new PaymentMethodDto();
        pmDto.setCustomerId(customerId);
        pmDto.setMethodType("CARD");
        pmDto.setDetails("****4567");
        pmDto.setPrimaryMethod(true);

        String paymentMethodRequestJson = Objects.requireNonNull(objectMapper.writeValueAsString(pmDto));

        mockMvc.perform(post("/api/payment-methods/create")
                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .content(paymentMethodRequestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.methodType", Objects.requireNonNull(equalTo("CARD"))))
            .andExpect(jsonPath("$.paymentMethodId", Objects.requireNonNull(notNullValue())));
    }

    @Test
    void testGetPaymentMethodsByCustomer() throws Exception {
        // Create customer
        CustomerDto custDto = new CustomerDto();
        custDto.setUsername("pmquser");
        custDto.setPassword("pmqpass123");
        custDto.setEmail("pmq@test.com");
        custDto.setPhoneNumber("555-4444");
        custDto.setStatus("A");

        String customerRequestJson = Objects.requireNonNull(objectMapper.writeValueAsString(custDto));

        String custResponse = mockMvc.perform(post("/api/customers/create")
            .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
            .content(customerRequestJson))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        CustomerDto created = objectMapper.readValue(custResponse, CustomerDto.class);
        Long customerId = created.getCustomerId();

        // Create payment method
        PaymentMethodDto pmDto = new PaymentMethodDto();
        pmDto.setCustomerId(customerId);
        pmDto.setMethodType("PAYPAL");
        pmDto.setDetails("pmq@paypal.com");
        pmDto.setPrimaryMethod(false);

        String paymentMethodRequestJson = Objects.requireNonNull(objectMapper.writeValueAsString(pmDto));

        mockMvc.perform(post("/api/payment-methods/create")
            .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
            .content(paymentMethodRequestJson))
            .andExpect(status().isOk());

        // Query by customer ID
        mockMvc.perform(get("/api/payment-methods/get?customerId=" + customerId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].methodType", Objects.requireNonNull(equalTo("PAYPAL"))))
            .andExpect(jsonPath("$", Objects.requireNonNull(hasSize(1))));
    }

    @Test
    void testEntityNotFoundError() throws Exception {
        mockMvc.perform(get("/api/customers/99999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error", Objects.requireNonNull(equalTo("Not Found"))))
            .andExpect(jsonPath("$.message", Objects.requireNonNull(containsString("Customer not found"))));
    }
}
