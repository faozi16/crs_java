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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.af.carrsvt.dto.PaymentMethodDto;
import com.af.carrsvt.entity.PaymentMethod;
import com.af.carrsvt.mapper.PaymentMethodMapper;
import com.af.carrsvt.service.PaymentMethodService;

@RestController
@RequestMapping("/api/payment-methods")
public class PaymentMethodController {
    @Autowired
    private PaymentMethodService paymentMethodService;

    @Autowired
    private PaymentMethodMapper paymentMethodMapper;

    @PostMapping("/create")
    public ResponseEntity<PaymentMethodDto> createPaymentMethod(@jakarta.validation.Valid @RequestBody PaymentMethodDto dto) {
        PaymentMethod pm = paymentMethodMapper.paymentMethodDtoToPaymentMethod(dto);
        PaymentMethod saved = paymentMethodService.savePaymentMethod(pm);
        return ResponseEntity.ok(paymentMethodMapper.paymentMethodToPaymentMethodDto(saved));
    }

    @GetMapping("/get")
    public ResponseEntity<List<PaymentMethodDto>> getAllPaymentMethods(@RequestParam(required = false) Long customerId) {
        List<PaymentMethod> list = (customerId == null) ? paymentMethodService.getAllPaymentMethods() : paymentMethodService.getByCustomerId(customerId);
        List<PaymentMethodDto> dtos = list.stream().map(paymentMethodMapper::paymentMethodToPaymentMethodDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentMethodDto> getById(@PathVariable Long id) {
        PaymentMethod pm = paymentMethodService.getPaymentMethodById(id);
        return ResponseEntity.ok(paymentMethodMapper.paymentMethodToPaymentMethodDto(pm));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentMethodDto> update(@PathVariable Long id, @jakarta.validation.Valid @RequestBody PaymentMethodDto dto) {
        PaymentMethod pm = paymentMethodMapper.paymentMethodDtoToPaymentMethod(dto);
        PaymentMethod updated = paymentMethodService.updatePaymentMethod(id, pm);
        return ResponseEntity.ok(paymentMethodMapper.paymentMethodToPaymentMethodDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        paymentMethodService.deletePaymentMethod(id);
        return ResponseEntity.noContent().build();
    }
}
