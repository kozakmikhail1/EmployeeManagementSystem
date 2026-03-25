package com.example.employeemanagementsystem.dto.get;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "Role response DTO")
@Getter
@Setter
public class RoleDto {

    @JsonProperty(access = Access.READ_ONLY)
    private Long id;

    private String name;
}
