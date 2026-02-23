package com.af.carrsvt;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import java.util.Objects;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import jakarta.persistence.EntityNotFoundException;

import com.af.carrsvt.controller.CustomerController;
import com.af.carrsvt.dto.CustomerDto;
import com.af.carrsvt.entity.Customer;
import com.af.carrsvt.exception.GlobalExceptionHandler;
import com.af.carrsvt.mapper.CustomerMapper;
import com.af.carrsvt.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerController customerController;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(customerController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createCustomer_shouldReturnSavedCustomer() throws Exception {
        CustomerDto requestDto = new CustomerDto();
        requestDto.setFirstName("John");
        requestDto.setLastName("Doe");
        requestDto.setUsername("john");
        requestDto.setPassword("secret1");
        requestDto.setEmail("john@example.com");

        Customer entity = new Customer();
        Customer savedEntity = new Customer();
        savedEntity.setCustomerId(1L);

        CustomerDto responseDto = new CustomerDto();
        responseDto.setCustomerId(1L);
        responseDto.setFirstName("John");
        responseDto.setLastName("Doe");
        responseDto.setUsername("john");
        responseDto.setEmail("john@example.com");

        when(customerMapper.customerDtoToCustomer(any(CustomerDto.class))).thenReturn(entity);
        when(customerService.saveCustomer(entity)).thenReturn(savedEntity);
        when(customerMapper.customerToCustomerDto(savedEntity)).thenReturn(responseDto);

        mockMvc.perform(post("/api/customers/create")
                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(requestDto))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.customerId").value(1L))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
            .andExpect(jsonPath("$.username").value("john"));
    }

    @Test
    void updateCustomer_shouldReturnUpdatedCustomer() throws Exception {
        CustomerDto requestDto = new CustomerDto();
        requestDto.setFirstName("John");
        requestDto.setLastName("Updated");
        requestDto.setUsername("john-updated");
        requestDto.setPassword("secret1");
        requestDto.setEmail("john.updated@example.com");

        Customer entity = new Customer();
        Customer updatedEntity = new Customer();
        updatedEntity.setCustomerId(1L);

        CustomerDto responseDto = new CustomerDto();
        responseDto.setCustomerId(1L);
        responseDto.setFirstName("John");
        responseDto.setLastName("Updated");
        responseDto.setUsername("john-updated");
        responseDto.setEmail("john.updated@example.com");

        when(customerMapper.customerDtoToCustomer(any(CustomerDto.class))).thenReturn(entity);
        when(customerService.updateCustomer(1L, entity)).thenReturn(updatedEntity);
        when(customerMapper.customerToCustomerDto(updatedEntity)).thenReturn(responseDto);

        mockMvc.perform(put("/api/customers/1")
                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(requestDto))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.customerId").value(1L))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Updated"))
            .andExpect(jsonPath("$.username").value("john-updated"));
    }

    @Test
    void deleteCustomer_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/customers/1"))
            .andExpect(status().isNoContent());

        verify(customerService).deleteCustomer(1L);
    }

    @Test
    void createCustomer_shouldReturnBadRequest_whenPayloadInvalid() throws Exception {
        CustomerDto requestDto = new CustomerDto();
        requestDto.setFirstName("Invalid");
        requestDto.setLastName("User");
        requestDto.setUsername("");
        requestDto.setPassword("123");
        requestDto.setEmail("invalid-email");

        mockMvc.perform(post("/api/customers/create")
            .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
            .content(Objects.requireNonNull(objectMapper.writeValueAsString(requestDto))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void updateCustomer_shouldReturnNotFound_whenCustomerMissing() throws Exception {
        CustomerDto requestDto = new CustomerDto();
        requestDto.setFirstName("John");
        requestDto.setLastName("Updated");
        requestDto.setUsername("john-updated");
        requestDto.setPassword("secret1");
        requestDto.setEmail("john.updated@example.com");

        Customer entity = new Customer();

        when(customerMapper.customerDtoToCustomer(any(CustomerDto.class))).thenReturn(entity);
        when(customerService.updateCustomer(999L, entity)).thenThrow(new EntityNotFoundException("Customer not found"));

        mockMvc.perform(put("/api/customers/999")
            .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
            .content(Objects.requireNonNull(objectMapper.writeValueAsString(requestDto))))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deleteCustomer_shouldReturnNotFound_whenCustomerMissing() throws Exception {
        org.mockito.Mockito.doThrow(new EntityNotFoundException("Customer not found"))
                .when(customerService).deleteCustomer(999L);

        mockMvc.perform(delete("/api/customers/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }

}

