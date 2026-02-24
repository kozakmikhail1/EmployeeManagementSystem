package com.example.employeemanagementsystem.mapper;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import com.example.employeemanagementsystem.dto.create.EmployeeCreateDto;
import com.example.employeemanagementsystem.dto.get.EmployeeDto;
import com.example.employeemanagementsystem.exception.ResourceNotFoundException;
import com.example.employeemanagementsystem.model.Employee;
import com.example.employeemanagementsystem.repository.DepartmentRepository;
import com.example.employeemanagementsystem.repository.PositionRepository;
import com.example.employeemanagementsystem.repository.UserRepository;

@Component  // Используем обычный Spring компонент вместо маппера
@RequiredArgsConstructor
public class EmployeeMapper {

    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final UserRepository userRepository;
    private final DepartmentMapper departmentMapper;
    private final PositionMapper positionMapper;
    private final UserMapper userMapper;

    public Employee toEntity(EmployeeCreateDto dto) {
        if (dto == null) return null;

        Employee employee = new Employee();
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setEmail(dto.getEmail());
        employee.setHireDate(dto.getHireDate());
        employee.setSalary(dto.getSalary());
        employee.setIsActive(dto.getIsActive());

        if (dto.getDepartmentId() != null) {
            employee.setDepartment(departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found")));
        }

        if (dto.getPositionId() != null) {
            employee.setPosition(positionRepository.findById(dto.getPositionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Position not found")));
        }

        if (dto.getUserId() != null) {
            employee.setUser(userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found")));
        }

        return employee;
    }

    public EmployeeDto toDto(Employee entity) {
        if (entity == null) return null;

        EmployeeDto dto = new EmployeeDto();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setEmail(entity.getEmail());
        dto.setHireDate(entity.getHireDate());
        dto.setSalary(entity.getSalary());
        dto.setIsActive(entity.getIsActive());

        if (entity.getDepartment() != null) {
            dto.setDepartment(departmentMapper.toDto(entity.getDepartment()));
        }

        if (entity.getPosition() != null) {
            dto.setPosition(positionMapper.toDto(entity.getPosition()));
        }

        if (entity.getUser() != null) {
            dto.setUser(userMapper.toDto(entity.getUser()));
        }

        return dto;
    }

    public void updateEmployeeFromDto(EmployeeCreateDto dto, Employee entity) {
        if (dto == null || entity == null) return;

        if (dto.getFirstName() != null) entity.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) entity.setLastName(dto.getLastName());
        if (dto.getEmail() != null) entity.setEmail(dto.getEmail());
        if (dto.getHireDate() != null) entity.setHireDate(dto.getHireDate());
        if (dto.getSalary() != null) entity.setSalary(dto.getSalary());
        if (dto.getIsActive() != null) entity.setIsActive(dto.getIsActive());

        if (dto.getDepartmentId() != null) {
            entity.setDepartment(departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found")));
        }

        if (dto.getPositionId() != null) {
            entity.setPosition(positionRepository.findById(dto.getPositionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Position not found")));
        }

        if (dto.getUserId() != null) {
            entity.setUser(userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found")));
        }
    }
}