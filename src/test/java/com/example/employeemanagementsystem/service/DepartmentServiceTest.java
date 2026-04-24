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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.example.employeemanagementsystem.cache.EmployeeSearchCache;
import com.example.employeemanagementsystem.dto.create.DepartmentCreateDto;
import com.example.employeemanagementsystem.dto.get.DepartmentDto;
import com.example.employeemanagementsystem.dto.patch.DepartmentPatchDto;
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
    void getDepartmentByIdSuccessReturnsMappedDto() {
        Department department = new Department();
        DepartmentDto dto = new DepartmentDto();
        when(departmentRepository.findById(11L)).thenReturn(Optional.of(department));
        when(departmentMapper.toDto(department)).thenReturn(dto);

        DepartmentDto result = departmentService.getDepartmentById(11L);

        org.junit.jupiter.api.Assertions.assertEquals(dto, result);
    }

    @Test
    void getAllDepartmentsMapsAllEntries() {
        Department department = new Department();
        DepartmentDto departmentDto = new DepartmentDto();

        when(departmentRepository.findAll()).thenReturn(List.of(department));
        when(departmentMapper.toDto(department)).thenReturn(departmentDto);

        List<DepartmentDto> result = departmentService.getAllDepartments();

        org.junit.jupiter.api.Assertions.assertEquals(1, result.size());
        verify(departmentMapper).toDto(department);
    }

    @Test
    void getDepartmentsPageWithNullFilterUsesEmptyQuery() {
        Pageable pageable = PageRequest.of(0, 10);
        Department department = new Department();
        DepartmentDto departmentDto = new DepartmentDto();
        Page<Department> page = new PageImpl<>(List.of(department));
        when(departmentRepository.searchPage("", pageable)).thenReturn(page);
        when(departmentMapper.toDto(department)).thenReturn(departmentDto);

        Page<DepartmentDto> result = departmentService.getDepartmentsPage(null, pageable);

        org.junit.jupiter.api.Assertions.assertEquals(1, result.getContent().size());
        verify(departmentRepository).searchPage("", pageable);
    }

    @Test
    void getDepartmentsPageWithBlankFilterUsesEmptyQuery() {
        Pageable pageable = PageRequest.of(0, 10);
        when(departmentRepository.searchPage("", pageable)).thenReturn(Page.empty(pageable));

        Page<DepartmentDto> result = departmentService.getDepartmentsPage("   ", pageable);

        org.junit.jupiter.api.Assertions.assertEquals(0, result.getContent().size());
        verify(departmentRepository).searchPage("", pageable);
    }

    @Test
    void getDepartmentsPageNormalizesFilterToLowerCase() {
        Pageable pageable = PageRequest.of(0, 10);
        when(departmentRepository.searchPage("finance", pageable)).thenReturn(Page.empty(pageable));

        departmentService.getDepartmentsPage("  FINANCE ", pageable);

        verify(departmentRepository).searchPage("finance", pageable);
    }

    @Test
    void createDepartmentSavesAndInvalidatesCache() {
        DepartmentCreateDto createDto = new DepartmentCreateDto();
        Department department = new Department();
        Department savedDepartment = new Department();
        DepartmentDto departmentDto = new DepartmentDto();

        when(departmentMapper.toEntity(createDto)).thenReturn(department);
        when(departmentRepository.save(department)).thenReturn(savedDepartment);
        when(departmentMapper.toDto(savedDepartment)).thenReturn(departmentDto);

        DepartmentDto result = departmentService.createDepartment(createDto);

        org.junit.jupiter.api.Assertions.assertEquals(departmentDto, result);
        verify(employeeSearchCache).invalidateAll();
    }

    @Test
    void patchDepartmentNotFoundThrows() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());
        DepartmentPatchDto patchDto = new DepartmentPatchDto();

        assertThrows(ResourceNotFoundException.class,
                () -> departmentService.patchDepartment(1L, patchDto));
    }

    @Test
    void patchDepartmentSuccessInvalidatesCache() {
        DepartmentPatchDto patchDto = new DepartmentPatchDto();
        Department department = new Department();
        Department savedDepartment = new Department();
        DepartmentDto dto = new DepartmentDto();

        when(departmentRepository.findById(3L)).thenReturn(Optional.of(department));
        when(departmentRepository.save(department)).thenReturn(savedDepartment);
        when(departmentMapper.toDto(savedDepartment)).thenReturn(dto);

        DepartmentDto result = departmentService.patchDepartment(3L, patchDto);

        org.junit.jupiter.api.Assertions.assertEquals(dto, result);
        verify(departmentMapper).updateDepartmentFromPatchDto(patchDto, department);
        verify(employeeSearchCache).invalidateAll();
    }

    @Test
    void updateDepartmentUpdatesEntityAndInvalidatesCache() {
        DepartmentCreateDto createDto = new DepartmentCreateDto();
        Department existingDepartment = new Department();
        Department updatedDepartment = new Department();
        DepartmentDto departmentDto = new DepartmentDto();

        when(departmentRepository.findById(2L)).thenReturn(Optional.of(existingDepartment));
        when(departmentRepository.save(existingDepartment)).thenReturn(updatedDepartment);
        when(departmentMapper.toDto(updatedDepartment)).thenReturn(departmentDto);

        DepartmentDto result = departmentService.updateDepartment(2L, createDto);

        org.junit.jupiter.api.Assertions.assertEquals(departmentDto, result);
        verify(departmentMapper).updateDepartmentFromDto(createDto, existingDepartment);
        verify(employeeSearchCache).invalidateAll();
    }

    @Test
    void updateDepartmentNotFoundThrows() {
        when(departmentRepository.findById(22L)).thenReturn(Optional.empty());
        DepartmentCreateDto createDto = new DepartmentCreateDto();

        assertThrows(ResourceNotFoundException.class,
                () -> departmentService.updateDepartment(22L, createDto));
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

    @Test
    void deleteDepartmentNotFoundThrows() {
        when(departmentRepository.findWithEmployeesById(42L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> departmentService.deleteDepartment(42L));
    }
}
