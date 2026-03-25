package com.example.employeemanagementsystem.dto.patch;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "Position patch payload")
@Getter
@Setter
public class PositionPatchDto {

    @Size(min = 1, message = "Position name cannot be blank")
    private String name;

    private String description;

    @PositiveOrZero(message = "Minimum salary must be positive or zero")
    private BigDecimal minSalary;

    @PositiveOrZero(message = "Maximum salary must be positive or zero")
    private BigDecimal maxSalary;
}
