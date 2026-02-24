package com.example.employeemanagementsystem.dto.create;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

@Getter
@Setter
public class PositionCreateDto {

    @NotBlank(message = "Position name cannot be blank")
    private String name;

    private String description;

    @PositiveOrZero(message = "Minimum salary must be positive or zero")
    private BigDecimal minSalary;

    @PositiveOrZero(message = "Maximum salary must be positive or zero")
    private BigDecimal maxSalary;
}