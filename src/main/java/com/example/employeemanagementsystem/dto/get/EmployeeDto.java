package com.example.employeemanagementsystem.dto.get;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeDto {

    @JsonProperty(access = Access.READ_ONLY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private LocalDate hireDate;
    private BigDecimal salary;
    private Boolean isActive;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private DepartmentDto department;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PositionDto position;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserDto user;
}