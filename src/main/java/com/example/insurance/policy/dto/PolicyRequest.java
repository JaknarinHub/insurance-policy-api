package com.example.insurance.policy.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import com.example.insurance.policy.model.PolicyStatus;

public record PolicyRequest(
    @NotBlank @Pattern(regexp = "[A-Za-z0-9-]+") @Size(max = 40) String policyNumber,
    @NotNull @Positive Long customerId,
    @NotNull @DecimalMin(value = "0", inclusive = false) @Digits(integer = 13, fraction = 2) BigDecimal sumAssured,
    @NotNull LocalDate startDate,
    @NotNull LocalDate endDate,
    @NotNull PolicyStatus status
) {}
