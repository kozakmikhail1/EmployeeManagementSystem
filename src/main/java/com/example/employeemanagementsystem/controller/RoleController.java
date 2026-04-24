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

import com.example.employeemanagementsystem.dto.create.RoleCreateDto;
import com.example.employeemanagementsystem.dto.get.RoleDto;
import com.example.employeemanagementsystem.dto.patch.RolePatchDto;
import com.example.employeemanagementsystem.service.RoleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/roles")
@Tag(name = "Roles", description = "Operations on roles")
public class RoleController {

    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get role by id")
    public ResponseEntity<RoleDto> getRoleById(@PathVariable Long id) {
        RoleDto roleDto = roleService.getRoleById(id);
        return ResponseEntity.ok(roleDto);
    }

    @GetMapping
    @Operation(summary = "Get all roles")
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        List<RoleDto> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/page")
    @Operation(summary = "Get roles page")
    public ResponseEntity<Page<RoleDto>> getRolesPage(
            @RequestParam(value = "q", required = false) String q,
            Pageable pageable) {
        Page<RoleDto> page = roleService.getRolesPage(q, pageable);
        return ResponseEntity.ok(page);
    }

    @PostMapping
    @Operation(summary = "Create role")
    public ResponseEntity<RoleDto> createRole(@Valid @RequestBody RoleCreateDto roleCreateDto) {
        RoleDto createdRole = roleService.createRole(roleCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRole);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update role")
    public ResponseEntity<RoleDto> updateRole(@PathVariable Long id,
                                              @Valid @RequestBody RoleCreateDto roleCreateDto) {
        RoleDto updatedRole = roleService.updateRole(id, roleCreateDto);
        return ResponseEntity.ok(updatedRole);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update role")
    public ResponseEntity<RoleDto> patchRole(@PathVariable Long id,
                                             @Valid @RequestBody RolePatchDto roleCreateDto) {
        RoleDto updatedRole = roleService.patchRole(id, roleCreateDto);
        return ResponseEntity.ok(updatedRole);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete role")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}
