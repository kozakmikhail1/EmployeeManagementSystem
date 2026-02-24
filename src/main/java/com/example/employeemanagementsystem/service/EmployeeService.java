package com.example.employeemanagementsystem.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.employeemanagementsystem.dto.create.EmployeeCreateDto;
import com.example.employeemanagementsystem.dto.get.EmployeeDto;
import com.example.employeemanagementsystem.exception.ResourceNotFoundException;
import com.example.employeemanagementsystem.mapper.EmployeeMapper;
import com.example.employeemanagementsystem.model.Employee;
import com.example.employeemanagementsystem.model.User;
import com.example.employeemanagementsystem.repository.EmployeeDao;
import com.example.employeemanagementsystem.repository.UserDao;

@Service
public class EmployeeService {

    private static final String EMPLOYEE_NOT_FOUND_MESSAGE = "Employee not found with id ";

    private final EmployeeDao employeeDao;
    private final EmployeeMapper employeeMapper;
    private final UserDao userDao;

    @Autowired
    public EmployeeService(EmployeeDao employeeDao,
                           EmployeeMapper employeeMapper,
                           UserDao userDao) {
        this.employeeDao = employeeDao;
        this.employeeMapper = employeeMapper;
        this.userDao = userDao;
    }

    @Transactional
    public EmployeeDto createEmployee(EmployeeCreateDto employeeDto) {
        Employee employee = employeeMapper.toEntity(employeeDto);

        User user = userDao.findById(employeeDto.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id " + employeeDto.getUserId()));
        employee.setUser(user);
        Employee savedEmployee = employeeDao.save(employee);

        return employeeMapper.toDto(savedEmployee);
    }

    @Transactional
    public EmployeeDto updateEmployee(Long id, EmployeeCreateDto employeeDto) {
        Employee employee =
                employeeDao
                        .findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                EMPLOYEE_NOT_FOUND_MESSAGE + id));

        if (employeeDto.getUserId() != null
                && !employeeDto.getUserId().equals(employee.getUser().getId())) {
            User user = userDao
                    .findById(employeeDto.getUserId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException(
                                    "User not found with id " + employeeDto.getUserId()));
            employee.setUser(user);
        }

        employeeMapper.updateEmployeeFromDto(employeeDto, employee);
        Employee updatedEmployee = employeeDao.save(employee);
        return employeeMapper.toDto(updatedEmployee);
    }


    @Transactional(readOnly = true)
    public Optional<Employee> getEmployeeById(Long id) {
        return employeeDao.findById(id);
    }

    @Transactional(readOnly = true)
    public EmployeeDto getEmployeeDtoById(Long id) {
        return employeeDao.findById(id)
                .map(employeeMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(EMPLOYEE_NOT_FOUND_MESSAGE + id));
    }

    @Transactional(readOnly = true)
    public List<EmployeeDto> getAllEmployees() {
        return employeeDao.findAll().stream().map(employeeMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<Employee> getEmployeesBySalaryRange(BigDecimal minSalary, BigDecimal maxSalary) {
        if (minSalary == null && maxSalary == null) {
            return employeeDao.findAll();
        } else if (minSalary == null) {
            return employeeDao.findBySalaryLessThanEqual(maxSalary);
        } else if (maxSalary == null) {
            return employeeDao.findBySalaryGreaterThanEqual(minSalary);
        } else {
            return employeeDao.findBySalaryBetween(minSalary, maxSalary);
        }
    }

    @Transactional(readOnly = true)
    public List<EmployeeDto> getEmployeesByDepartmentId(Long departmentId) {
        List<Employee> employees = employeeDao.findByDepartmentId(departmentId);
        return employees.stream().map(employeeMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<EmployeeDto> getEmployeesByPositionId(Long positionId) {
        List<Employee> employees = employeeDao.findByPositionId(positionId);
        return employees.stream().map(employeeMapper::toDto).toList();
    }

    @Transactional
    public void deleteEmployee(Long id) {
        if (!employeeDao.existsById(id)) {
            throw new ResourceNotFoundException(EMPLOYEE_NOT_FOUND_MESSAGE + id);
        }
        employeeDao.deleteById(id);
    }

    @Transactional
    public Employee updateEmployeeWithoutDto(Employee employee) {
        return employeeDao.save(employee);
    }
}