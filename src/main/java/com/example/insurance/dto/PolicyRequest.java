package com.example.insurance.dto;
import com.example.insurance.model.PolicyStatus;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
public record PolicyRequest(
    @NotBlank @Pattern(regexp = "[A-Za-z0-9-]+") @Size(max = 40) String policyNumber,
    @NotNull @Positive Long customerId,
    @NotNull @DecimalMin(value = "0", inclusive = false) @Digits(integer = 13, fraction = 2) BigDecimal sumAssured,
    @NotNull LocalDate startDate,
    @NotNull LocalDate endDate,
    @NotNull PolicyStatus status
) {}
