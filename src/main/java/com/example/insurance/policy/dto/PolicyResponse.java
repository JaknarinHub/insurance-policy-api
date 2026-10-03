package com.example.insurance.policy.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.insurance.policy.model.Policy;
import com.example.insurance.policy.model.PolicyStatus;

public record PolicyResponse(Long id, String policyNumber, Long customerId, BigDecimal sumAssured,
                             LocalDate startDate, LocalDate endDate, PolicyStatus status) {
    public static PolicyResponse from(Policy policy) {
        return new PolicyResponse(policy.getId(), policy.getPolicyNumber(), policy.getCustomer().getId(),
                policy.getSumAssured(), policy.getStartDate(), policy.getEndDate(), policy.getStatus());
    }
}
