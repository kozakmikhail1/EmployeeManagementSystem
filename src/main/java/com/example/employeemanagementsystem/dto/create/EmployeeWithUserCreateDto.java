package com.example.employeemanagementsystem.dto.create;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeWithUserCreateDto {

    EmployeeCreateDto employeeCreateDto;
    UserCreateDto userCreateDto;
}
