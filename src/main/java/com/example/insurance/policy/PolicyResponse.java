package com.example.insurance.policy;
import java.math.BigDecimal;
import java.time.LocalDate;
public record PolicyResponse(Long id, String policyNumber, Long customerId, BigDecimal sumAssured,
                             LocalDate startDate, LocalDate endDate, PolicyStatus status) {
    public static PolicyResponse from(Policy policy) {
        return new PolicyResponse(policy.getId(), policy.getPolicyNumber(), policy.getCustomer().getId(),
                policy.getSumAssured(), policy.getStartDate(), policy.getEndDate(), policy.getStatus());
    }
}
