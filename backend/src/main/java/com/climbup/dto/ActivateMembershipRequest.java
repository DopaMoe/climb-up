package com.climbup.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ActivateMembershipRequest(
    @NotNull LocalDate startDate
) {}
