package com.example.insurance.controller;

import java.net.URI;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.insurance.dto.common.PageResponse;
import com.example.insurance.dto.customer.CustomerRequest;
import com.example.insurance.dto.customer.CustomerResponse;
import com.example.insurance.service.customer.CustomerService;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService service;
    public CustomerController(CustomerService service) { this.service = service; }
    @GetMapping
    public PageResponse<CustomerResponse> list(@RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return PageResponse.from(service.list(name, PageRequest.of(page, size, Sort.by("id"))).map(CustomerResponse::from));
    }
    @GetMapping("/{id}")
    public CustomerResponse get(@PathVariable @Positive Long id) { return CustomerResponse.from(service.get(id)); }
    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {
        CustomerResponse result = CustomerResponse.from(service.create(request));
        return ResponseEntity.created(URI.create("/api/customers/" + result.id())).body(result);
    }
    @PutMapping("/{id}")
    public CustomerResponse update(@PathVariable @Positive Long id, @Valid @RequestBody CustomerRequest request) { return CustomerResponse.from(service.update(id, request)); }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}
