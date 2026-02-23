
package com.example.employeemanagementsystem.dto.create;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentCreateDto {

    @NotBlank(message = "Department name cannot be blank")
    private String name;

    private String description; 
}