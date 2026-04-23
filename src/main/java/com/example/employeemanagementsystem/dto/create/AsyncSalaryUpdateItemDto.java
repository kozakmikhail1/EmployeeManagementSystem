package com.example.employeemanagementsystem.dto.create;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AsyncSalaryUpdateItemDto {

    @NotNull(message = "Employee id cannot be null")
    @Positive(message = "Employee id must be positive")
    private Long employeeId;

    @NotNull(message = "Salary cannot be null")
    @DecimalMin(value = "0.01", message = "Salary must be greater than 0")
    private BigDecimal salary;
}
