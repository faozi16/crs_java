package com.af.carrsvt.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentDto {
    private Long paymentId;

    @NotNull
    private Long reservationId;

    private OffsetDateTime paymentTime;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private String paymentMethod;
    private String status;
}
