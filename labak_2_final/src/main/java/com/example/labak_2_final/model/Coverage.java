package com.example.labak_2_final.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "coverages")
public class Coverage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @Column(name = "limit_amount")
    private BigDecimal limitAmount;

    public Coverage() {}

    public Coverage(String name, String description, BigDecimal limitAmount) {
        this.name = name;
        this.description = description;
        this.limitAmount = limitAmount;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getLimitAmount() { return limitAmount; }
    public void setLimitAmount(BigDecimal limitAmount) { this.limitAmount = limitAmount; }
}