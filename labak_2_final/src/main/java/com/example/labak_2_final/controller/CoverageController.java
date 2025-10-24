package com.example.labak_2_final.controller;

import com.example.labak_2_final.model.Coverage;
import com.example.labak_2_final.repository.CoverageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/coverages")
public class CoverageController {

    @Autowired
    private CoverageRepository coverageRepository;

    @GetMapping
    public List<Coverage> getAllCoverages() {
        return coverageRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Coverage> getCoverageById(@PathVariable Long id) {
        Optional<Coverage> coverage = coverageRepository.findById(id);
        return coverage.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Coverage createCoverage(@RequestBody Coverage coverage) {
        return coverageRepository.save(coverage);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Coverage> updateCoverage(@PathVariable Long id, @RequestBody Coverage coverageDetails) {
        Optional<Coverage> optionalCoverage = coverageRepository.findById(id);
        if (optionalCoverage.isPresent()) {
            Coverage coverage = optionalCoverage.get();
            coverage.setName(coverageDetails.getName());
            coverage.setDescription(coverageDetails.getDescription());
            coverage.setLimitAmount(coverageDetails.getLimitAmount());
            return ResponseEntity.ok(coverageRepository.save(coverage));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoverage(@PathVariable Long id) {
        if (coverageRepository.existsById(id)) {
            coverageRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}