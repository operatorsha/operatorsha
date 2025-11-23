package com.example.labak_3_final.repository;

import com.example.labak_3_final.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
