package com.example.employeemanagementsystem.dto.create;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "Employee and user create payload")
@Getter
@Setter
public class EmployeeWithUserCreateDto {

    @Valid
    @NotNull(message = "Employee payload cannot be null")
    private EmployeeCreateDto employeeCreateDto;

    @Valid
    @NotNull(message = "User payload cannot be null")
    private UserCreateDto userCreateDto;
}
