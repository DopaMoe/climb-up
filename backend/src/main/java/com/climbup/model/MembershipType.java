package com.climbup.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "membership_types")
@Getter
@Setter
public class MembershipType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    /** null for entry-based types */
    private Integer durationDays;

    /** null for time-based types */
    private Integer entriesCount;

    /** null for time-based types; days from startDate the entry pack is valid */
    private Integer entriesValidityDays;

    @Column(nullable = false)
    private boolean isActive = true;

    /** 0 = use global discount; >0 = override global */
    @Column(nullable = false)
    private double discountPct = 0.0;

    @JsonProperty("isActive")
    public boolean isActive() { return isActive; }
}
