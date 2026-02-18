package com.af.carrsvt.service;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.af.carrsvt.entity.PaymentMethod;
import com.af.carrsvt.repository.PaymentMethodRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class PaymentMethodService {
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    public PaymentMethod savePaymentMethod(PaymentMethod pm) {
        if (pm.getCreatedAt() == null) pm.setCreatedAt(OffsetDateTime.now());
        return paymentMethodRepository.save(pm);
    }

    public List<PaymentMethod> getAllPaymentMethods() {
        return paymentMethodRepository.findAll();
    }

    public List<PaymentMethod> getByCustomerId(Long customerId) {
        return paymentMethodRepository.findByCustomerId(customerId);
    }

    public PaymentMethod getPaymentMethodById(Long id) {
        return paymentMethodRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("PaymentMethod not found"));
    }

    public PaymentMethod updatePaymentMethod(Long id, PaymentMethod pmDetails) {
        PaymentMethod pm = getPaymentMethodById(id);
        pm.setMethodType(pmDetails.getMethodType());
        pm.setDetails(pmDetails.getDetails());
        pm.setPrimaryMethod(pmDetails.getPrimaryMethod());
        return paymentMethodRepository.save(pm);
    }

    public void deletePaymentMethod(Long id) {
        PaymentMethod pm = getPaymentMethodById(id);
        paymentMethodRepository.delete(pm);
    }
}
