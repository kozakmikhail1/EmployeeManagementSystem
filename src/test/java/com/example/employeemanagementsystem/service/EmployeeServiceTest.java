package com.example.employeemanagementsystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.employeemanagementsystem.cache.EmployeeSearchCache;
import com.example.employeemanagementsystem.dto.create.EmployeeCreateDto;
import com.example.employeemanagementsystem.dto.get.EmployeeDto;
import com.example.employeemanagementsystem.exception.ResourceConflictException;
import com.example.employeemanagementsystem.mapper.EmployeeMapper;
import com.example.employeemanagementsystem.mapper.UserMapper;
import com.example.employeemanagementsystem.model.Employee;
import com.example.employeemanagementsystem.repository.EmployeeRepository;
import com.example.employeemanagementsystem.repository.RoleRepository;
import com.example.employeemanagementsystem.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RoleService roleService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmployeeSearchCache employeeSearchCache;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void createEmployeesBulkSuccess() {
        EmployeeCreateDto dto1 = new EmployeeCreateDto();
        EmployeeCreateDto dto2 = new EmployeeCreateDto();

        Employee entity1 = new Employee();
        Employee entity2 = new Employee();
        Employee saved1 = new Employee();
        saved1.setId(1L);
        Employee saved2 = new Employee();
        saved2.setId(2L);

        EmployeeDto out1 = new EmployeeDto();
        out1.setId(1L);
        EmployeeDto out2 = new EmployeeDto();
        out2.setId(2L);

        when(employeeMapper.toEntity(dto1)).thenReturn(entity1);
        when(employeeMapper.toEntity(dto2)).thenReturn(entity2);
        when(employeeRepository.save(entity1)).thenReturn(saved1);
        when(employeeRepository.save(entity2)).thenReturn(saved2);
        when(employeeMapper.toDto(saved1)).thenReturn(out1);
        when(employeeMapper.toDto(saved2)).thenReturn(out2);

        List<EmployeeDto> result = employeeService.createEmployeesBulk(List.of(dto1, dto2));

        assertEquals(2, result.size());
        verify(employeeRepository).save(entity1);
        verify(employeeRepository).save(entity2);
        verify(employeeSearchCache).invalidateAll();
    }

    @Test
    void createEmployeesBulkConflictOnUserThrows() {
        EmployeeCreateDto dto = new EmployeeCreateDto();
        dto.setUserId(10L);
        Employee entity = new Employee();

        when(employeeMapper.toEntity(dto)).thenReturn(entity);
        when(employeeRepository.existsByUserId(10L)).thenReturn(true);

        assertThrows(ResourceConflictException.class,
                () -> employeeService.createEmployeesBulk(List.of(dto)));

        verify(employeeRepository, never()).save(entity);
        verify(employeeSearchCache).invalidateAll();
    }

    @Test
    void createEmployeesBulkWithoutTransactionPartialSaveBeforeFailure() {
        EmployeeCreateDto dto1 = new EmployeeCreateDto();
        EmployeeCreateDto dto2 = new EmployeeCreateDto();
        dto2.setUserId(10L);

        Employee entity1 = new Employee();
        Employee entity2 = new Employee();
        Employee saved1 = new Employee();
        saved1.setId(1L);

        EmployeeDto out1 = new EmployeeDto();
        out1.setId(1L);

        when(employeeMapper.toEntity(dto1)).thenReturn(entity1);
        when(employeeMapper.toEntity(dto2)).thenReturn(entity2);
        when(employeeRepository.save(entity1)).thenReturn(saved1);
        when(employeeMapper.toDto(saved1)).thenReturn(out1);
        when(employeeRepository.existsByUserId(10L)).thenReturn(true);

        assertThrows(ResourceConflictException.class,
                () -> employeeService.createEmployeesBulkWithoutTransaction(List.of(dto1, dto2)));

        verify(employeeRepository).save(entity1);
        verify(employeeRepository, never()).save(entity2);
        verify(employeeSearchCache).invalidateAll();
    }
}
