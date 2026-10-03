package com.example.insurance.customer;
import com.example.insurance.common.PageResponse;
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
    public PageResponse<Customer> list(@RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return PageResponse.from(service.list(name, PageRequest.of(page, size, Sort.by("id"))));
    }
    @GetMapping("/{id}")
    public Customer get(@PathVariable @Positive Long id) { return service.get(id); }
    @PostMapping
    public ResponseEntity<Customer> create(@Valid @RequestBody CustomerRequest request) {
        Customer result = service.create(request);
        return ResponseEntity.created(URI.create("/api/customers/" + result.getId())).body(result);
    }
    @PutMapping("/{id}")
    public Customer update(@PathVariable @Positive Long id, @Valid @RequestBody CustomerRequest request) { return service.update(id, request); }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}
