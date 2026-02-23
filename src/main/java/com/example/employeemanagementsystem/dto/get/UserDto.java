
package com.example.employeemanagementsystem.dto.get;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

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