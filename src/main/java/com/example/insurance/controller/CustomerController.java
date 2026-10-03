package com.example.insurance.controller;
import com.example.insurance.service.CustomerService;
import com.example.insurance.dto.PageResponse;
import com.example.insurance.dto.CustomerRequest;
import com.example.insurance.dto.CustomerResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.*;
import java.net.URI;
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
