package com.example.labak_3_final.controller;

import com.example.labak_3_final.model.Coverage;
import com.example.labak_3_final.model.Customer;
import com.example.labak_3_final.model.Policy;
import com.example.labak_3_final.repository.CoverageRepository;
import com.example.labak_3_final.repository.CustomerRepository;
import com.example.labak_3_final.repository.PolicyRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CoverageRepository coverageRepository;

    // =============================
    //           CRUD
    // =============================

    @GetMapping
    public List<Policy> getAllPolicies() {
        return policyRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Policy> getPolicyById(@PathVariable Long id) {
        Optional<Policy> policy = policyRepository.findById(id);
        return policy.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Policy createPolicy(@RequestBody Policy policy) {
        policy.setId(null);
        return policyRepository.save(policy);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Policy> updatePolicy(@PathVariable Long id,
                                               @RequestBody Policy updated) {
        return policyRepository.findById(id)
                .map(existing -> {
                    existing.setPolicyNumber(updated.getPolicyNumber());
                    existing.setStartDate(updated.getStartDate());
                    existing.setEndDate(updated.getEndDate());
                    existing.setPremium(updated.getPremium());
                    existing.setStatus(updated.getStatus());
                    return ResponseEntity.ok(policyRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePolicy(@PathVariable Long id) {
        if (policyRepository.existsById(id)) {
            policyRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // =============================
    //      БИЗНЕС-ОПЕРАЦИИ
    // =============================

    /**
     * Оформить полис для существующего клиента.
     * POST /api/policies/assign/{customerId}
     */
    @PostMapping("/assign/{customerId}")
    public ResponseEntity<Policy> createPolicyForCustomer(@PathVariable Long customerId,
                                                          @RequestBody Policy policyData) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Customer customer = customerOpt.get();
        policyData.setId(null);
        policyData.setCustomer(customer);
        Policy saved = policyRepository.save(policyData);
        return ResponseEntity.ok(saved);
    }

    /**
     * Добавить покрытие к полису.
     * POST /api/policies/{policyId}/coverages/{coverageId}
     */
    @PostMapping("/{policyId}/coverages/{coverageId}")
    @Transactional
    public ResponseEntity<Policy> addCoverageToPolicy(@PathVariable Long policyId,
                                                      @PathVariable Long coverageId) {
        Optional<Policy> policyOpt = policyRepository.findById(policyId);
        Optional<Coverage> coverageOpt = coverageRepository.findById(coverageId);

        if (policyOpt.isEmpty() || coverageOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Policy policy = policyOpt.get();
        Coverage coverage = coverageOpt.get();

        if (!policy.getCoverages().contains(coverage)) {
            policy.getCoverages().add(coverage);
        }

        Policy saved = policyRepository.save(policy);
        return ResponseEntity.ok(saved);
    }

    /**
     * Закрыть полис (например, после окончания срока или полного возмещения).
     * POST /api/policies/{policyId}/close
     */
    @PostMapping("/{policyId}/close")
    public ResponseEntity<Policy> closePolicy(@PathVariable Long policyId) {
        return policyRepository.findById(policyId)
                .map(policy -> {
                    policy.setStatus("CLOSED");
                    return ResponseEntity.ok(policyRepository.save(policy));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
