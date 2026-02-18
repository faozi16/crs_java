package com.af.carrsvt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.af.carrsvt.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
