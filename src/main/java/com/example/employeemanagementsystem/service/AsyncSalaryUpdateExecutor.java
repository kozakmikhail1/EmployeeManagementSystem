package com.example.employeemanagementsystem.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.employeemanagementsystem.dto.create.AsyncSalaryUpdateItemDto;
import com.example.employeemanagementsystem.exception.ResourceNotFoundException;
import com.example.employeemanagementsystem.model.Employee;
import com.example.employeemanagementsystem.repository.EmployeeRepository;

@Service
public class AsyncSalaryUpdateExecutor {

    private static final String EMPLOYEE_NOT_FOUND_MESSAGE = "Employee not found with id ";
    private static final String INTERRUPTED_MESSAGE = "Async salary update processing was interrupted";

    private final EmployeeRepository employeeRepository;

    @Value("${app.async.salary-update.per-item-delay-ms:5000}")
    private long perItemDelayMs;

    @Autowired
    public AsyncSalaryUpdateExecutor(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Async
    public CompletableFuture<Integer> process(List<AsyncSalaryUpdateItemDto> updates) {
        int processedItems = 0;
        if (updates == null) {
            return CompletableFuture.completedFuture(processedItems);
        }

        for (AsyncSalaryUpdateItemDto item : updates) {
            if (item == null) {
                throw new IllegalArgumentException("Bulk salary update item cannot be null");
            }

            Employee employee = employeeRepository.findById(item.getEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            EMPLOYEE_NOT_FOUND_MESSAGE + item.getEmployeeId()));

            employee.setSalary(item.getSalary());
            employeeRepository.save(employee);
            processedItems++;

            pauseAfterItemProcessed();
        }

        return CompletableFuture.completedFuture(processedItems);
    }

    private void pauseAfterItemProcessed() {
        if (perItemDelayMs <= 0) {
            return;
        }

        try {
            Thread.sleep(perItemDelayMs);
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(INTERRUPTED_MESSAGE, interruptedException);
        }
    }
}
