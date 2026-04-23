package com.example.employeemanagementsystem.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.employeemanagementsystem.cache.CacheNames;
import com.example.employeemanagementsystem.cache.EmployeeSearchCache;
import com.example.employeemanagementsystem.cache.InvalidateReadCaches;
import com.example.employeemanagementsystem.dto.create.DepartmentCreateDto;
import com.example.employeemanagementsystem.dto.get.DepartmentDto;
import com.example.employeemanagementsystem.dto.patch.DepartmentPatchDto;
import com.example.employeemanagementsystem.exception.ResourceNotFoundException;
import com.example.employeemanagementsystem.mapper.DepartmentMapper;
import com.example.employeemanagementsystem.model.Department;
import com.example.employeemanagementsystem.model.Employee;
import com.example.employeemanagementsystem.repository.DepartmentRepository;

@Service
public class DepartmentService {

    private static final String DEPARTMENT_NOT_FOUND_MESSAGE = "Department not found with id ";

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;
    private final UserService userService;
    private final EmployeeSearchCache employeeSearchCache;

    @Autowired
    public DepartmentService(
            DepartmentRepository departmentRepository, DepartmentMapper departmentMapper,
            UserService userService, EmployeeSearchCache employeeSearchCache) {
        this.departmentRepository = departmentRepository;
        this.departmentMapper = departmentMapper;
        this.userService = userService;
        this.employeeSearchCache = employeeSearchCache;
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.DEPARTMENT_BY_ID, key = "#id")
    public DepartmentDto getDepartmentById(Long id) {
        return departmentRepository
                .findById(id)
                .map(departmentMapper::toDto)
                .orElseThrow(
                        () -> new ResourceNotFoundException(DEPARTMENT_NOT_FOUND_MESSAGE + id));
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.DEPARTMENTS_ALL)
    public List<DepartmentDto> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(departmentMapper::toDto)
                .toList();
    }

    @Transactional
    @InvalidateReadCaches
    public DepartmentDto createDepartment(DepartmentCreateDto departmentDto) {
        Department department = departmentMapper.toEntity(departmentDto);
        Department savedDepartment = departmentRepository.save(department);
        employeeSearchCache.invalidateAll();
        return departmentMapper.toDto(savedDepartment);
    }

    @Transactional
    @InvalidateReadCaches
    public DepartmentDto updateDepartment(Long id, DepartmentCreateDto departmentDto) {
        Department department = departmentRepository
                .findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(DEPARTMENT_NOT_FOUND_MESSAGE + id));
        departmentMapper.updateDepartmentFromDto(departmentDto, department);
        Department updatedDepartment = departmentRepository.save(department);
        employeeSearchCache.invalidateAll();
        return departmentMapper.toDto(updatedDepartment);
    }

    @Transactional
    @InvalidateReadCaches
    public DepartmentDto patchDepartment(Long id, DepartmentPatchDto departmentDto) {
        Department department = departmentRepository
                .findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(DEPARTMENT_NOT_FOUND_MESSAGE + id));
        departmentMapper.updateDepartmentFromPatchDto(departmentDto, department);
        Department updatedDepartment = departmentRepository.save(department);
        employeeSearchCache.invalidateAll();
        return departmentMapper.toDto(updatedDepartment);
    }

    @Transactional
    @InvalidateReadCaches
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findWithEmployeesById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(DEPARTMENT_NOT_FOUND_MESSAGE + id));

        for (Employee employee : department.getEmployees()) {
            if (employee.getUser() != null) {
                userService.deleteUser(employee.getUser().getId());
            }
        }

        departmentRepository.delete(department);
        employeeSearchCache.invalidateAll();
    }
}
