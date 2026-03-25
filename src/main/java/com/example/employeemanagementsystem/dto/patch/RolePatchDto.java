package com.example.employeemanagementsystem.dto.patch;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "Role patch payload")
@Getter
@Setter
public class RolePatchDto {

    @Size(min = 1, message = "Role name cannot be blank")
    private String name;
}
