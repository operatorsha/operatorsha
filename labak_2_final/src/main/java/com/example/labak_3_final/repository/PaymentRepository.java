package com.example.labak_3_final.repository;

import com.example.labak_3_final.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
