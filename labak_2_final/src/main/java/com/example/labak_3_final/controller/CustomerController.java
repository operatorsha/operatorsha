package com.example.labak_3_final.controller;

import com.example.labak_3_final.model.Customer;
import com.example.labak_3_final.model.Policy;
import com.example.labak_3_final.model.Payment;
import com.example.labak_3_final.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    // =============================
    //           CRUD
    // =============================

    // GET ALL
    @GetMapping
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        Optional<Customer> customer = customerRepository.findById(id);
        return customer.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // CREATE
    @PostMapping
    public Customer createCustomer(@RequestBody Customer customer) {
        customer.setId(null); // на всякий случай
        return customerRepository.save(customer);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id,
                                                   @RequestBody Customer updated) {
        return customerRepository.findById(id)
                .map(existing -> {
                    existing.setFirstName(updated.getFirstName());
                    existing.setLastName(updated.getLastName());
                    existing.setEmail(updated.getEmail());
                    existing.setPhone(updated.getPhone());
                    return ResponseEntity.ok(customerRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // =============================
    //      БИЗНЕС-ОПЕРАЦИИ
    // =============================

    /**
     * Получить все полисы клиента.
     */
    @GetMapping("/{id}/policies")
    public ResponseEntity<List<Policy>> getCustomerPolicies(@PathVariable Long id) {
        return customerRepository.findById(id)
                .map(customer -> ResponseEntity.ok(customer.getPolicies()))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Получить общую сумму оплаченных платежей клиента по всем его полисам.
     */
    @GetMapping("/{id}/total-paid")
    public ResponseEntity<BigDecimal> getCustomerTotalPaid(@PathVariable Long id) {
        return customerRepository.findById(id)
                .map(customer -> {
                    BigDecimal total = customer.getPolicies().stream()
                            .flatMap(policy -> policy.getPayments().stream())
                            .map(p -> p.getAmount() != null ? p.getAmount() : BigDecimal.ZERO)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return ResponseEntity.ok(total);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
