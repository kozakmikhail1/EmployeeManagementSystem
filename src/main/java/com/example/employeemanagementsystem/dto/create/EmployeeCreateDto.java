package com.example.employeemanagementsystem.dto.create;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeCreateDto {

    @NotBlank(message = "First name cannot be blank")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Hire date cannot be null")
    private LocalDate hireDate;

    @NotNull(message = "Salary cannot be null")
    @Positive(message = "Salary must be positive")
    private BigDecimal salary;

    private Boolean isActive;

    @NotNull(message = "Department ID cannot be null")
    private Long departmentId;

    @NotNull(message = "Position ID cannot be null")
    private Long positionId;

    @NotNull(message = "User ID cannot be null")
    private Long userId;
}