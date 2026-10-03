package com.example.insurance.dto.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerRequest(
    @NotBlank @Size(max = 100) String name,
    @NotBlank @Email @Size(max = 200) String email
) {}
