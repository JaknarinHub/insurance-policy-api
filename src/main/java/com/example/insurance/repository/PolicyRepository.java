package com.example.insurance.repository;
import com.example.insurance.model.Policy;
import org.springframework.data.jpa.repository.*;
public interface PolicyRepository extends JpaRepository<Policy, Long>, JpaSpecificationExecutor<Policy> {
    boolean existsByPolicyNumber(String number);
    boolean existsByPolicyNumberAndIdNot(String number, Long id);
    boolean existsByCustomerId(Long customerId);
}
