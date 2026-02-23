package com.example.employeemanagementsystem.dto.create;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateDto {

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @JsonProperty(access = Access.WRITE_ONLY)
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<Long> roleIds;
}