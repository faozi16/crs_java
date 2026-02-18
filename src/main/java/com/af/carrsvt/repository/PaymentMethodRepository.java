package com.af.carrsvt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.af.carrsvt.entity.PaymentMethod;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
    List<PaymentMethod> findByCustomerId(Long customerId);
}
