
package com.example.employeemanagementsystem.dto.create;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleCreateDto {
    @NotBlank(message = "Role name cannot be blank")
    private String name;
}