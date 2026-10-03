package com.example.insurance.service.customer;

import java.util.Locale;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.insurance.common.ApiException;
import com.example.insurance.dto.customer.CustomerRequest;
import com.example.insurance.model.customer.Customer;
import com.example.insurance.repository.customer.CustomerRepository;
import com.example.insurance.repository.policy.PolicyRepository;

@Service
@Transactional
public class CustomerService {
    private final CustomerRepository customers;
    private final PolicyRepository policies;
    public CustomerService(CustomerRepository customers, PolicyRepository policies) { this.customers = customers; this.policies = policies; }
    @Transactional(readOnly = true)
    public Customer get(Long id) { return customers.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Customer not found")); }
    @Transactional(readOnly = true)
    public Page<Customer> list(String name, Pageable page) { return customers.findByNameContainingIgnoreCase(name, page); }
    public Customer create(CustomerRequest request) {
        String email = normalizeEmail(request.email());
        if (customers.existsByEmail(email)) throw new ApiException(HttpStatus.CONFLICT, "Email already exists");
        return customers.save(new Customer(request.name().trim(), email));
    }
    public Customer update(Long id, CustomerRequest request) {
        Customer customer = get(id);
        String email = normalizeEmail(request.email());
        if (customers.existsByEmailAndIdNot(email, id)) throw new ApiException(HttpStatus.CONFLICT, "Email already exists");
        customer.update(request.name().trim(), email);
        return customers.save(customer);
    }
    public void delete(Long id) {
        Customer customer = get(id);
        if (policies.existsByCustomerId(id)) throw new ApiException(HttpStatus.CONFLICT, "Delete the customer's policies first");
        customers.delete(customer);
    }
    private String normalizeEmail(String email) { return email.trim().toLowerCase(Locale.ROOT); }
}
