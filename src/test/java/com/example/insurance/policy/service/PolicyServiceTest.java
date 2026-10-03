package com.example.insurance.policy.service;
import com.example.insurance.common.ApiException;
import com.example.insurance.policy.dto.PolicyRequest;
import com.example.insurance.policy.model.PolicyStatus;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
class PolicyServiceTest {
    private PolicyRequest request(LocalDate start, LocalDate end) {
        return new PolicyRequest("DEMO-001", 1L, new BigDecimal("100.00"), start, end, PolicyStatus.ACTIVE);
    }
    @Test void allowsSameDayCoverage() {
        LocalDate date = LocalDate.of(2026, 1, 1);
        assertDoesNotThrow(() -> PolicyService.validateDates(request(date, date)));
    }
    @Test void rejectsEndBeforeStart() {
        LocalDate date = LocalDate.of(2026, 1, 1);
        ApiException error = assertThrows(ApiException.class, () -> PolicyService.validateDates(request(date, date.minusDays(1))));
        assertEquals(400, error.getStatus().value());
    }
}
