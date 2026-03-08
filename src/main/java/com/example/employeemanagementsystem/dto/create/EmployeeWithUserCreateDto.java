package com.example.employeemanagementsystem.dto.create;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

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
