package com.example.employeemanagementsystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.employeemanagementsystem.cache.EmployeeSearchCache;
import com.example.employeemanagementsystem.dto.create.RoleCreateDto;
import com.example.employeemanagementsystem.dto.get.RoleDto;
import com.example.employeemanagementsystem.exception.ResourceNotFoundException;
import com.example.employeemanagementsystem.mapper.RoleMapper;
import com.example.employeemanagementsystem.model.Role;
import com.example.employeemanagementsystem.repository.RoleRepository;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private EmployeeSearchCache employeeSearchCache;

    @InjectMocks
    private RoleService roleService;

    @Test
    void createRoleSuccess() {
        RoleCreateDto createDto = new RoleCreateDto();
        createDto.setName("ADMIN");

        Role role = new Role();
        Role savedRole = new Role();
        savedRole.setId(1L);
        savedRole.setName("ADMIN");

        RoleDto roleDto = new RoleDto();
        roleDto.setId(1L);
        roleDto.setName("ADMIN");

        when(roleMapper.toEntity(createDto)).thenReturn(role);
        when(roleRepository.save(role)).thenReturn(savedRole);
        when(roleMapper.toDto(savedRole)).thenReturn(roleDto);

        RoleDto result = roleService.createRole(createDto);

        assertEquals(1L, result.getId());
        verify(employeeSearchCache).invalidateAll();
    }

    @Test
    void getRoleByIdNotFoundThrows() {
        when(roleRepository.findById(777L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roleService.getRoleById(777L));
    }
}
