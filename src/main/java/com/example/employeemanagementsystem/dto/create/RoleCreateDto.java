package com.example.employeemanagementsystem.dto.create;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
public class RoleCreateDto {
    @NotBlank(message = "Role name cannot be blank")
    private String name;
}
