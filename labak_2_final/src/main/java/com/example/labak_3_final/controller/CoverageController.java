package com.example.labak_3_final.controller;

import com.example.labak_3_final.model.Coverage;
import com.example.labak_3_final.repository.CoverageRepository;
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

    // GET ALL
    @GetMapping
    public List<Coverage> getAllCoverages() {
        return coverageRepository.findAll();
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Coverage> getCoverageById(@PathVariable Long id) {
        Optional<Coverage> coverage = coverageRepository.findById(id);
        return coverage.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // CREATE
    @PostMapping
    public Coverage createCoverage(@RequestBody Coverage coverage) {
        coverage.setId(null);
        return coverageRepository.save(coverage);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Coverage> updateCoverage(@PathVariable Long id,
                                                   @RequestBody Coverage updated) {
        return coverageRepository.findById(id)
                .map(existing -> {
                    existing.setName(updated.getName());
                    existing.setDescription(updated.getDescription());
                    existing.setLimitAmount(updated.getLimitAmount());
                    return ResponseEntity.ok(coverageRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoverage(@PathVariable Long id) {
        if (coverageRepository.existsById(id)) {
            coverageRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
