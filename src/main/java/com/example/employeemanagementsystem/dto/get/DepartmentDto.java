
package com.example.employeemanagementsystem.dto.get;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentDto {

    @JsonProperty(access = Access.READ_ONLY)
    private Long id;

    private String name;
    private String description;
}