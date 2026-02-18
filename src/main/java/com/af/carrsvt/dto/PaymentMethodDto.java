package com.af.carrsvt.dto;

import java.time.OffsetDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentMethodDto {
    private Long paymentMethodId;

    @NotNull
    private Long customerId;

    @NotBlank
    private String methodType;

    private String details;

    private Boolean primaryMethod = false;

    private OffsetDateTime createdAt;
}
