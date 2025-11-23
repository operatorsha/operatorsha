package com.example.labak_3_final.controller;

import com.example.labak_3_final.model.Claim;
import com.example.labak_3_final.model.Coverage;
import com.example.labak_3_final.model.Policy;
import com.example.labak_3_final.repository.ClaimRepository;
import com.example.labak_3_final.repository.CoverageRepository;
import com.example.labak_3_final.repository.PolicyRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private CoverageRepository coverageRepository;

    // =============================
    //           CRUD
    // =============================

    @GetMapping
    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Claim> getClaimById(@PathVariable Long id) {
        Optional<Claim> claim = claimRepository.findById(id);
        return claim.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Claim createClaim(@RequestBody Claim claim) {
        claim.setId(null);
        return claimRepository.save(claim);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Claim> updateClaim(@PathVariable Long id,
                                             @RequestBody Claim updated) {
        return claimRepository.findById(id)
                .map(existing -> {
                    existing.setClaimNumber(updated.getClaimNumber());
                    existing.setClaimDate(updated.getClaimDate());
                    existing.setClaimedAmount(updated.getClaimedAmount());
                    existing.setDescription(updated.getDescription());
                    existing.setStatus(updated.getStatus());
                    return ResponseEntity.ok(claimRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClaim(@PathVariable Long id) {
        if (claimRepository.existsById(id)) {
            claimRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // =============================
    //      БИЗНЕС-ОПЕРАЦИИ
    // =============================

    /**
     * Зарегистрировать страховой случай по полису.
     * POST /api/claims/create/{policyId}
     *
     * В теле передаём:
     * {
     *   "claimNumber": "...",
     *   "claimDate": "2025-01-01",
     *   "claimedAmount": 100000,
     *   "description": "...",
     *   "status": "PENDING",
     *   "coverage": { "id": 1 }
     * }
     */
    @PostMapping("/create/{policyId}")
    public ResponseEntity<Claim> createClaimForPolicy(@PathVariable Long policyId,
                                                      @RequestBody Claim claimData) {
        Optional<Policy> policyOpt = policyRepository.findById(policyId);
        if (policyOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (claimData.getCoverage() == null || claimData.getCoverage().getId() == null) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Coverage> coverageOpt = coverageRepository.findById(claimData.getCoverage().getId());
        if (coverageOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Policy policy = policyOpt.get();
        Coverage coverage = coverageOpt.get();

        // Проверяем, что покрытие действительно входит в полис
        if (!policy.getCoverages().contains(coverage)) {
            return ResponseEntity.badRequest().body(null);
        }

        // Проверка лимита: заявленная сумма не должна превышать лимит покрытия
        if (claimData.getClaimedAmount() != null &&
                coverage.getLimitAmount() != null &&
                claimData.getClaimedAmount().compareTo(coverage.getLimitAmount()) > 0) {
            return ResponseEntity.badRequest().build();
        }

        Claim claim = new Claim();
        claim.setClaimNumber(claimData.getClaimNumber());
        claim.setClaimDate(claimData.getClaimDate());
        claim.setDescription(claimData.getDescription());
        claim.setClaimedAmount(claimData.getClaimedAmount());
        claim.setStatus(
                claimData.getStatus() != null ? claimData.getStatus() : "PENDING"
        );
        claim.setPolicy(policy);
        claim.setCoverage(coverage);

        Claim saved = claimRepository.save(claim);
        return ResponseEntity.ok(saved);
    }

    /**
     * Обновить статус страхового случая.
     * POST /api/claims/{claimId}/status?value=APPROVED
     *
     * Если статус меняется на APPROVED, проверяем,
     * что суммарная одобренная сумма по этому покрытию и полису
     * не превышает лимит покрытия.
     */
    @PostMapping("/{claimId}/status")
    @Transactional
    public ResponseEntity<Claim> updateClaimStatus(@PathVariable Long claimId,
                                                   @RequestParam("value") String newStatus) {
        Optional<Claim> claimOpt = claimRepository.findById(claimId);
        if (claimOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Claim claim = claimOpt.get();

        if ("APPROVED".equalsIgnoreCase(newStatus)) {
            Coverage coverage = claim.getCoverage();
            Policy policy = claim.getPolicy();

            if (coverage == null || policy == null) {
                return ResponseEntity.badRequest().build();
            }

            BigDecimal limit = coverage.getLimitAmount() != null
                    ? coverage.getLimitAmount()
                    : BigDecimal.ZERO;

            // Сумма уже одобренных заявлений по этому полису и покрытию
            BigDecimal alreadyApproved = policy.getClaims().stream()
                    .filter(c -> c.getCoverage() != null
                            && c.getCoverage().getId().equals(coverage.getId())
                            && "APPROVED".equalsIgnoreCase(c.getStatus()))
                    .map(c -> c.getClaimedAmount() != null ? c.getClaimedAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal currentAmount = claim.getClaimedAmount() != null
                    ? claim.getClaimedAmount()
                    : BigDecimal.ZERO;

            if (alreadyApproved.add(currentAmount).compareTo(limit) > 0) {
                // превысили лимит
                return ResponseEntity.badRequest().build();
            }
        }

        claim.setStatus(newStatus.toUpperCase());
        Claim saved = claimRepository.save(claim);
        return ResponseEntity.ok(saved);
    }
}
