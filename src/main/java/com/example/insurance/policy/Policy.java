package com.example.insurance.policy;
import com.example.insurance.customer.Customer;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
@Entity
@Table(name = "policies", uniqueConstraints = @UniqueConstraint(columnNames = "policy_number"))
public class Policy {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "policy_number", nullable = false, length = 40)
    private String policyNumber;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal sumAssured;
    @Column(nullable = false)
    private LocalDate startDate;
    @Column(nullable = false)
    private LocalDate endDate;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private PolicyStatus status;
    protected Policy() {}
    public Policy(String number, Customer customer, BigDecimal amount, LocalDate start, LocalDate end, PolicyStatus status) {
        update(number, customer, amount, start, end, status);
    }
    public void update(String number, Customer customer, BigDecimal amount, LocalDate start, LocalDate end, PolicyStatus status) {
        this.policyNumber = number; this.customer = customer; this.sumAssured = amount;
        this.startDate = start; this.endDate = end; this.status = status;
    }
    public Long getId() { return id; }
    public String getPolicyNumber() { return policyNumber; }
    public Customer getCustomer() { return customer; }
    public BigDecimal getSumAssured() { return sumAssured; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public PolicyStatus getStatus() { return status; }
}
