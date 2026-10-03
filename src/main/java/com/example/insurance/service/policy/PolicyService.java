package com.example.insurance.service.policy;

import java.util.Locale;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.insurance.common.ApiException;
import com.example.insurance.model.customer.Customer;
import com.example.insurance.service.customer.CustomerService;
import com.example.insurance.dto.policy.PolicyRequest;
import com.example.insurance.dto.policy.PolicyResponse;
import com.example.insurance.model.policy.Policy;
import com.example.insurance.model.policy.PolicyStatus;
import com.example.insurance.repository.policy.PolicyRepository;

@Service
@Transactional
public class PolicyService {
    private final PolicyRepository policies;
    private final CustomerService customers;
    public PolicyService(PolicyRepository policies, CustomerService customers) { this.policies = policies; this.customers = customers; }
    private Policy getEntity(Long id) { return policies.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Policy not found")); }
    @Transactional(readOnly = true)
    public PolicyResponse get(Long id) { return PolicyResponse.from(getEntity(id)); }
    @Transactional(readOnly = true)
    public Page<PolicyResponse> list(Long customerId, PolicyStatus status, Pageable page) {
        Specification<Policy> filter = (root, query, cb) -> cb.conjunction();
        if (customerId != null) filter = filter.and((root, query, cb) -> cb.equal(root.get("customer").get("id"), customerId));
        if (status != null) filter = filter.and((root, query, cb) -> cb.equal(root.get("status"), status));
        return policies.findAll(filter, page).map(PolicyResponse::from);
    }
    public PolicyResponse create(PolicyRequest request) {
        validateDates(request);
        String number = normalizeNumber(request.policyNumber());
        if (policies.existsByPolicyNumber(number)) throw new ApiException(HttpStatus.CONFLICT, "Policy number already exists");
        Customer customer = customers.get(request.customerId());
        return PolicyResponse.from(policies.save(new Policy(number, customer, request.sumAssured(), request.startDate(), request.endDate(), request.status())));
    }
    public PolicyResponse update(Long id, PolicyRequest request) {
        Policy policy = getEntity(id);
        validateDates(request);
        String number = normalizeNumber(request.policyNumber());
        if (policies.existsByPolicyNumberAndIdNot(number, id)) throw new ApiException(HttpStatus.CONFLICT, "Policy number already exists");
        policy.update(number, customers.get(request.customerId()), request.sumAssured(), request.startDate(), request.endDate(), request.status());
        return PolicyResponse.from(policies.save(policy));
    }
    public void delete(Long id) { policies.delete(getEntity(id)); }
    static void validateDates(PolicyRequest request) {
        if (request.endDate().isBefore(request.startDate())) throw new ApiException(HttpStatus.BAD_REQUEST, "End date must be on or after start date");
    }
    private String normalizeNumber(String number) { return number.toUpperCase(Locale.ROOT); }
}
