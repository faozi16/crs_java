package com.af.carrsvt.mapper;

import org.mapstruct.Mapper;

import com.af.carrsvt.dto.PaymentMethodDto;
import com.af.carrsvt.entity.PaymentMethod;

@Mapper(componentModel = "spring")
public interface PaymentMethodMapper {
    PaymentMethodDto paymentMethodToPaymentMethodDto(PaymentMethod pm);
    PaymentMethod paymentMethodDtoToPaymentMethod(PaymentMethodDto pmDto);
}
