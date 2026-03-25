package com.example.employeemanagementsystem.dto.create;

import lombok.Getter;
import lombok.Setter;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Department create/update payload")
@Getter
@Setter
public class DepartmentCreateDto {

    @NotBlank(message = "Department name cannot be blank")
    private String name;

    private String description;
}
