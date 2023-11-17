package com.example.rqchallenge.employees.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NewEmployee(
        @Schema(example = "John Smith") @NotBlank String name,
        @Schema(example = "45") @NotNull Integer age,
        @Schema(example = "50000") @NotNull Integer salary) {}
