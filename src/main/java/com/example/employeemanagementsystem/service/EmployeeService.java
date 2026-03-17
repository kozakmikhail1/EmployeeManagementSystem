package com.example.employeemanagementsystem.service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.employeemanagementsystem.cache.EmployeeSearchCache;
import com.example.employeemanagementsystem.cache.EmployeeSearchCacheKey;
import com.example.employeemanagementsystem.dto.create.EmployeeCreateDto;
import com.example.employeemanagementsystem.dto.create.UserCreateDto;
import com.example.employeemanagementsystem.dto.get.EmployeeDto;
import com.example.employeemanagementsystem.exception.ResourceConflictException;
import com.example.employeemanagementsystem.exception.ResourceNotFoundException;
import com.example.employeemanagementsystem.mapper.EmployeeMapper;
import com.example.employeemanagementsystem.mapper.UserMapper;
import com.example.employeemanagementsystem.model.Employee;
import com.example.employeemanagementsystem.model.Role;
import com.example.employeemanagementsystem.model.User;
import com.example.employeemanagementsystem.repository.EmployeeRepository;
import com.example.employeemanagementsystem.repository.RoleRepository;
import com.example.employeemanagementsystem.repository.UserRepository;

@Service
public class EmployeeService {

    private static final String EMPLOYEE_NOT_FOUND_MESS = "Employee not found with id ";
    private static final String USER_ALREADY_ASSIGNED_MESSAGE =
            "User is already assigned to another employee. User id ";

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RoleService roleService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmployeeSearchCache employeeSearchCache;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository,
            EmployeeMapper employeeMapper,
            UserRepository userRepository,
            RoleRepository roleRepository,
            RoleService roleService,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            EmployeeSearchCache employeeSearchCache) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.roleService = roleService;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.employeeSearchCache = employeeSearchCache;
    }

    @Transactional
    public EmployeeDto createEmployee(EmployeeCreateDto employeeDto) {
        Employee employee = employeeMapper.toEntity(employeeDto);

        if (employeeDto.getUserId() != null) {
            if (employeeRepository.existsByUserId(employeeDto.getUserId())) {
                throw new ResourceConflictException(
                        USER_ALREADY_ASSIGNED_MESSAGE + employeeDto.getUserId());
            }
            User user = userRepository.findById(employeeDto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "User not found with id " + employeeDto.getUserId()));
            employee.setUser(user);
        }
        Employee savedEmployee = employeeRepository.save(employee);
        invalidateEmployeeSearchCache();
        return employeeMapper.toDto(savedEmployee);
    }

    @Transactional
    public EmployeeDto createEmployeeWithUser(EmployeeCreateDto employeeDto, UserCreateDto userDto) {
        Employee employee = employeeMapper.toEntity(employeeDto);
        User user = userMapper.toEntity(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRoles(resolveRoles(userDto));

        user.setEmployee(null);
        userRepository.save(user);

        employee.setUser(user);
        user.setEmployee(employee);
        Employee savedEmployee = employeeRepository.save(employee);
        invalidateEmployeeSearchCache();
        return employeeMapper.toDto(savedEmployee);
    }

    private Set<Role> resolveRoles(UserCreateDto userDto) {
        Set<Role> roles = new HashSet<>();
        if (userDto.getRolesId() != null && !userDto.getRolesId().isEmpty()) {
            for (Long roleId : userDto.getRolesId()) {
                Role role = roleRepository.findById(roleId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Role not found with id " + roleId));
                roles.add(role);
            }
        } else {
            roles.add(roleService.findRoleByName("USER"));
        }
        return roles;
    }

    @Transactional
    public EmployeeDto updateEmployee(Long id, EmployeeCreateDto employeeDto) {
        Employee employee = employeeRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        EMPLOYEE_NOT_FOUND_MESS + id));

        if (employeeDto.getUserId() != null) {
            if (employeeRepository.existsByUserIdAndIdNot(employeeDto.getUserId(), id)) {
                throw new ResourceConflictException(
                        USER_ALREADY_ASSIGNED_MESSAGE + employeeDto.getUserId());
            }

            User user = userRepository
                    .findById(employeeDto.getUserId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException(
                                    "User not found with id " + employeeDto.getUserId()));
            employee.setUser(user);
        }

        employeeMapper.updateEmployeeFromDto(employeeDto, employee);
        Employee updatedEmployee = employeeRepository.save(employee);
        invalidateEmployeeSearchCache();
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

    @Transactional(readOnly = true)
    public Page<EmployeeDto> searchEmployeesWithNestedFilterJpql(
            String departmentName,
            String roleName,
            Boolean active,
            Pageable pageable) {
        EmployeeSearchCacheKey cacheKey = EmployeeSearchCacheKey.from(
                EmployeeSearchCacheKey.QueryType.JPQL,
                departmentName,
                roleName,
                active,
                pageable);
        Page<EmployeeDto> cachedPage = employeeSearchCache.get(cacheKey);
        if (cachedPage != null) {
            return cachedPage;
        }

        Page<EmployeeDto> page = employeeRepository.searchWithNestedFiltersJpql(
                departmentName, roleName, active, pageable)
            .map(employeeMapper::toDto);
        employeeSearchCache.put(cacheKey, page);
        return page;
    }

    @Transactional(readOnly = true)
    public Page<EmployeeDto> searchEmployeesWithNestedFilterNative(
            String departmentName,
            String roleName,
            Boolean active,
            Pageable pageable) {
        EmployeeSearchCacheKey cacheKey = EmployeeSearchCacheKey.from(
                EmployeeSearchCacheKey.QueryType.NATIVE,
                departmentName,
                roleName,
                active,
                pageable);
        Page<EmployeeDto> cachedPage = employeeSearchCache.get(cacheKey);
        if (cachedPage != null) {
            return cachedPage;
        }

        Page<EmployeeDto> page = employeeRepository.searchWithNestedFiltersNative(
                departmentName, roleName, active, pageable)
            .map(employeeMapper::toDto);
        employeeSearchCache.put(cacheKey, page);
        return page;
    }

    @Transactional
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException(EMPLOYEE_NOT_FOUND_MESS + id);
        }
        employeeRepository.deleteById(id);
        invalidateEmployeeSearchCache();
    }

    public Employee getEmployeeByUserId(Long id) {
        return employeeRepository.findByUserId(id);
    }

    @Transactional
    public Employee updateEmployeeWithoutDto(Employee employee) {
        Employee savedEmployee = employeeRepository.save(employee);
        invalidateEmployeeSearchCache();
        return savedEmployee;
    }

    private void invalidateEmployeeSearchCache() {
        employeeSearchCache.invalidateAll();
    }
}
