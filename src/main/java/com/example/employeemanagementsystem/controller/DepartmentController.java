package com.example.employeemanagementsystem.controller;

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

import com.example.employeemanagementsystem.dto.create.DepartmentCreateDto;
import com.example.employeemanagementsystem.dto.get.DepartmentDto;
import com.example.employeemanagementsystem.dto.get.EmployeeDto;
import com.example.employeemanagementsystem.dto.patch.DepartmentPatchDto;
import com.example.employeemanagementsystem.service.DepartmentService;
import com.example.employeemanagementsystem.service.EmployeeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/departments")
@Tag(name = "Departments", description = "Operations on departments")
public class DepartmentController {

    private final DepartmentService departmentService;
    private final EmployeeService employeeService;

    @Autowired
    public DepartmentController(DepartmentService departmentService,
            EmployeeService employeeService) {

        this.departmentService = departmentService;
        this.employeeService = employeeService;
    }
    @GetMapping("/{id}")
    @Operation(summary = "Get department by id")
    public ResponseEntity<DepartmentDto> getDepartmentById(@PathVariable Long id) {
        DepartmentDto departmentDto = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(departmentDto);
    }

    @GetMapping
    @Operation(summary = "Get all departments")
    public ResponseEntity<List<DepartmentDto>> getAllDepartments() {
        List<DepartmentDto> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/page")
    @Operation(summary = "Get departments page")
    public ResponseEntity<Page<DepartmentDto>> getDepartmentsPage(
            @RequestParam(value = "q", required = false) String q,
            Pageable pageable) {
        Page<DepartmentDto> page = departmentService.getDepartmentsPage(q, pageable);
        return ResponseEntity.ok(page);
    }

    @PostMapping
    @Operation(summary = "Create department")
    public ResponseEntity<DepartmentDto> createDepartment(
            @Valid @RequestBody DepartmentCreateDto departmentDto) {
        DepartmentDto createdDepartment = departmentService.createDepartment(departmentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDepartment);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update department")
    public ResponseEntity<DepartmentDto> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentCreateDto departmentDetails) {
        DepartmentDto updatedDepartment = departmentService.updateDepartment(id,
                departmentDetails);
        return ResponseEntity.ok(updatedDepartment);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update department")
    public ResponseEntity<DepartmentDto> patchDepartment(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentPatchDto departmentDetails) {
        DepartmentDto updatedDepartment = departmentService.patchDepartment(id, departmentDetails);
        return ResponseEntity.ok(updatedDepartment);
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Delete department")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{departmentId}/employees")
    @Operation(summary = "Get employees by department")
    public ResponseEntity<List<EmployeeDto>> getEmployeesByDepartment(
            @PathVariable Long departmentId) {
        List<EmployeeDto> employees = employeeService.getEmployeesByDepartmentId(departmentId);
        return ResponseEntity.ok(employees);
    }

}
