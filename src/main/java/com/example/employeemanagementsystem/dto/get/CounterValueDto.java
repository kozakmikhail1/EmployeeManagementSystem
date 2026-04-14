package com.example.employeemanagementsystem.dto.get;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CounterValueDto {
    private String counterName;
    private long value;
}
