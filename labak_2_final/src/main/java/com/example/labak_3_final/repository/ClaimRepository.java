package com.example.labak_3_final.repository;

import com.example.labak_3_final.model.Claim;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
}
