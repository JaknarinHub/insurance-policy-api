package com.example.insurance.dto.customer;

import com.example.insurance.model.customer.Customer;

public record CustomerResponse(Long id, String name, String email) {
    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(customer.getId(), customer.getName(), customer.getEmail());
    }
}
