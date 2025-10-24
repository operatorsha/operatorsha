package com.example.labak_2_final.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "claims")
public class Claim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String claimNumber;
    private LocalDate claimDate;
    private String description;
    private BigDecimal claimedAmount;
    private String status;

    @ManyToOne
    @JoinColumn(name = "policy_id")
    @JsonIgnore
    private Policy policy;

    @ManyToOne
    @JoinColumn(name = "coverage_id")
    private Coverage coverage;

    public Claim() {}

    public Claim(String claimNumber, LocalDate claimDate, String description,
                 BigDecimal claimedAmount, String status, Policy policy, Coverage coverage) {
        this.claimNumber = claimNumber;
        this.claimDate = claimDate;
        this.description = description;
        this.claimedAmount = claimedAmount;
        this.status = status;
        this.policy = policy;
        this.coverage = coverage;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getClaimNumber() { return claimNumber; }
    public void setClaimNumber(String claimNumber) { this.claimNumber = claimNumber; }

    public LocalDate getClaimDate() { return claimDate; }
    public void setClaimDate(LocalDate claimDate) { this.claimDate = claimDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getClaimedAmount() { return claimedAmount; }
    public void setClaimedAmount(BigDecimal claimedAmount) { this.claimedAmount = claimedAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Policy getPolicy() { return policy; }
    public void setPolicy(Policy policy) { this.policy = policy; }

    public Coverage getCoverage() { return coverage; }
    public void setCoverage(Coverage coverage) { this.coverage = coverage; }
}