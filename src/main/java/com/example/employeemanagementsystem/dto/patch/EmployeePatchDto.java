package com.example.employeemanagementsystem.dto.patch;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "Employee patch payload")
@Getter
@Setter
public class EmployeePatchDto {

    @Size(min = 1, message = "First name cannot be blank")
    private String firstName;

    @Size(min = 1, message = "Last name cannot be blank")
    private String lastName;

    @Email(message = "Invalid email format")
    private String email;

    private LocalDate hireDate;

    @Positive(message = "Salary must be positive")
    private BigDecimal salary;

    private Boolean isActive;

    @Positive(message = "Department ID must be positive")
    private Long departmentId;

    @Positive(message = "Position ID must be positive")
    private Long positionId;

    @Positive(message = "User ID must be positive")
    private Long userId;
}
