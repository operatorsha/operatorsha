package com.example.labak_3_final.controller;

import com.example.labak_3_final.model.Payment;
import com.example.labak_3_final.model.Policy;
import com.example.labak_3_final.repository.PaymentRepository;
import com.example.labak_3_final.repository.PolicyRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PolicyRepository policyRepository;

    // =============================
    //           CRUD
    // =============================

    @GetMapping
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        Optional<Payment> payment = paymentRepository.findById(id);
        return payment.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Payment createPayment(@RequestBody Payment payment) {
        payment.setId(null);
        return paymentRepository.save(payment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Payment> updatePayment(@PathVariable Long id,
                                                 @RequestBody Payment updated) {
        return paymentRepository.findById(id)
                .map(existing -> {
                    existing.setPaymentNumber(updated.getPaymentNumber());
                    existing.setPaymentDate(updated.getPaymentDate());
                    existing.setAmount(updated.getAmount());
                    existing.setPaymentMethod(updated.getPaymentMethod());
                    existing.setStatus(updated.getStatus());
                    return ResponseEntity.ok(paymentRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        if (paymentRepository.existsById(id)) {
            paymentRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // =============================
    //      БИЗНЕС-ОПЕРАЦИИ
    // =============================

    /**
     * Оплатить полис.
     * POST /api/payments/pay/{policyId}
     * В теле запроса можно передать amount, paymentMethod, paymentNumber (если нужно).
     */
    @PostMapping("/pay/{policyId}")
    @Transactional
    public ResponseEntity<Payment> payForPolicy(@PathVariable Long policyId,
                                                @RequestBody Payment paymentData) {
        Optional<Policy> policyOpt = policyRepository.findById(policyId);
        if (policyOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Policy policy = policyOpt.get();

        Payment payment = new Payment();
        payment.setPaymentNumber(paymentData.getPaymentNumber());
        payment.setPaymentMethod(paymentData.getPaymentMethod());
        payment.setAmount(paymentData.getAmount());
        payment.setStatus(
                paymentData.getStatus() != null ? paymentData.getStatus() : "PAID"
        );
        payment.setPaymentDate(
                paymentData.getPaymentDate() != null ? paymentData.getPaymentDate() : LocalDate.now()
        );
        payment.setPolicy(policy);

        Payment saved = paymentRepository.save(payment);

        // Например, если общая сумма оплат >= премии — можно проставить статус полиса PAID
        if (policy.getPremium() != null) {
            BigDecimal totalPaid = policy.getPayments().stream()
                    .map(p -> p.getAmount() != null ? p.getAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (totalPaid.compareTo(policy.getPremium()) >= 0) {
                policy.setStatus("PAID");
                policyRepository.save(policy);
            }
        }

        return ResponseEntity.ok(saved);
    }

    /**
     * Получить общую сумму платежей по полису.
     * GET /api/payments/policy/{policyId}/total
     */
    @GetMapping("/policy/{policyId}/total")
    public ResponseEntity<BigDecimal> getTotalPaymentsForPolicy(@PathVariable Long policyId) {
        Optional<Policy> policyOpt = policyRepository.findById(policyId);
        if (policyOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Policy policy = policyOpt.get();
        BigDecimal total = policy.getPayments().stream()
                .map(p -> p.getAmount() != null ? p.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ResponseEntity.ok(total);
    }
}
