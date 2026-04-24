package com.example.employeemanagementsystem.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.employeemanagementsystem.cache.CacheNames;
import com.example.employeemanagementsystem.cache.EmployeeSearchCache;
import com.example.employeemanagementsystem.cache.InvalidateReadCaches;
import com.example.employeemanagementsystem.dto.create.RoleCreateDto;
import com.example.employeemanagementsystem.dto.get.RoleDto;
import com.example.employeemanagementsystem.dto.patch.RolePatchDto;
import com.example.employeemanagementsystem.exception.ResourceNotFoundException;
import com.example.employeemanagementsystem.mapper.RoleMapper;
import com.example.employeemanagementsystem.model.Role;
import com.example.employeemanagementsystem.repository.RoleRepository;

@Service
public class RoleService {

    private static final String ROLE_NOT_FOUND_WITH_ID_MESSAGE = "Role not found with id ";
    private static final String ROLE_NOT_FOUND_WITH_NAME_MESSAGE = "Role not found with name ";

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final EmployeeSearchCache employeeSearchCache;

    @Autowired
    public RoleService(
            RoleRepository roleRepository,
            RoleMapper roleMapper,
            EmployeeSearchCache employeeSearchCache) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
        this.employeeSearchCache = employeeSearchCache;
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.ROLE_BY_ID, key = "#id")
    public RoleDto getRoleById(Long id) {
        return roleRepository.findById(id)
            .map(roleMapper::toDto)
            .orElseThrow(
                () -> new ResourceNotFoundException(ROLE_NOT_FOUND_WITH_ID_MESSAGE + id));
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.ROLES_ALL)
    public List<RoleDto> getAllRoles() {
        return roleRepository.findAll().stream()
            .map(roleMapper::toDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public Page<RoleDto> getRolesPage(String q, Pageable pageable) {
        String normalizedQuery = normalizeFilterValueForQuery(q);
        return roleRepository.searchPage(normalizedQuery, pageable).map(roleMapper::toDto);
    }

    @Transactional
    @InvalidateReadCaches
    public RoleDto createRole(RoleCreateDto roleCreateDto) {
        Role role = roleMapper.toEntity(roleCreateDto);
        Role savedRole = roleRepository.save(role);
        employeeSearchCache.invalidateAll();
        return roleMapper.toDto(savedRole);
    }

    @Transactional
    @InvalidateReadCaches
    public RoleDto updateRole(Long id, RoleCreateDto roleCreateDto) {
        Role role = roleRepository.findById(id)
            .orElseThrow(
                () -> new ResourceNotFoundException(ROLE_NOT_FOUND_WITH_ID_MESSAGE + id));
        roleMapper.updateRoleFromDto(roleCreateDto, role);
        Role updatedRole = roleRepository.save(role);
        employeeSearchCache.invalidateAll();
        return roleMapper.toDto(updatedRole);
    }

    @Transactional
    @InvalidateReadCaches
    public RoleDto patchRole(Long id, RolePatchDto roleCreateDto) {
        Role role = roleRepository.findById(id)
            .orElseThrow(
                () -> new ResourceNotFoundException(ROLE_NOT_FOUND_WITH_ID_MESSAGE + id));
        roleMapper.updateRoleFromPatchDto(roleCreateDto, role);
        Role updatedRole = roleRepository.save(role);
        employeeSearchCache.invalidateAll();
        return roleMapper.toDto(updatedRole);
    }

    @Transactional
    @InvalidateReadCaches
    public void deleteRole(Long id) {
        roleRepository.findById(id)
            .orElseThrow(
                () -> new ResourceNotFoundException(ROLE_NOT_FOUND_WITH_ID_MESSAGE + id));
        roleRepository.deleteById(id);
        employeeSearchCache.invalidateAll();
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.ROLE_BY_NAME, key = "#roleName")
    public Role findRoleByName(String roleName) {
        return roleRepository.findByName(roleName)
            .orElseThrow(() ->
                new ResourceNotFoundException(ROLE_NOT_FOUND_WITH_NAME_MESSAGE + roleName));
    }

    private String normalizeFilterValueForQuery(String value) {
        if (value == null) {
            return "";
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return "";
        }
        return trimmed.toLowerCase();
    }
}
