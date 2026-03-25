package com.example.employeemanagementsystem.dto.get;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "User response DTO")
@Getter
@Setter
public class UserDto {

    @JsonProperty(access = Access.READ_ONLY)
    private Long id;

    private String username;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private EmployeeDto employee;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<RoleDto> roles;
}
