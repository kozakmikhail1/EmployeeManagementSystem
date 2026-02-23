package com.example.employeemanagementsystem.service;

import com.example.employeemanagementsystem.repository.RoleDao;
import com.example.employeemanagementsystem.dto.create.RoleCreateDto;
import com.example.employeemanagementsystem.dto.get.RoleDto;
import com.example.employeemanagementsystem.exception.ResourceNotFoundException;
import com.example.employeemanagementsystem.mapper.RoleMapper;
import com.example.employeemanagementsystem.model.Role;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleService {

    private static final String ROLE_NOT_FOUND_WITH_ID_MESSAGE = "Role not found with id ";
    private static final String ROLE_NOT_FOUND_WITH_NAME_MESSAGE = "Role not found with name ";

    private final RoleDao roleDao;
    private final RoleMapper roleMapper;

    @Autowired
    public RoleService(RoleDao roleDao, RoleMapper roleMapper) {
        this.roleDao = roleDao;
        this.roleMapper = roleMapper;
    }

    @Transactional(readOnly = true)
    public RoleDto getRoleById(Long id) {
        return roleDao.findById(id)
            .map(roleMapper::toDto)
            .orElseThrow(() -> new ResourceNotFoundException(ROLE_NOT_FOUND_WITH_ID_MESSAGE + id));
    }

    @Transactional(readOnly = true)
    public List<RoleDto> getAllRoles() {
        return roleDao.findAll().stream()
            .map(roleMapper::toDto)
            .collect(Collectors.toList()); 
    }

    @Transactional
    public RoleDto createRole(RoleCreateDto roleCreateDto) {
        Role role = roleMapper.toEntity(roleCreateDto);
        Role savedRole = roleDao.save(role);
        return roleMapper.toDto(savedRole);
    }

    @Transactional
    public RoleDto updateRole(Long id, RoleCreateDto roleCreateDto) {
        Role role = roleDao.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(ROLE_NOT_FOUND_WITH_ID_MESSAGE + id));
        roleMapper.updateRoleFromDto(roleCreateDto, role);
        Role updatedRole = roleDao.save(role);
        return roleMapper.toDto(updatedRole);
    }

    @Transactional
    public void deleteRole(Long id) {
        roleDao.findById(id) 
            .orElseThrow(() -> new ResourceNotFoundException(ROLE_NOT_FOUND_WITH_ID_MESSAGE + id));
        roleDao.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Role findRoleByName(String roleName) {
        return roleDao.findByName(roleName)
            .orElseThrow(() ->
                new ResourceNotFoundException(ROLE_NOT_FOUND_WITH_NAME_MESSAGE + roleName));
    }
}