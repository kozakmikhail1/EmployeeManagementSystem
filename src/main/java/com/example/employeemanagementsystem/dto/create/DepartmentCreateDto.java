package com.example.employeemanagementsystem.dto.create;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
public class DepartmentCreateDto {

    @NotBlank(message = "Department name cannot be blank")
    private String name;

    private String description;
}