package com.example.insurance.policy;
import com.example.insurance.common.PageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.*;
import java.net.URI;
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
