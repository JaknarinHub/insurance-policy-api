package com.example.insurance.config;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.insurance.model.customer.Customer;
import com.example.insurance.repository.CustomerRepository;
import com.example.insurance.model.policy.Policy;
import com.example.insurance.model.policy.PolicyStatus;
import com.example.insurance.repository.PolicyRepository;

@Component
@ConditionalOnProperty(name = "app.seed-data", havingValue = "true")
public class DemoData implements CommandLineRunner {
    private final CustomerRepository customers;
    private final PolicyRepository policies;
    public DemoData(CustomerRepository customers, PolicyRepository policies) { this.customers = customers; this.policies = policies; }
    @Override @Transactional
    public void run(String... args) {
        Customer alex = customers.save(new Customer("Alex Demo", "alex@example.com"));
        Customer sam = customers.save(new Customer("Sam Sample", "sam@example.com"));
        policies.save(new Policy("DEMO-001", alex, new BigDecimal("100000.00"), LocalDate.of(2026,1,1), LocalDate.of(2027,1,1), PolicyStatus.ACTIVE));
        policies.save(new Policy("DEMO-002", sam, new BigDecimal("50000.00"), LocalDate.of(2025,1,1), LocalDate.of(2026,1,1), PolicyStatus.EXPIRED));
    }
}
