package com.af.carrsvt.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DriverDto {
    private Long driverId;

    @NotBlank
    private String username;

    @NotBlank
    @Size(min = 6, message = "password must be at least 6 characters")
    private String password;

    @Email
    @NotBlank
    private String email;

    private String phoneNumber;

    @NotBlank
    private String licenseDriver;

    private LocalDate dateOfBirth;
    private String placeOfBirth;
    private String address;
    private String status;
}
