package com.climbup.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
    @NotBlank String name,
    @NotBlank @Email String email,
    String phone,
    @NotNull Long membershipTypeId
) {}
