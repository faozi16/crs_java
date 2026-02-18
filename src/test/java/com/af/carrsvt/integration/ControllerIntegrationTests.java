package com.af.carrsvt.integration;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
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

        mockMvc.perform(post("/api/customers/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username", equalTo("apiuser")))
            .andExpect(jsonPath("$.email", equalTo("apiuser@test.com")))
            .andExpect(jsonPath("$.customerId", notNullValue()));
    }

    @Test
    void testCustomerValidationErrors() throws Exception {
        CustomerDto dto = new CustomerDto();
        dto.setUsername("user");
        // password too short
        dto.setPassword("123");
        // email invalid
        dto.setEmail("not-an-email");

        mockMvc.perform(post("/api/customers/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors", notNullValue()));
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

        String response = mockMvc.perform(post("/api/customers/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        CustomerDto created = objectMapper.readValue(response, CustomerDto.class);
        Long customerId = created.getCustomerId();

        // Now retrieve it
        mockMvc.perform(get("/api/customers/" + customerId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username", equalTo("queryuser")))
            .andExpect(jsonPath("$.customerId", equalTo(customerId.intValue())));
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

        String custResponse = mockMvc.perform(post("/api/customers/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(custDto)))
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

        mockMvc.perform(post("/api/payment-methods/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pmDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.methodType", equalTo("CARD")))
            .andExpect(jsonPath("$.paymentMethodId", notNullValue()));
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

        String custResponse = mockMvc.perform(post("/api/customers/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(custDto)))
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

        mockMvc.perform(post("/api/payment-methods/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pmDto)))
            .andExpect(status().isOk());

        // Query by customer ID
        mockMvc.perform(get("/api/payment-methods/get?customerId=" + customerId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].methodType", equalTo("PAYPAL")))
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void testEntityNotFoundError() throws Exception {
        mockMvc.perform(get("/api/customers/99999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error", equalTo("Not Found")))
            .andExpect(jsonPath("$.message", containsString("Customer not found")));
    }
}
