package com.af.carrsvt;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import jakarta.persistence.EntityNotFoundException;

import com.af.carrsvt.controller.PaymentMethodController;
import com.af.carrsvt.dto.PaymentMethodDto;
import com.af.carrsvt.entity.PaymentMethod;
import com.af.carrsvt.exception.GlobalExceptionHandler;
import com.af.carrsvt.mapper.PaymentMethodMapper;
import com.af.carrsvt.service.PaymentMethodService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class PaymentMethodControllerTest {

    @Mock
    private PaymentMethodService paymentMethodService;

    @Mock
    private PaymentMethodMapper paymentMethodMapper;

    @InjectMocks
    private PaymentMethodController paymentMethodController;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentMethodController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createPaymentMethod_shouldReturnSavedPaymentMethod() throws Exception {
        PaymentMethodDto requestDto = new PaymentMethodDto();
        requestDto.setCustomerId(1L);
        requestDto.setMethodType("CARD");
        requestDto.setDetails("****1111");

        PaymentMethod entity = new PaymentMethod();
        PaymentMethod savedEntity = new PaymentMethod();
        savedEntity.setPaymentMethodId(1L);

        PaymentMethodDto responseDto = new PaymentMethodDto();
        responseDto.setPaymentMethodId(1L);
        responseDto.setMethodType("CARD");

        when(paymentMethodMapper.paymentMethodDtoToPaymentMethod(any(PaymentMethodDto.class))).thenReturn(entity);
        when(paymentMethodService.savePaymentMethod(entity)).thenReturn(savedEntity);
        when(paymentMethodMapper.paymentMethodToPaymentMethodDto(savedEntity)).thenReturn(responseDto);

        mockMvc.perform(post("/api/payment-methods/create")
            .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
            .content(Objects.requireNonNull(objectMapper.writeValueAsString(requestDto))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.paymentMethodId").value(1L));
    }

    @Test
    void updatePaymentMethod_shouldReturnUpdatedPaymentMethod() throws Exception {
        PaymentMethodDto requestDto = new PaymentMethodDto();
        requestDto.setCustomerId(1L);
        requestDto.setMethodType("PAYPAL");
        requestDto.setDetails("paypal-token");

        PaymentMethod entity = new PaymentMethod();
        PaymentMethod updatedEntity = new PaymentMethod();
        updatedEntity.setPaymentMethodId(1L);

        PaymentMethodDto responseDto = new PaymentMethodDto();
        responseDto.setPaymentMethodId(1L);
        responseDto.setMethodType("PAYPAL");

        when(paymentMethodMapper.paymentMethodDtoToPaymentMethod(any(PaymentMethodDto.class))).thenReturn(entity);
        when(paymentMethodService.updatePaymentMethod(1L, entity)).thenReturn(updatedEntity);
        when(paymentMethodMapper.paymentMethodToPaymentMethodDto(updatedEntity)).thenReturn(responseDto);

        mockMvc.perform(put("/api/payment-methods/1")
            .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
            .content(Objects.requireNonNull(objectMapper.writeValueAsString(requestDto))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.paymentMethodId").value(1L));
    }

    @Test
    void deletePaymentMethod_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/payment-methods/1"))
            .andExpect(status().isNoContent());

        verify(paymentMethodService).deletePaymentMethod(1L);
    }

    @Test
    void createPaymentMethod_shouldReturnBadRequest_whenPayloadInvalid() throws Exception {
        PaymentMethodDto requestDto = new PaymentMethodDto();
        requestDto.setMethodType("");

        mockMvc.perform(post("/api/payment-methods/create")
            .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
            .content(Objects.requireNonNull(objectMapper.writeValueAsString(requestDto))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void updatePaymentMethod_shouldReturnNotFound_whenPaymentMethodMissing() throws Exception {
        PaymentMethodDto requestDto = new PaymentMethodDto();
        requestDto.setCustomerId(1L);
        requestDto.setMethodType("PAYPAL");
        requestDto.setDetails("paypal-token");

        PaymentMethod entity = new PaymentMethod();

        when(paymentMethodMapper.paymentMethodDtoToPaymentMethod(any(PaymentMethodDto.class))).thenReturn(entity);
        when(paymentMethodService.updatePaymentMethod(999L, entity)).thenThrow(new EntityNotFoundException("Payment method not found"));

        mockMvc.perform(put("/api/payment-methods/999")
            .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
            .content(Objects.requireNonNull(objectMapper.writeValueAsString(requestDto))))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deletePaymentMethod_shouldReturnNotFound_whenPaymentMethodMissing() throws Exception {
        org.mockito.Mockito.doThrow(new EntityNotFoundException("Payment method not found"))
                .when(paymentMethodService).deletePaymentMethod(999L);

        mockMvc.perform(delete("/api/payment-methods/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }
}
