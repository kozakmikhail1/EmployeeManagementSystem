package com.example.employeemanagementsystem.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.employeemanagementsystem.dto.create.AsyncSalaryUpdateItemDto;
import com.example.employeemanagementsystem.dto.create.EmployeeCreateDto;
import com.example.employeemanagementsystem.dto.create.EmployeeWithUserCreateDto;
import com.example.employeemanagementsystem.dto.get.AsyncTaskStartResponseDto;
import com.example.employeemanagementsystem.dto.get.AsyncTaskStatusDto;
import com.example.employeemanagementsystem.dto.get.CounterValueDto;
import com.example.employeemanagementsystem.dto.get.EmployeeDto;
import com.example.employeemanagementsystem.dto.patch.EmployeePatchDto;
import com.example.employeemanagementsystem.service.AsyncSalaryUpdateService;
import com.example.employeemanagementsystem.service.EmployeeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/employees")
@Tag(name = "Employees", description = "Operations on employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final AsyncSalaryUpdateService asyncSalaryUpdateService;

    @Autowired
    public EmployeeController(
            EmployeeService employeeService,
            AsyncSalaryUpdateService asyncSalaryUpdateService) {
        this.employeeService = employeeService;
        this.asyncSalaryUpdateService = asyncSalaryUpdateService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get employee by id")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable Long id) {
        EmployeeDto employeeDto = employeeService.getEmployeeDtoById(id);
        return ResponseEntity.ok(employeeDto);
    }

    @GetMapping(params = "!departmentId")
    @Operation(summary = "Get all employees with optional salary range filter")
    public ResponseEntity<List<EmployeeDto>> getAllEmployees(
            @RequestParam(value = "min_salary", required = false) BigDecimal minSalary,
            @RequestParam(value = "max_salary", required = false) BigDecimal maxSalary) {

        List<EmployeeDto> employees = employeeService.getEmployeesBySalaryRange(minSalary, maxSalary);

        return ResponseEntity.ok(employees);
    }

    @GetMapping(params = "departmentId")
    @Operation(summary = "Get employees by department id")
    public ResponseEntity<List<EmployeeDto>> getAllEmployeesByDepartment(
            @Positive @RequestParam(value = "departmentId", defaultValue = "1") Long departmentId) {
        List<EmployeeDto> employees = employeeService.getEmployeesByDepartmentId(departmentId);
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/")
    @Operation(summary = "Get employees by position id")
    public ResponseEntity<List<EmployeeDto>> getAllEmployeesByPosition(
            @Positive @RequestParam(value = "positionId") Long positionId) {
        List<EmployeeDto> employees = employeeService.getEmployeesByPositionId(positionId);
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/search/jpql")
    @Operation(summary = "Search employees with nested filters (JPQL)")
    public ResponseEntity<Page<EmployeeDto>> searchEmployeesWithNestedFilterJpql(
            @RequestParam(value = "departmentName", required = false) String departmentName,
            @RequestParam(value = "roleName", required = false) String roleName,
            @RequestParam(value = "active", required = false) Boolean active,
            Pageable pageable) {
        Page<EmployeeDto> employeePage = employeeService.searchEmployeesWithNestedFilterJpql(
                departmentName, roleName, active, pageable);
        return ResponseEntity.ok(employeePage);
    }

    @PostMapping
    @Operation(summary = "Create employee")
    public ResponseEntity<EmployeeDto> createEmployee(
            @Valid @RequestBody EmployeeCreateDto employeeDto) {
        EmployeeDto createdEmployee = employeeService.createEmployee(employeeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
    }

    @PostMapping("/bulk")
    @Operation(summary = "Create employees in bulk (transactional)")
    public ResponseEntity<List<EmployeeDto>> createEmployeesBulk(
            @Valid @RequestBody List<@Valid EmployeeCreateDto> employeeDtos) {
        List<EmployeeDto> createdEmployees = employeeService.createEmployeesBulk(employeeDtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployees);
    }

    @PostMapping("/bulk-salary-async")
    @Operation(summary = "Start async salary update in bulk")
    public ResponseEntity<AsyncTaskStartResponseDto> startAsyncBulkSalaryUpdate(
            @Valid @RequestBody List<@Valid AsyncSalaryUpdateItemDto> updates) {
        String taskId = asyncSalaryUpdateService.startBulkSalaryUpdateTask(updates);
        return ResponseEntity.accepted().body(new AsyncTaskStartResponseDto(taskId));
    }

    @GetMapping("/tasks/{taskId}")
    @Operation(summary = "Get async task status")
    public ResponseEntity<AsyncTaskStatusDto> getAsyncTaskStatus(@PathVariable String taskId) {
        return ResponseEntity.ok(asyncSalaryUpdateService.getTaskStatus(taskId));
    }

    @GetMapping("/tasks/counters/salary-updates")
    @Operation(summary = "Get total processed salary updates counter")
    public ResponseEntity<CounterValueDto> getProcessedSalaryUpdatesCounter() {
        long counterValue = asyncSalaryUpdateService.getProcessedItemsCounter();
        return ResponseEntity.ok(new CounterValueDto("processedSalaryUpdates", counterValue));
    }

    @PostMapping("/user")
    @Operation(summary = "Create employee with user")
    public ResponseEntity<EmployeeDto> createEmployeeWithUser(
            @Valid @RequestBody EmployeeWithUserCreateDto employeeDto) {

        EmployeeDto employeeDtores = employeeService.createEmployeeWithUser(
                employeeDto.getEmployeeCreateDto(),
                employeeDto.getUserCreateDto());
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeDtores);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update employee")
    public ResponseEntity<EmployeeDto> updateEmployee(
            @PathVariable Long id, @Valid @RequestBody EmployeeCreateDto employeeDto) {
        EmployeeDto updatedEmployee = employeeService.updateEmployee(id, employeeDto);
        return ResponseEntity.ok(updatedEmployee);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update employee")
    public ResponseEntity<EmployeeDto> patchEmployee(
            @PathVariable Long id, @Valid @RequestBody EmployeePatchDto employeeDto) {
        EmployeeDto updatedEmployee = employeeService.patchEmployee(id, employeeDto);
        return ResponseEntity.ok(updatedEmployee);
    }

    @DeleteMapping("/{id}/user")
    @Operation(summary = "Unlink user from employee")
    public ResponseEntity<EmployeeDto> unlinkUserFromEmployee(@PathVariable Long id) {
        EmployeeDto updatedEmployee = employeeService.unlinkUserFromEmployee(id);
        return ResponseEntity.ok(updatedEmployee);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete employee")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
