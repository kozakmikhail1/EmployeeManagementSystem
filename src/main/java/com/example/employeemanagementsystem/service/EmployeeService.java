package com.example.employeemanagementsystem.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.employeemanagementsystem.dto.create.EmployeeCreateDto;
import com.example.employeemanagementsystem.dto.create.UserCreateDto;
import com.example.employeemanagementsystem.dto.get.EmployeeDto;
import com.example.employeemanagementsystem.exception.ResourceNotFoundException;
import com.example.employeemanagementsystem.mapper.EmployeeMapper;
import com.example.employeemanagementsystem.mapper.UserMapper;
import com.example.employeemanagementsystem.model.Employee;
import com.example.employeemanagementsystem.model.User;
import com.example.employeemanagementsystem.repository.DepartmentRepository;
import com.example.employeemanagementsystem.repository.EmployeeRepository;
import com.example.employeemanagementsystem.repository.UserRepository;

@Service
public class EmployeeService {

    private static final String EMPLOYEE_NOT_FOUND_MESS = "Employee not found with id ";

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository,
            EmployeeMapper employeeMapper,
            UserRepository userRepository,
            DepartmentRepository departmentRepository,
            UserMapper userMapper) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.userMapper = userMapper;
    }

    @Transactional
    public EmployeeDto createEmployee(EmployeeCreateDto employeeDto) {
        Employee employee = employeeMapper.toEntity(employeeDto);

        User user = userRepository.findById(employeeDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id " + employeeDto.getUserId()));
        employee.setUser(user);
        Employee savedEmployee = employeeRepository.save(employee);

        return employeeMapper.toDto(savedEmployee);
    }

    @Transactional
    public EmployeeDto createEmployeeWithUser(EmployeeCreateDto employeeDto, UserCreateDto userDto) {
        Employee employee = employeeMapper.toEntity(employeeDto);
        User user = userMapper.toEntity(userDto);

        employee.setUser(user);
        user.setEmployee(employee);
        Employee savedEmployee = employeeRepository.save(employee);

        EmployeeDto result = employeeMapper.toDto(savedEmployee);
        return result;
    }

    @Transactional
    public EmployeeDto updateEmployee(Long id, EmployeeCreateDto employeeDto) {
        Employee employee = employeeRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        EMPLOYEE_NOT_FOUND_MESS + id));

        if (employeeDto.getUserId() != null
                && !employeeDto.getUserId().equals(employee.getUser().getId())) {
            User user = userRepository
                    .findById(employeeDto.getUserId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException(
                                    "User not found with id " + employeeDto.getUserId()));
            employee.setUser(user);
        }

        employeeMapper.updateEmployeeFromDto(employeeDto, employee);
        Employee updatedEmployee = employeeRepository.save(employee);
        return employeeMapper.toDto(updatedEmployee);
    }

    @Transactional(readOnly = true)
    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public EmployeeDto getEmployeeDtoById(Long id) {
        return employeeMapper.toDto(employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        EMPLOYEE_NOT_FOUND_MESS + id)));
    }

    @Transactional(readOnly = true)
    public List<EmployeeDto> getAllEmployees() {
        return employeeRepository.findAll().stream().map(employeeMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<EmployeeDto> getEmployeesBySalaryRange(BigDecimal minSalary, BigDecimal maxSalary) {
        if (minSalary == null && maxSalary == null) {
            return employeeMapper.toDto(employeeRepository.findAll());
        } else if (minSalary == null) {
            return employeeMapper.toDto(employeeRepository.findBySalaryLessThanEqual(maxSalary));
        } else if (maxSalary == null) {
            return employeeMapper.toDto(employeeRepository.findBySalaryGreaterThanEqual(minSalary));
        } else {
            return employeeMapper.toDto(employeeRepository.findBySalaryBetween(minSalary, maxSalary));
        }
    }

    @Transactional(readOnly = true)
    public List<EmployeeDto> getEmployeesByDepartmentId(Long departmentId) {
        List<Employee> employees = employeeRepository.findByDepartmentId(departmentId);
        return employees.stream().map(employeeMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<EmployeeDto> getEmployeesByPositionId(Long positionId) {
        List<Employee> employees = employeeRepository.findByPositionId(positionId);
        return employees.stream().map(employeeMapper::toDto).toList();
    }

    @Transactional
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException(EMPLOYEE_NOT_FOUND_MESS + id);
        }
        employeeRepository.deleteById(id);
    }

    public Employee getEmployeeByUserId(Long id) {
        return employeeRepository.findByUserId(id);
    }

    @Transactional
    public Employee updateEmployeeWithoutDto(Employee employee) {
        return employeeRepository.save(employee);
    }
}
