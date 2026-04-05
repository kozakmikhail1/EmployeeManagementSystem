package com.example.employeemanagementsystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.employeemanagementsystem.cache.EmployeeSearchCache;
import com.example.employeemanagementsystem.dto.create.RoleCreateDto;
import com.example.employeemanagementsystem.dto.get.RoleDto;
import com.example.employeemanagementsystem.dto.patch.RolePatchDto;
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

    @Test
    void getRoleByIdSuccessReturnsMappedDto() {
        Role role = new Role();
        RoleDto dto = new RoleDto();
        when(roleRepository.findById(778L)).thenReturn(Optional.of(role));
        when(roleMapper.toDto(role)).thenReturn(dto);

        RoleDto result = roleService.getRoleById(778L);

        assertEquals(dto, result);
    }

    @Test
    void getAllRolesMapsEntities() {
        Role role = new Role();
        RoleDto dto = new RoleDto();
        when(roleRepository.findAll()).thenReturn(List.of(role));
        when(roleMapper.toDto(role)).thenReturn(dto);

        List<RoleDto> result = roleService.getAllRoles();

        assertEquals(1, result.size());
        verify(roleMapper).toDto(role);
    }

    @Test
    void updateRoleNotFoundThrows() {
        when(roleRepository.findById(10L)).thenReturn(Optional.empty());
        RoleCreateDto createDto = new RoleCreateDto();

        assertThrows(ResourceNotFoundException.class,
                () -> roleService.updateRole(10L, createDto));
    }

    @Test
    void updateRoleSuccessInvalidatesCache() {
        RoleCreateDto updateDto = new RoleCreateDto();
        Role role = new Role();
        Role savedRole = new Role();
        RoleDto dto = new RoleDto();

        when(roleRepository.findById(11L)).thenReturn(Optional.of(role));
        when(roleRepository.save(role)).thenReturn(savedRole);
        when(roleMapper.toDto(savedRole)).thenReturn(dto);

        RoleDto result = roleService.updateRole(11L, updateDto);

        assertEquals(dto, result);
        verify(roleMapper).updateRoleFromDto(updateDto, role);
        verify(employeeSearchCache).invalidateAll();
    }

    @Test
    void patchRoleSuccess() {
        RolePatchDto patchDto = new RolePatchDto();
        Role existingRole = new Role();
        Role savedRole = new Role();
        RoleDto dto = new RoleDto();

        when(roleRepository.findById(12L)).thenReturn(Optional.of(existingRole));
        when(roleRepository.save(existingRole)).thenReturn(savedRole);
        when(roleMapper.toDto(savedRole)).thenReturn(dto);

        RoleDto result = roleService.patchRole(12L, patchDto);

        assertEquals(dto, result);
        verify(roleMapper).updateRoleFromPatchDto(patchDto, existingRole);
        verify(employeeSearchCache).invalidateAll();
    }

    @Test
    void patchRoleNotFoundThrows() {
        when(roleRepository.findById(13L)).thenReturn(Optional.empty());
        RolePatchDto patchDto = new RolePatchDto();

        assertThrows(ResourceNotFoundException.class,
                () -> roleService.patchRole(13L, patchDto));
    }

    @Test
    void findRoleByNameNotFoundThrows() {
        when(roleRepository.findByName("MANAGER")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roleService.findRoleByName("MANAGER"));
    }

    @Test
    void findRoleByNameSuccessReturnsRole() {
        Role role = new Role();
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(role));

        Role result = roleService.findRoleByName("USER");

        assertEquals(role, result);
    }

    @Test
    void deleteRoleSuccessInvalidatesCache() {
        Role role = new Role();
        when(roleRepository.findById(14L)).thenReturn(Optional.of(role));

        roleService.deleteRole(14L);

        verify(roleRepository).deleteById(14L);
        verify(employeeSearchCache).invalidateAll();
    }

    @Test
    void deleteRoleNotFoundThrows() {
        when(roleRepository.findById(15L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roleService.deleteRole(15L));
    }
}
