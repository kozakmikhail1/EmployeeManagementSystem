package com.example.employeemanagementsystem.dto.patch;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "Department patch payload")
@Getter
@Setter
public class DepartmentPatchDto {

    @Size(min = 1, message = "Department name cannot be blank")
    private String name;

    private String description;
}
