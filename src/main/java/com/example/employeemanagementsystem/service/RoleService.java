package com.example.employeemanagementsystem.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.employeemanagementsystem.dto.create.RoleCreateDto;
import com.example.employeemanagementsystem.dto.get.RoleDto;
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

    @Autowired
    public RoleService(RoleRepository roleRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    @Transactional(readOnly = true)
    public RoleDto getRoleById(Long id) {
        return roleRepository.findById(id)
            .map(roleMapper::toDto)
            .orElseThrow(
                () -> new ResourceNotFoundException(ROLE_NOT_FOUND_WITH_ID_MESSAGE + id));
    }

    @Transactional(readOnly = true)
    public List<RoleDto> getAllRoles() {
        return roleRepository.findAll().stream()
            .map(roleMapper::toDto)
            .toList();
    }

    @Transactional
    public RoleDto createRole(RoleCreateDto roleCreateDto) {
        Role role = roleMapper.toEntity(roleCreateDto);
        Role savedRole = roleRepository.save(role);
        return roleMapper.toDto(savedRole);
    }

    @Transactional
    public RoleDto updateRole(Long id, RoleCreateDto roleCreateDto) {
        Role role = roleRepository.findById(id)
            .orElseThrow(
                () -> new ResourceNotFoundException(ROLE_NOT_FOUND_WITH_ID_MESSAGE + id));
        roleMapper.updateRoleFromDto(roleCreateDto, role);
        Role updatedRole = roleRepository.save(role);
        return roleMapper.toDto(updatedRole);
    }

    @Transactional
    public void deleteRole(Long id) {
        roleRepository.findById(id)
            .orElseThrow(
                () -> new ResourceNotFoundException(ROLE_NOT_FOUND_WITH_ID_MESSAGE + id));
        roleRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Role findRoleByName(String roleName) {
        return roleRepository.findByName(roleName)
            .orElseThrow(() ->
                new ResourceNotFoundException(ROLE_NOT_FOUND_WITH_NAME_MESSAGE + roleName));
    }
}
