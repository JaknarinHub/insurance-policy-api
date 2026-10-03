package com.example.insurance.policy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.insurance.policy.model.Policy;

public interface PolicyRepository extends JpaRepository<Policy, Long>, JpaSpecificationExecutor<Policy> {
    boolean existsByPolicyNumber(String number);
    boolean existsByPolicyNumberAndIdNot(String number, Long id);
    boolean existsByCustomerId(Long customerId);
}
