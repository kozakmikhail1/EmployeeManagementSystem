package com.example.employeemanagementsystem.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.example.employeemanagementsystem.dto.create.EmployeeCreateDto;
import com.example.employeemanagementsystem.dto.create.EmployeeWithUserCreateDto;
import com.example.employeemanagementsystem.dto.get.EmployeeDto;
import com.example.employeemanagementsystem.service.EmployeeService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable Long id) {
        EmployeeDto employeeDto = employeeService.getEmployeeDtoById(id);
        return ResponseEntity.ok(employeeDto);
    }

    @GetMapping(params = "!departmentId")
    public ResponseEntity<List<EmployeeDto>> getAllEmployees(
            @RequestParam(value = "min_salary", required = false) BigDecimal minSalary,
            @RequestParam(value = "max_salary", required = false) BigDecimal maxSalary) {

        List<EmployeeDto> employees = employeeService.getEmployeesBySalaryRange(minSalary, maxSalary);

        return ResponseEntity.ok(employees);
    }

    @GetMapping(params = "departmentId")
    public ResponseEntity<List<EmployeeDto>> getAllEmployeesByDepartment(
            @Positive @RequestParam(value = "departmentId", defaultValue = "1") Long departmentId) {
        List<EmployeeDto> employees = employeeService.getEmployeesByDepartmentId(departmentId);
        return ResponseEntity.ok(employees);
    }

    @GetMapping()
    public ResponseEntity<List<EmployeeDto>> getAllEmployeesByPosition(
            @Positive @RequestParam(value = "positionId", defaultValue = "1") Long positionId) {
        List<EmployeeDto> employees = employeeService.getEmployeesByPositionId(positionId);
        return ResponseEntity.ok(employees);
    }

    @PostMapping
    public ResponseEntity<EmployeeDto> createEmployee(
            @Valid @RequestBody EmployeeCreateDto employeeDto) {
        EmployeeDto createdEmployee = employeeService.createEmployee(employeeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
    }

    @PostMapping("/user")
    public ResponseEntity<EmployeeDto> createEmployeeWithUser(
            @Valid @RequestBody EmployeeWithUserCreateDto employeeDto) {

        EmployeeDto employeeDtores = employeeService.createEmployeeWithUser(
                employeeDto.getEmployeeCreateDto(),
                employeeDto.getUserCreateDto());
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeDtores);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDto> updateEmployee(
            @PathVariable Long id, @Valid @RequestBody EmployeeCreateDto employeeDto) {
        EmployeeDto updatedEmployee = employeeService.updateEmployee(id, employeeDto);
        return ResponseEntity.ok(updatedEmployee);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EmployeeDto> patchEmployee(
            @PathVariable Long id, @RequestBody EmployeeCreateDto employeeDto) {
        return updateEmployee(id, employeeDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
