package com.example.employeemanagementsystem.dto.create;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "Employee create/update payload")
@Getter
@Setter
public class EmployeeCreateDto {
    private static final String PERSON_NAME_PATTERN = "^[\\p{L}]+(?:[ '-][\\p{L}]+)*$";

    @NotBlank(message = "First name cannot be blank")
    @Pattern(
            regexp = PERSON_NAME_PATTERN,
            message = "First name can contain only letters, spaces, apostrophes, and hyphens")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Pattern(
            regexp = PERSON_NAME_PATTERN,
            message = "Last name can contain only letters, spaces, apostrophes, and hyphens")
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

    private Long userId;
}
