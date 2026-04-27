package com.climbup.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record MembershipTypeRequest(
    @NotBlank String name,
    String description,
    @NotNull @DecimalMin("0.0") BigDecimal basePrice,
    Integer durationDays,
    Integer entriesCount,
    Integer entriesValidityDays,
    double discountPct
) {}
