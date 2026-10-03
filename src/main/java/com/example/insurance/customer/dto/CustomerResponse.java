package com.example.insurance.customer.dto;

import com.example.insurance.customer.model.Customer;

public record CustomerResponse(Long id, String name, String email) {
    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(customer.getId(), customer.getName(), customer.getEmail());
    }
}
