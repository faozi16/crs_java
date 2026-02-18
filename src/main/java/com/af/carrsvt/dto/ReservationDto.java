package com.af.carrsvt.dto;

import java.time.OffsetDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationDto {
    private Long reservationId;

    @NotNull
    private Long customerId;

    @NotNull
    private Long vehicleId;

    @NotNull
    private OffsetDateTime pickupTime;

    @NotBlank
    private String pickupLocation;

    @NotBlank
    private String dropoffLocation;

    private String status;
}
