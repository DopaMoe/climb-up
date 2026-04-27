package com.climbup.dto;

import jakarta.validation.constraints.NotNull;

public record CheckInRequest(@NotNull Long userId) {}
