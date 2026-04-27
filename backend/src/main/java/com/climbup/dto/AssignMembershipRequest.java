package com.climbup.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AssignMembershipRequest(
    @NotNull Long membershipTypeId,
    @NotNull LocalDate startDate,
    boolean directActivate
) {}
