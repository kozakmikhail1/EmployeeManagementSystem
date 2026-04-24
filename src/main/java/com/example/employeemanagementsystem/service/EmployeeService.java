package com.example.employeemanagementsystem.service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.employeemanagementsystem.cache.CacheNames;
import com.example.employeemanagementsystem.cache.EmployeeSearchCache;
import com.example.employeemanagementsystem.cache.EmployeeSearchCacheKey;
import com.example.employeemanagementsystem.cache.InvalidateReadCaches;
import com.example.employeemanagementsystem.dto.create.EmployeeCreateDto;
import com.example.employeemanagementsystem.dto.create.UserCreateDto;
import com.example.employeemanagementsystem.dto.get.EmployeeDto;
import com.example.employeemanagementsystem.dto.patch.EmployeePatchDto;
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
    private static final String USER_NOT_FOUND_MESSAGE = "User not found with id ";

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
    @InvalidateReadCaches
    public EmployeeDto createEmployee(EmployeeCreateDto employeeDto) {
        Employee employee = employeeMapper.toEntity(employeeDto);

        if (employeeDto.getUserId() != null) {
            if (employeeRepository.existsByUserId(employeeDto.getUserId())) {
                throw new ResourceConflictException(
                        USER_ALREADY_ASSIGNED_MESSAGE + employeeDto.getUserId());
            }
            User user = userRepository.findById(employeeDto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            USER_NOT_FOUND_MESSAGE + employeeDto.getUserId()));
            employee.setUser(user);
        }
        Employee savedEmployee = employeeRepository.save(employee);
        invalidateEmployeeSearchCache();
        return employeeMapper.toDto(savedEmployee);
    }

    @Transactional
    @InvalidateReadCaches
    public List<EmployeeDto> createEmployeesBulk(List<EmployeeCreateDto> employeeDtos) {
        List<Employee> employees = prepareEmployeesForBulk(employeeDtos);
        if (employees.isEmpty()) {
            return List.of();
        }

        List<EmployeeDto> savedEmployees = employeeRepository.saveAll(employees).stream()
                .map(employeeMapper::toDto)
                .toList();
        invalidateEmployeeSearchCache();
        return savedEmployees;
    }

    @Transactional
    @InvalidateReadCaches
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
    @InvalidateReadCaches
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
                                    USER_NOT_FOUND_MESSAGE + employeeDto.getUserId()));
            employee.setUser(user);
        }

        employeeMapper.updateEmployeeFromDto(employeeDto, employee);
        Employee updatedEmployee = employeeRepository.save(employee);
        invalidateEmployeeSearchCache();
        return employeeMapper.toDto(updatedEmployee);
    }

    @Transactional
    @InvalidateReadCaches
    public EmployeeDto patchEmployee(Long id, EmployeePatchDto employeeDto) {
        Employee employee = employeeRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        EMPLOYEE_NOT_FOUND_MESS + id));

        if (employeeDto.getUserId() != null
                && employeeRepository.existsByUserIdAndIdNot(employeeDto.getUserId(), id)) {
            throw new ResourceConflictException(
                    USER_ALREADY_ASSIGNED_MESSAGE + employeeDto.getUserId());
        }

        employeeMapper.updateEmployeeFromPatchDto(employeeDto, employee);
        Employee updatedEmployee = employeeRepository.save(employee);
        invalidateEmployeeSearchCache();
        return employeeMapper.toDto(updatedEmployee);
    }

    @Transactional
    @InvalidateReadCaches
    public EmployeeDto unlinkUserFromEmployee(Long id) {
        Employee employee = employeeRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        EMPLOYEE_NOT_FOUND_MESS + id));

        User linkedUser = employee.getUser();
        if (linkedUser != null) {
            employee.setUser(null);
            if (linkedUser.getEmployee() != null
                    && Objects.equals(linkedUser.getEmployee().getId(), employee.getId())) {
                linkedUser.setEmployee(null);
            }
        }

        Employee updatedEmployee = employeeRepository.save(employee);
        invalidateEmployeeSearchCache();
        return employeeMapper.toDto(updatedEmployee);
    }

    @Transactional(readOnly = true)
    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.EMPLOYEE_BY_ID, key = "#id")
    public EmployeeDto getEmployeeDtoById(Long id) {
        return employeeMapper.toDto(employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        EMPLOYEE_NOT_FOUND_MESS + id)));
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.EMPLOYEES_ALL)
    public List<EmployeeDto> getAllEmployees() {
        return employeeRepository.findAll().stream().map(employeeMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.EMPLOYEES_BY_SALARY_RANGE)
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
    @Cacheable(cacheNames = CacheNames.EMPLOYEES_BY_DEPARTMENT, key = "#departmentId")
    public List<EmployeeDto> getEmployeesByDepartmentId(Long departmentId) {
        List<Employee> employees = employeeRepository.findByDepartmentId(departmentId);
        return employees.stream().map(employeeMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.EMPLOYEES_BY_POSITION, key = "#positionId")
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
        String normalizedDepartmentName = normalizeFilterValueForQuery(departmentName);
        String normalizedRoleName = normalizeFilterValueForQuery(roleName);
        EmployeeSearchCacheKey cacheKey = EmployeeSearchCacheKey.from(
                EmployeeSearchCacheKey.QueryType.JPQL,
                normalizedDepartmentName,
                normalizedRoleName,
                active,
                pageable);
        Page<EmployeeDto> cachedPage = employeeSearchCache.get(cacheKey);
        if (cachedPage != null) {
            return cachedPage;
        }

        Page<EmployeeDto> page = employeeRepository.searchWithNestedFiltersJpql(
                normalizedDepartmentName, normalizedRoleName, active, pageable)
            .map(employeeMapper::toDto);
        employeeSearchCache.put(cacheKey, page);
        return page;
    }

    @Transactional(readOnly = true)
    public Page<EmployeeDto> searchEmployeesWithAllFiltersJpql(
            String q,
            String departmentName,
            String roleName,
            Boolean active,
            BigDecimal minSalary,
            BigDecimal maxSalary,
            Pageable pageable) {
        String normalizedQuery = normalizeFilterValueForQuery(q);
        String normalizedDepartmentName = normalizeFilterValueForQuery(departmentName);
        String normalizedRoleName = normalizeFilterValueForQuery(roleName);

        return employeeRepository.searchWithAllFiltersJpql(
                normalizedQuery,
                normalizedDepartmentName,
                normalizedRoleName,
                active,
                minSalary,
                maxSalary,
                pageable).map(employeeMapper::toDto);
    }

    @Transactional
    @InvalidateReadCaches
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
    @InvalidateReadCaches
    public Employee updateEmployeeWithoutDto(Employee employee) {
        Employee savedEmployee = employeeRepository.save(employee);
        invalidateEmployeeSearchCache();
        return savedEmployee;
    }

    private void invalidateEmployeeSearchCache() {
        employeeSearchCache.invalidateAll();
    }

    private String normalizeFilterValueForQuery(String value) {
        if (value == null) {
            return "";
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return "";
        }
        return trimmed.toLowerCase();
    }

    private List<Employee> prepareEmployeesForBulk(List<EmployeeCreateDto> employeeDtos) {
        if (employeeDtos == null || employeeDtos.isEmpty()) {
            return List.of();
        }

        if (employeeDtos.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Employee payload cannot be null");
        }

        List<Long> userIds = employeeDtos.stream()
                .map(EmployeeCreateDto::getUserId)
                .filter(Objects::nonNull)
                .toList();

        validateDuplicateUserIds(userIds);
        validateUsersAvailableForBulk(userIds);

        Map<Long, User> usersById = loadUsersById(userIds);

        return employeeDtos.stream()
                .map(employeeDto -> prepareEmployeeForCreate(employeeDto, usersById))
                .toList();
    }

    private Employee prepareEmployeeForCreate(
            EmployeeCreateDto employeeDto,
            Map<Long, User> usersById) {
        if (employeeDto == null) {
            throw new IllegalArgumentException("Employee payload cannot be null");
        }

        Employee employee = employeeMapper.toEntity(employeeDto);
        Optional.ofNullable(employeeDto.getUserId())
                .ifPresent(userId -> {
                    User user = resolveUserForCreate(userId, usersById);
                    employee.setUser(user);
                });
        return employee;
    }

    private User resolveUserForCreate(Long userId, Map<Long, User> usersById) {
        if (!usersById.isEmpty()) {
            return Optional.ofNullable(usersById.get(userId))
                    .orElseThrow(() -> new ResourceNotFoundException(
                            USER_NOT_FOUND_MESSAGE + userId));
        }

        if (employeeRepository.existsByUserId(userId)) {
            throw new ResourceConflictException(USER_ALREADY_ASSIGNED_MESSAGE + userId);
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        USER_NOT_FOUND_MESSAGE + userId));
    }

    private void validateDuplicateUserIds(List<Long> userIds) {
        userIds.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() > 1)
                .findFirst()
                .ifPresent(entry -> {
                    throw new ResourceConflictException(
                            "Bulk payload contains duplicate user id " + entry.getKey());
                });
    }

    private void validateUsersAvailableForBulk(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return;
        }

        employeeRepository.findAllByUserIdIn(userIds).stream()
                .map(Employee::getUser)
                .filter(Objects::nonNull)
                .map(User::getId)
                .findFirst()
                .ifPresent(userId -> {
                    throw new ResourceConflictException(
                            USER_ALREADY_ASSIGNED_MESSAGE + userId);
                });
    }

    private Map<Long, User> loadUsersById(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Long, User> usersById = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        userIds.stream()
                .filter(userId -> !usersById.containsKey(userId))
                .findFirst()
                .ifPresent(userId -> {
                    throw new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE + userId);
                });

        return usersById;
    }
}
