package com.example.employeemanagementsystem.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.employeemanagementsystem.dto.create.EmployeeCreateDto;
import com.example.employeemanagementsystem.dto.patch.EmployeePatchDto;
import com.example.employeemanagementsystem.dto.get.EmployeeDto;
import com.example.employeemanagementsystem.exception.ResourceNotFoundException;
import com.example.employeemanagementsystem.model.Employee;
import com.example.employeemanagementsystem.repository.DepartmentRepository;
import com.example.employeemanagementsystem.repository.PositionRepository;
import com.example.employeemanagementsystem.repository.UserRepository;

import io.micrometer.common.lang.NonNullApi;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@NonNullApi
public class EmployeeMapper {

    private static final String DEPARTMENT_NOT_FOUND_MESSAGE = "Department not found";
    private static final String POSITION_NOT_FOUND_MESSAGE = "Position not found";
    private static final String USER_NOT_FOUND_MESSAGE = "User not found";

    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final UserRepository userRepository;


    public Employee toEntity(EmployeeCreateDto dto) {
        if (dto == null) {
            return null;
        }

        Employee employee = new Employee();
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setEmail(dto.getEmail());
        employee.setHireDate(dto.getHireDate());
        employee.setSalary(dto.getSalary());
        employee.setIsActive(dto.getIsActive());

        if (dto.getDepartmentId() != null) {
            employee.setDepartment(departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException(DEPARTMENT_NOT_FOUND_MESSAGE)));
        }

        if (dto.getPositionId() != null) {
            employee.setPosition(positionRepository.findById(dto.getPositionId())
                    .orElseThrow(() -> new ResourceNotFoundException(POSITION_NOT_FOUND_MESSAGE)));
        }

        if (dto.getUserId() != null) {
            employee.setUser(userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE)));
        }

        return employee;
    }

    public List<Employee> toEntity(List<EmployeeCreateDto> entity) {
        if (entity == null) {
            return null;
        }

        List<Employee> employeeList = new ArrayList<>();

        for (EmployeeCreateDto x : entity) {
            employeeList.add(toEntity(x));
        }
        return employeeList;
    }

    public EmployeeDto toDto(Employee entity) {
        if (entity == null) {
            return null;
        }

        EmployeeDto dto = new EmployeeDto();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setEmail(entity.getEmail());
        dto.setHireDate(entity.getHireDate());
        dto.setSalary(entity.getSalary());
        dto.setIsActive(entity.getIsActive());

        if (entity.getDepartment() != null) {
            dto.setDepartmentId(entity.getDepartment().getId());
        }

        if (entity.getPosition() != null) {
            dto.setPositionId(entity.getPosition().getId());
        }

        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
        }

        return dto;
    }

    public List<EmployeeDto> toDto(List<Employee> entity) {
        if (entity == null) {
            return null;
        }

        List<EmployeeDto> employeeDtoList = new ArrayList<>();

        for (Employee x : entity) {
            employeeDtoList.add(toDto(x));
        }
        return employeeDtoList;
    }

    public void updateEmployeeFromDto(EmployeeCreateDto dto, Employee entity) {
        if (dto == null || entity == null) {
            return;
        }

        applyEmployeeFields(
                dto.getFirstName(),
                dto.getLastName(),
                dto.getEmail(),
                dto.getHireDate(),
                dto.getSalary(),
                dto.getIsActive(),
                entity);
        applyEmployeeRelations(dto.getDepartmentId(), dto.getPositionId(), dto.getUserId(), entity);
    }

    public void updateEmployeeFromPatchDto(EmployeePatchDto dto, Employee entity) {
        if (dto == null || entity == null) {
            return;
        }

        applyEmployeeFields(
                dto.getFirstName(),
                dto.getLastName(),
                dto.getEmail(),
                dto.getHireDate(),
                dto.getSalary(),
                dto.getIsActive(),
                entity);
        applyEmployeeRelations(dto.getDepartmentId(), dto.getPositionId(), dto.getUserId(), entity);
    }

    private void applyEmployeeFields(
            String firstName,
            String lastName,
            String email,
            java.time.LocalDate hireDate,
            java.math.BigDecimal salary,
            Boolean isActive,
            Employee entity) {
        if (firstName != null) {
            entity.setFirstName(firstName);
        }
        if (lastName != null) {
            entity.setLastName(lastName);
        }
        if (email != null) {
            entity.setEmail(email);
        }
        if (hireDate != null) {
            entity.setHireDate(hireDate);
        }
        if (salary != null) {
            entity.setSalary(salary);
        }
        if (isActive != null) {
            entity.setIsActive(isActive);
        }
    }

    private void applyEmployeeRelations(
            Long departmentId,
            Long positionId,
            Long userId,
            Employee entity) {
        if (departmentId != null) {
            entity.setDepartment(departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new ResourceNotFoundException(DEPARTMENT_NOT_FOUND_MESSAGE)));
        }

        if (positionId != null) {
            entity.setPosition(positionRepository.findById(positionId)
                    .orElseThrow(() -> new ResourceNotFoundException(POSITION_NOT_FOUND_MESSAGE)));
        }

        if (userId != null) {
            entity.setUser(userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE)));
        }
    }
    
}
