package com.example.labak_2_final.controller;

import com.example.labak_2_final.model.Policy;
import com.example.labak_2_final.repository.PolicyRepository;
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
        return policyRepository.save(policy);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Policy> updatePolicy(@PathVariable Long id, @RequestBody Policy policyDetails) {
        Optional<Policy> optionalPolicy = policyRepository.findById(id);
        if (optionalPolicy.isPresent()) {
            Policy policy = optionalPolicy.get();
            policy.setPolicyNumber(policyDetails.getPolicyNumber());
            policy.setStartDate(policyDetails.getStartDate());
            policy.setEndDate(policyDetails.getEndDate());
            policy.setPremium(policyDetails.getPremium());
            policy.setStatus(policyDetails.getStatus());
            return ResponseEntity.ok(policyRepository.save(policy));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePolicy(@PathVariable Long id) {
        if (policyRepository.existsById(id)) {
            policyRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}