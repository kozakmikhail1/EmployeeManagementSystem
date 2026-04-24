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

import com.example.employeemanagementsystem.dto.create.PositionCreateDto;
import com.example.employeemanagementsystem.dto.get.EmployeeDto;
import com.example.employeemanagementsystem.dto.get.PositionDto;
import com.example.employeemanagementsystem.dto.patch.PositionPatchDto;
import com.example.employeemanagementsystem.service.EmployeeService;
import com.example.employeemanagementsystem.service.PositionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/positions")
@Tag(name = "Positions", description = "Operations on positions")
public class PositionController {

    private final PositionService positionService;
    private final EmployeeService employeeService;

    @Autowired
    public PositionController(PositionService positionService, EmployeeService employeeService) {
        this.positionService = positionService;
        this.employeeService = employeeService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get position by id")
    public ResponseEntity<PositionDto> getPositionById(@PathVariable Long id) {
        PositionDto positionDto = positionService.getPositionById(id);
        return ResponseEntity.ok(positionDto);
    }

    @GetMapping
    @Operation(summary = "Get all positions")
    public ResponseEntity<List<PositionDto>> getAllPositions() {
        List<PositionDto> positions = positionService.getAllPositions();
        return ResponseEntity.ok(positions);
    }

    @GetMapping("/page")
    @Operation(summary = "Get positions page")
    public ResponseEntity<Page<PositionDto>> getPositionsPage(
            @RequestParam(value = "q", required = false) String q,
            Pageable pageable) {
        Page<PositionDto> page = positionService.getPositionsPage(q, pageable);
        return ResponseEntity.ok(page);
    }

    @PostMapping
    @Operation(summary = "Create position")
    public ResponseEntity<PositionDto> createPosition(
        @Valid @RequestBody PositionCreateDto positionCreateDto) {
        PositionDto createdPosition = positionService.createPosition(positionCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPosition);
    }

    
    @PutMapping("/{id}")
    @Operation(summary = "Update position")
    public ResponseEntity<PositionDto> updatePosition(
        @PathVariable Long id, @Valid @RequestBody PositionCreateDto positionCreateDto) {
        PositionDto updatedPosition = positionService.updatePosition(id, positionCreateDto);
        return ResponseEntity.ok(updatedPosition);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update position")
    public ResponseEntity<PositionDto> patchPosition(
        @PathVariable Long id, @Valid @RequestBody PositionPatchDto positionCreateDto) {
        PositionDto updatedPosition = positionService.patchPosition(id, positionCreateDto);
        return ResponseEntity.ok(updatedPosition);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete position")
    public ResponseEntity<Void> deletePosition(@PathVariable Long id) {
        positionService.deletePosition(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{positionId}/employees")
    @Operation(summary = "Get employees by position")
    public ResponseEntity<List<EmployeeDto>> getEmployeesByPosition(@PathVariable Long positionId) {
        List<EmployeeDto> employees = employeeService.getEmployeesByPositionId(positionId);
        return ResponseEntity.ok(employees);
    }
}
