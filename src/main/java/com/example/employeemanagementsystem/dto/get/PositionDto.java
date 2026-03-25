package com.example.employeemanagementsystem.dto.get;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "Position response DTO")
@Getter
@Setter
public class PositionDto {

    @JsonProperty(access = Access.READ_ONLY)
    private Long id;

    private String name;
    private String description;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
}
