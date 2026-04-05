package com.example.employeemanagementsystem.service;

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
import com.example.employeemanagementsystem.exception.ResourceNotFoundException;
import com.example.employeemanagementsystem.mapper.DepartmentMapper;
import com.example.employeemanagementsystem.model.Department;
import com.example.employeemanagementsystem.model.Employee;
import com.example.employeemanagementsystem.model.User;
import com.example.employeemanagementsystem.repository.DepartmentRepository;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private DepartmentMapper departmentMapper;

    @Mock
    private UserService userService;

    @Mock
    private EmployeeSearchCache employeeSearchCache;

    @InjectMocks
    private DepartmentService departmentService;

    @Test
    void getDepartmentByIdNotFoundThrows() {
        when(departmentRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> departmentService.getDepartmentById(10L));
    }

    @Test
    void deleteDepartmentRemovesLinkedUsersAndDepartment() {
        User linkedUser = new User();
        linkedUser.setId(99L);

        Employee employeeWithUser = new Employee();
        employeeWithUser.setUser(linkedUser);

        Employee employeeWithoutUser = new Employee();
        employeeWithoutUser.setUser(null);

        Department department = new Department();
        department.setId(1L);
        department.setEmployees(List.of(employeeWithUser, employeeWithoutUser));

        when(departmentRepository.findWithEmployeesById(1L)).thenReturn(Optional.of(department));

        departmentService.deleteDepartment(1L);

        verify(userService).deleteUser(99L);
        verify(departmentRepository).delete(department);
        verify(employeeSearchCache).invalidateAll();
    }
}
