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

import com.example.employeemanagementsystem.dto.create.RoleCreateDto;
import com.example.employeemanagementsystem.dto.get.RoleDto;
import com.example.employeemanagementsystem.service.RoleService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleDto> getRoleById(@PathVariable Long id) {
        RoleDto roleDto = roleService.getRoleById(id);
        return ResponseEntity.ok(roleDto);
    }

    @GetMapping
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        List<RoleDto> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @PostMapping
    public ResponseEntity<RoleDto> createRole(@Valid @RequestBody RoleCreateDto roleCreateDto) {
        RoleDto createdRole = roleService.createRole(roleCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRole);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleDto> updateRole(@PathVariable Long id,
                                              @Valid @RequestBody RoleCreateDto roleCreateDto) {
        RoleDto updatedRole = roleService.updateRole(id, roleCreateDto);
        return ResponseEntity.ok(updatedRole);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}