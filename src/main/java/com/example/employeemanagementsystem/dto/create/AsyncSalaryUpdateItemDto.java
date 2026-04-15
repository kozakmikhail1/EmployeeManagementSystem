package com.example.employeemanagementsystem.dto.create;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AsyncSalaryUpdateItemDto {

    /*
     * Идентификатор сотрудника, которому нужно обновить зарплату.
     * Валидация:
     * - не null, иначе нельзя однозначно найти запись;
     * - > 0, т.к. идентификаторы в БД положительные.
     */
    @NotNull(message = "Employee id cannot be null")
    @Positive(message = "Employee id must be positive")
    private Long employeeId;

    /*
     * Новое значение зарплаты.
     * Валидация:
     * - не null, т.к. операция требует конкретного значения;
     * - >= 0.01, чтобы не допустить нулевую/отрицательную зарплату.
     */
    @NotNull(message = "Salary cannot be null")
    @DecimalMin(value = "0.01", message = "Salary must be greater than 0")
    private BigDecimal salary;
}
