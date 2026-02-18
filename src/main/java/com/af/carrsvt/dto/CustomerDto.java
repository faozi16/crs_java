package com.af.carrsvt.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDto {
    private Long customerId;

    @NotBlank
    private String username;

    @NotBlank
    @Size(min = 6, message = "password must be at least 6 characters")
    private String password;

    @Email
    @NotBlank
    private String email;

    private String phoneNumber;
    private String status;
    private String paymentMethod1;
    private String paymentMethod2;
    private String detailPaymentMethod1;
    private String detailPaymentMethod2;  
}
