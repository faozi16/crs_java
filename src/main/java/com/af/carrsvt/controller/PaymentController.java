package com.af.carrsvt.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.af.carrsvt.dto.PaymentDto;
import com.af.carrsvt.entity.Payment;
import com.af.carrsvt.mapper.PaymentMapper;
import com.af.carrsvt.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentMapper paymentMapper;

    @PostMapping("/create")
    public ResponseEntity<PaymentDto> createPayment(@jakarta.validation.Valid @RequestBody PaymentDto paymentDto) {
        Payment entity = paymentMapper.paymentDtoToPayment(paymentDto);
        Payment saved = paymentService.savePayment(entity);
        return ResponseEntity.ok(paymentMapper.paymentToPaymentDto(saved));
    }

    @GetMapping("/get")
    public ResponseEntity<List<PaymentDto>> getAllPayments() {
        List<Payment> list = paymentService.getAllPayments();
        List<PaymentDto> dtos = list.stream().map(paymentMapper::paymentToPaymentDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> getPaymentById(@PathVariable Long id) {
        Payment p = paymentService.getPaymentById(id);
        return ResponseEntity.ok(paymentMapper.paymentToPaymentDto(p));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentDto> updatePayment(@PathVariable Long id, @jakarta.validation.Valid @RequestBody PaymentDto paymentDetails) {
        Payment entity = paymentMapper.paymentDtoToPayment(paymentDetails);
        Payment updated = paymentService.updatePayment(id, entity);
        return ResponseEntity.ok(paymentMapper.paymentToPaymentDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}
