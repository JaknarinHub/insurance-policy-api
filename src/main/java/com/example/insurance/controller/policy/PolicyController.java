package com.example.insurance.controller.policy;

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
import com.example.insurance.dto.policy.PolicyRequest;
import com.example.insurance.dto.policy.PolicyResponse;
import com.example.insurance.model.policy.PolicyStatus;
import com.example.insurance.service.policy.PolicyService;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {
    private final PolicyService service;
    public PolicyController(PolicyService service) { this.service = service; }
    @GetMapping
    public PageResponse<PolicyResponse> list(@RequestParam(required = false) @Positive Long customerId, @RequestParam(required = false) PolicyStatus status,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return PageResponse.from(service.list(customerId, status, PageRequest.of(page, size, Sort.by("id"))));
    }
    @GetMapping("/{id}")
    public PolicyResponse get(@PathVariable @Positive Long id) { return service.get(id); }
    @PostMapping
    public ResponseEntity<PolicyResponse> create(@Valid @RequestBody PolicyRequest request) {
        PolicyResponse result = service.create(request);
        return ResponseEntity.created(URI.create("/api/policies/" + result.id())).body(result);
    }
    @PutMapping("/{id}")
    public PolicyResponse update(@PathVariable @Positive Long id, @Valid @RequestBody PolicyRequest request) { return service.update(id, request); }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}
