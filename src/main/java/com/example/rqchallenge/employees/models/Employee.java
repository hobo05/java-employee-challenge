package com.example.rqchallenge.employees.models;

import io.swagger.v3.oas.annotations.media.Schema;

public record Employee (
        @Schema(example = "123") Long id,
        @Schema(example = "John Smith") String name,
        @Schema(example = "45") Integer age,
        @Schema(example = "50000") Integer salary,
        @Schema(example = "") String profileImage
) {}
