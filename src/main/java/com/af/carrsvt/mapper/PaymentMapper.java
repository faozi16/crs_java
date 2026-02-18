package com.af.carrsvt.mapper;

import org.mapstruct.Mapper;

import com.af.carrsvt.dto.PaymentDto;
import com.af.carrsvt.entity.Payment;

@Mapper(
    componentModel = "spring"
)
public interface PaymentMapper {
    PaymentDto paymentToPaymentDto(Payment payment);
    Payment paymentDtoToPayment(PaymentDto paymentDto);
}
