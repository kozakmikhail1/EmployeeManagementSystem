package com.example.employeemanagementsystem.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.employeemanagementsystem.dto.create.DepartmentCreateDto;
import com.example.employeemanagementsystem.dto.get.DepartmentDto;
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
    private final UserService userService; // Добавляем UserService

    @Autowired
    public DepartmentService(
        DepartmentRepository departmentRepository, DepartmentMapper departmentMapper,
        UserService userService) {
        this.departmentRepository = departmentRepository;
        this.departmentMapper = departmentMapper;
        this.userService = userService; // Внедряем UserService
    }

    @Transactional(readOnly = true)
    public DepartmentDto getDepartmentById(Long id) {
        return departmentRepository
            .findById(id)
            .map(departmentMapper::toDto)
            .orElseThrow(
                () -> new ResourceNotFoundException(DEPARTMENT_NOT_FOUND_MESSAGE + id));
    }

    @Transactional(readOnly = true)
    public List<DepartmentDto> getAllDepartments() {
        return departmentRepository.findAll().stream()
            .map(departmentMapper::toDto)
            .toList();
    }

    @Transactional
    public DepartmentDto createDepartment(DepartmentCreateDto departmentDto) {
        Department department = departmentMapper.toEntity(departmentDto);
        Department savedDepartment = departmentRepository.save(department);
        return departmentMapper.toDto(savedDepartment);
    }

    @Transactional
    public DepartmentDto updateDepartment(Long id, DepartmentCreateDto departmentDto) {
        Department department = departmentRepository
            .findById(id)
            .orElseThrow(
                () -> new ResourceNotFoundException(DEPARTMENT_NOT_FOUND_MESSAGE + id));
        departmentMapper.updateDepartmentFromDto(departmentDto, department);
        Department updatedDepartment = departmentRepository.save(department);
        return departmentMapper.toDto(updatedDepartment);
    }

    @Transactional
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
            .orElseThrow(
                () -> new ResourceNotFoundException(DEPARTMENT_NOT_FOUND_MESSAGE + id));

        // Сначала удаляем User для каждого Employee
        for (Employee employee : department.getEmployees()) {
            if (employee.getUser() != null) {
                userService.deleteUser(employee.getUser().getId()); // Используем UserService
            }
        }

        // Теперь безопасно удаляем Department (Employee удалятся каскадно)
        departmentRepository.delete(department);
    }
}
