package com.af.carrsvt;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

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

import com.af.carrsvt.controller.PaymentController;
import com.af.carrsvt.dto.PaymentDto;
import com.af.carrsvt.entity.Payment;
import com.af.carrsvt.exception.GlobalExceptionHandler;
import com.af.carrsvt.mapper.PaymentMapper;
import com.af.carrsvt.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentController paymentController;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createPayment_shouldReturnSavedPayment() throws Exception {
        PaymentDto requestDto = new PaymentDto();
        requestDto.setReservationId(1L);
        requestDto.setAmount(new BigDecimal("100000"));
        requestDto.setPaymentMethod("CARD");

        Payment entity = new Payment();
        Payment savedEntity = new Payment();
        savedEntity.setPaymentId(1L);

        PaymentDto responseDto = new PaymentDto();
        responseDto.setPaymentId(1L);
        responseDto.setPaymentMethod("CARD");

        when(paymentMapper.paymentDtoToPayment(any(PaymentDto.class))).thenReturn(entity);
        when(paymentService.savePayment(entity)).thenReturn(savedEntity);
        when(paymentMapper.paymentToPaymentDto(savedEntity)).thenReturn(responseDto);

        mockMvc.perform(post("/api/payments/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.paymentId").value(1L));
    }

    @Test
    void updatePayment_shouldReturnUpdatedPayment() throws Exception {
        PaymentDto requestDto = new PaymentDto();
        requestDto.setReservationId(1L);
        requestDto.setAmount(new BigDecimal("125000"));
        requestDto.setPaymentMethod("PAYPAL");

        Payment entity = new Payment();
        Payment updatedEntity = new Payment();
        updatedEntity.setPaymentId(1L);

        PaymentDto responseDto = new PaymentDto();
        responseDto.setPaymentId(1L);
        responseDto.setPaymentMethod("PAYPAL");

        when(paymentMapper.paymentDtoToPayment(any(PaymentDto.class))).thenReturn(entity);
        when(paymentService.updatePayment(1L, entity)).thenReturn(updatedEntity);
        when(paymentMapper.paymentToPaymentDto(updatedEntity)).thenReturn(responseDto);

        mockMvc.perform(put("/api/payments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.paymentId").value(1L));
    }

    @Test
    void deletePayment_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/payments/1"))
            .andExpect(status().isNoContent());

        verify(paymentService).deletePayment(1L);
    }

    @Test
    void createPayment_shouldReturnBadRequest_whenPayloadInvalid() throws Exception {
        PaymentDto requestDto = new PaymentDto();
        requestDto.setReservationId(1L);
        requestDto.setAmount(new BigDecimal("-1"));
        requestDto.setPaymentMethod(null);

        mockMvc.perform(post("/api/payments/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void updatePayment_shouldReturnNotFound_whenPaymentMissing() throws Exception {
        PaymentDto requestDto = new PaymentDto();
        requestDto.setReservationId(1L);
        requestDto.setAmount(new BigDecimal("125000"));
        requestDto.setPaymentMethod("PAYPAL");

        Payment entity = new Payment();

        when(paymentMapper.paymentDtoToPayment(any(PaymentDto.class))).thenReturn(entity);
        when(paymentService.updatePayment(999L, entity)).thenThrow(new EntityNotFoundException("Payment not found"));

        mockMvc.perform(put("/api/payments/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deletePayment_shouldReturnNotFound_whenPaymentMissing() throws Exception {
        org.mockito.Mockito.doThrow(new EntityNotFoundException("Payment not found"))
                .when(paymentService).deletePayment(999L);

        mockMvc.perform(delete("/api/payments/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }
}
