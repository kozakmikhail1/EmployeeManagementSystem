package com.example.employeemanagementsystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.employeemanagementsystem.dto.create.AsyncSalaryUpdateItemDto;
import com.example.employeemanagementsystem.exception.ResourceNotFoundException;
import com.example.employeemanagementsystem.model.Employee;
import com.example.employeemanagementsystem.repository.EmployeeRepository;

@ExtendWith(MockitoExtension.class)
class AsyncSalaryUpdateExecutorTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private AsyncSalaryUpdateExecutor asyncSalaryUpdateExecutor;

    @Test
    void processNullUpdatesReturnsZero() throws ExecutionException, InterruptedException {
        int result = asyncSalaryUpdateExecutor.process(null).get();

        assertEquals(0, result);
    }

    @Test
    void processUpdatesSalaryAndReturnsProcessedCount() throws ExecutionException, InterruptedException {
        AsyncSalaryUpdateItemDto itemDto = new AsyncSalaryUpdateItemDto();
        itemDto.setEmployeeId(1L);
        itemDto.setSalary(BigDecimal.valueOf(4500));
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setSalary(BigDecimal.valueOf(3000));

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        int result = asyncSalaryUpdateExecutor.process(List.of(itemDto)).get();

        assertEquals(1, result);
        assertEquals(BigDecimal.valueOf(4500), employee.getSalary());
        verify(employeeRepository).save(employee);
    }

    @Test
    void processNullItemThrows() {
        List<AsyncSalaryUpdateItemDto> updates = Arrays.asList((AsyncSalaryUpdateItemDto) null);

        assertThrows(IllegalArgumentException.class, () -> asyncSalaryUpdateExecutor.process(updates));
    }

    @Test
    void processMissingEmployeeThrows() {
        AsyncSalaryUpdateItemDto itemDto = new AsyncSalaryUpdateItemDto();
        itemDto.setEmployeeId(999L);
        itemDto.setSalary(BigDecimal.valueOf(1000));
        List<AsyncSalaryUpdateItemDto> updates = List.of(itemDto);
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> asyncSalaryUpdateExecutor.process(updates));
    }
}
