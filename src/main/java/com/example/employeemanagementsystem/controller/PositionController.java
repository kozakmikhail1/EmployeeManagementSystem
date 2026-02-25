package com.example.employeemanagementsystem.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.employeemanagementsystem.dto.create.PositionCreateDto;
import com.example.employeemanagementsystem.dto.get.EmployeeDto;
import com.example.employeemanagementsystem.dto.get.PositionDto;
import com.example.employeemanagementsystem.service.EmployeeService;
import com.example.employeemanagementsystem.service.PositionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/positions")
public class PositionController {

    private final PositionService positionService;
    private final EmployeeService employeeService;

    @Autowired
    public PositionController(PositionService positionService, EmployeeService employeeService) {
        this.positionService = positionService;
        this.employeeService = employeeService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PositionDto> getPositionById(@PathVariable Long id) {
        PositionDto positionDto = positionService.getPositionById(id);
        return ResponseEntity.ok(positionDto);
    }

    @GetMapping
    public ResponseEntity<List<PositionDto>> getAllPositions() {
        List<PositionDto> positions = positionService.getAllPositions();
        return ResponseEntity.ok(positions);
    }

    @PostMapping
    public ResponseEntity<PositionDto> createPosition(
        @Valid @RequestBody PositionCreateDto positionCreateDto) {
        PositionDto createdPosition = positionService.createPosition(positionCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPosition);
    }

    //
    @PutMapping("/{id}")
    public ResponseEntity<PositionDto> updatePosition(
        @PathVariable Long id, @Valid @RequestBody PositionCreateDto positionCreateDto) {
        PositionDto updatedPosition = positionService.updatePosition(id, positionCreateDto);
        return ResponseEntity.ok(updatedPosition);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePosition(@PathVariable Long id) {
        positionService.deletePosition(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{positionId}/employees")
    public ResponseEntity<List<EmployeeDto>> getEmployeesByPosition(@PathVariable Long positionId) {
        List<EmployeeDto> employees = employeeService.getEmployeesByPositionId(positionId);
        return ResponseEntity.ok(employees);
    }
}
