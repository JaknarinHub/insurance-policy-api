package com.example.insurance.dto;
import jakarta.validation.constraints.*;
public record CustomerRequest(
    @NotBlank @Size(max = 100) String name,
    @NotBlank @Email @Size(max = 200) String email
) {}
