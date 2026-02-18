package com.af.carrsvt.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleDto {
    private Long vehicleId;
    private Long driverId;

    @NotBlank
    private String vehicleType;

    @NotBlank
    private String licensePlate;
    private String status;
}
