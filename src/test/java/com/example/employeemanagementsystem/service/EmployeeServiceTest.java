package com.example.employeemanagementsystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.employeemanagementsystem.cache.EmployeeSearchCache;
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

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RoleService roleService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmployeeSearchCache employeeSearchCache;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void createEmployeesBulkSuccess() {
        EmployeeCreateDto dto1 = new EmployeeCreateDto();
        EmployeeCreateDto dto2 = new EmployeeCreateDto();

        Employee entity1 = new Employee();
        Employee entity2 = new Employee();
        Employee saved1 = new Employee();
        saved1.setId(1L);
        Employee saved2 = new Employee();
        saved2.setId(2L);

        EmployeeDto out1 = new EmployeeDto();
        out1.setId(1L);
        EmployeeDto out2 = new EmployeeDto();
        out2.setId(2L);

        when(employeeMapper.toEntity(dto1)).thenReturn(entity1);
        when(employeeMapper.toEntity(dto2)).thenReturn(entity2);
        when(employeeRepository.saveAll(List.of(entity1, entity2))).thenReturn(List.of(saved1, saved2));
        when(employeeMapper.toDto(saved1)).thenReturn(out1);
        when(employeeMapper.toDto(saved2)).thenReturn(out2);

        List<EmployeeDto> result = employeeService.createEmployeesBulk(List.of(dto1, dto2));

        assertEquals(2, result.size());
        verify(employeeRepository).saveAll(List.of(entity1, entity2));
        verify(employeeSearchCache).invalidateAll();
    }

    @Test
    void createEmployeesBulkConflictOnUserThrows() {
        EmployeeCreateDto dto = new EmployeeCreateDto();
        dto.setUserId(10L);
        List<EmployeeCreateDto> employees = List.of(dto);
        User user = new User();
        user.setId(10L);
        Employee existingEmployee = new Employee();
        existingEmployee.setUser(user);

        when(employeeRepository.findAllByUserIdIn(List.of(10L))).thenReturn(List.of(existingEmployee));

        assertThrows(ResourceConflictException.class,
                () -> employeeService.createEmployeesBulk(employees));

        verify(employeeRepository, never()).saveAll(anyList());
        verify(employeeSearchCache, never()).invalidateAll();
    }

    @Test
    void createEmployeesBulkWithEmptyInputReturnsEmptyList() {
        List<EmployeeDto> result = employeeService.createEmployeesBulk(List.of());

        assertEquals(0, result.size());
        verify(employeeSearchCache, never()).invalidateAll();
    }

    @Test
    void createEmployeesBulkWithNullListReturnsEmptyList() {
        List<EmployeeDto> result = employeeService.createEmployeesBulk(null);

        assertEquals(0, result.size());
        verify(employeeSearchCache, never()).invalidateAll();
    }

    @Test
    void createEmployeesBulkWithNullPayloadThrowsIllegalArgumentException() {
        List<EmployeeCreateDto> payload = Arrays.asList((EmployeeCreateDto) null);

        assertThrows(IllegalArgumentException.class, () -> employeeService.createEmployeesBulk(payload));
        verify(employeeSearchCache, never()).invalidateAll();
    }

    @Test
    void createEmployeesBulkWithDuplicateUserIdsThrowsConflict() {
        EmployeeCreateDto dto1 = new EmployeeCreateDto();
        dto1.setUserId(55L);
        EmployeeCreateDto dto2 = new EmployeeCreateDto();
        dto2.setUserId(55L);
        List<EmployeeCreateDto> payload = List.of(dto1, dto2);

        assertThrows(ResourceConflictException.class,
                () -> employeeService.createEmployeesBulk(payload));

        verify(employeeRepository, never()).saveAll(anyList());
        verify(employeeSearchCache, never()).invalidateAll();
    }

    @Test
    void prepareEmployeeForCreateWithNonEmptyUsersMapAndMissingUserThrowsNotFound() {
        EmployeeCreateDto dto = new EmployeeCreateDto();
        dto.setUserId(99L);
        Employee entity = new Employee();
        User anotherUser = new User();
        anotherUser.setId(100L);
        Map<Long, User> usersById = Map.of(100L, anotherUser);

        when(employeeMapper.toEntity(dto)).thenReturn(entity);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> invokePrepareEmployeeForCreate(dto, usersById));
        assertEquals("User not found with id 99", exception.getMessage());
    }

    @Test
    void createEmployeeWithUserAlreadyAssignedThrowsConflict() {
        EmployeeCreateDto dto = new EmployeeCreateDto();
        dto.setUserId(20L);
        Employee entity = new Employee();

        when(employeeMapper.toEntity(dto)).thenReturn(entity);
        when(employeeRepository.existsByUserId(20L)).thenReturn(true);

        assertThrows(ResourceConflictException.class, () -> employeeService.createEmployee(dto));
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void createEmployeeWithMissingUserThrowsNotFound() {
        EmployeeCreateDto dto = new EmployeeCreateDto();
        dto.setUserId(30L);
        Employee entity = new Employee();

        when(employeeMapper.toEntity(dto)).thenReturn(entity);
        when(employeeRepository.existsByUserId(30L)).thenReturn(false);
        when(userRepository.findById(30L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.createEmployee(dto));
    }

    @Test
    void createEmployeeWithValidUserSavesAndMaps() {
        EmployeeCreateDto dto = new EmployeeCreateDto();
        dto.setUserId(40L);

        Employee entity = new Employee();
        User user = new User();
        Employee savedEmployee = new Employee();
        EmployeeDto out = new EmployeeDto();

        when(employeeMapper.toEntity(dto)).thenReturn(entity);
        when(employeeRepository.existsByUserId(40L)).thenReturn(false);
        when(userRepository.findById(40L)).thenReturn(Optional.of(user));
        when(employeeRepository.save(entity)).thenReturn(savedEmployee);
        when(employeeMapper.toDto(savedEmployee)).thenReturn(out);

        EmployeeDto result = employeeService.createEmployee(dto);

        assertEquals(out, result);
        assertSame(user, entity.getUser());
        verify(employeeSearchCache).invalidateAll();
    }

    @Test
    void createEmployeeWithoutUserSavesDirectly() {
        EmployeeCreateDto dto = new EmployeeCreateDto();
        Employee entity = new Employee();
        Employee savedEmployee = new Employee();
        EmployeeDto out = new EmployeeDto();

        when(employeeMapper.toEntity(dto)).thenReturn(entity);
        when(employeeRepository.save(entity)).thenReturn(savedEmployee);
        when(employeeMapper.toDto(savedEmployee)).thenReturn(out);

        EmployeeDto result = employeeService.createEmployee(dto);

        assertEquals(out, result);
        verify(employeeSearchCache).invalidateAll();
        verify(employeeRepository, never()).existsByUserId(any(Long.class));
    }

    @Test
    void createEmployeeWithUserUsesDefaultRoleWhenRolesAreEmpty() {
        EmployeeCreateDto employeeCreateDto = new EmployeeCreateDto();
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setPassword("pass");
        userCreateDto.setRolesId(Set.of());

        Employee employee = new Employee();
        User user = new User();
        Role defaultRole = new Role();
        Employee savedEmployee = new Employee();
        EmployeeDto employeeDto = new EmployeeDto();

        when(employeeMapper.toEntity(employeeCreateDto)).thenReturn(employee);
        when(userMapper.toEntity(userCreateDto)).thenReturn(user);
        when(passwordEncoder.encode("pass")).thenReturn("encoded-pass");
        when(roleService.findRoleByName("USER")).thenReturn(defaultRole);
        when(employeeRepository.save(employee)).thenReturn(savedEmployee);
        when(employeeMapper.toDto(savedEmployee)).thenReturn(employeeDto);

        EmployeeDto result = employeeService.createEmployeeWithUser(employeeCreateDto, userCreateDto);

        assertEquals(employeeDto, result);
        assertEquals(1, user.getRoles().size());
        assertSame(user, employee.getUser());
        verify(userRepository).save(user);
        verify(employeeSearchCache).invalidateAll();
    }

    @Test
    void createEmployeeWithUserUsesDefaultRoleWhenRolesAreNull() {
        EmployeeCreateDto employeeCreateDto = new EmployeeCreateDto();
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setPassword("pass");
        userCreateDto.setRolesId(null);

        Employee employee = new Employee();
        User user = new User();
        Role defaultRole = new Role();
        Employee savedEmployee = new Employee();
        EmployeeDto employeeDto = new EmployeeDto();

        when(employeeMapper.toEntity(employeeCreateDto)).thenReturn(employee);
        when(userMapper.toEntity(userCreateDto)).thenReturn(user);
        when(passwordEncoder.encode("pass")).thenReturn("encoded-pass");
        when(roleService.findRoleByName("USER")).thenReturn(defaultRole);
        when(employeeRepository.save(employee)).thenReturn(savedEmployee);
        when(employeeMapper.toDto(savedEmployee)).thenReturn(employeeDto);

        EmployeeDto result = employeeService.createEmployeeWithUser(employeeCreateDto, userCreateDto);

        assertEquals(employeeDto, result);
        assertEquals(1, user.getRoles().size());
    }

    @Test
    void createEmployeeWithUserResolvesExplicitRoles() {
        EmployeeCreateDto employeeCreateDto = new EmployeeCreateDto();
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setPassword("pass");
        userCreateDto.setRolesId(Set.of(10L));

        Employee employee = new Employee();
        User user = new User();
        Role role = new Role();
        Employee savedEmployee = new Employee();
        EmployeeDto employeeDto = new EmployeeDto();

        when(employeeMapper.toEntity(employeeCreateDto)).thenReturn(employee);
        when(userMapper.toEntity(userCreateDto)).thenReturn(user);
        when(passwordEncoder.encode("pass")).thenReturn("encoded-pass");
        when(roleRepository.findById(10L)).thenReturn(Optional.of(role));
        when(employeeRepository.save(employee)).thenReturn(savedEmployee);
        when(employeeMapper.toDto(savedEmployee)).thenReturn(employeeDto);

        EmployeeDto result = employeeService.createEmployeeWithUser(employeeCreateDto, userCreateDto);

        assertEquals(employeeDto, result);
        assertEquals(1, user.getRoles().size());
    }

    @Test
    void createEmployeeWithUserThrowsWhenRoleNotFound() {
        EmployeeCreateDto employeeCreateDto = new EmployeeCreateDto();
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setPassword("pass");
        userCreateDto.setRolesId(Set.of(11L));

        when(employeeMapper.toEntity(employeeCreateDto)).thenReturn(new Employee());
        when(userMapper.toEntity(userCreateDto)).thenReturn(new User());
        when(passwordEncoder.encode("pass")).thenReturn("encoded-pass");
        when(roleRepository.findById(11L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> employeeService.createEmployeeWithUser(employeeCreateDto, userCreateDto));
    }

    @Test
    void updateEmployeeNotFoundThrows() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());
        EmployeeCreateDto createDto = new EmployeeCreateDto();

        assertThrows(ResourceNotFoundException.class,
                () -> employeeService.updateEmployee(99L, createDto));
    }

    @Test
    void updateEmployeeWithAssignedUserConflictThrows() {
        EmployeeCreateDto dto = new EmployeeCreateDto();
        dto.setUserId(3L);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(new Employee()));
        when(employeeRepository.existsByUserIdAndIdNot(3L, 1L)).thenReturn(true);

        assertThrows(ResourceConflictException.class, () -> employeeService.updateEmployee(1L, dto));
    }

    @Test
    void updateEmployeeWithUserNotFoundThrows() {
        EmployeeCreateDto dto = new EmployeeCreateDto();
        dto.setUserId(31L);

        when(employeeRepository.findById(15L)).thenReturn(Optional.of(new Employee()));
        when(employeeRepository.existsByUserIdAndIdNot(31L, 15L)).thenReturn(false);
        when(userRepository.findById(31L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.updateEmployee(15L, dto));
    }

    @Test
    void updateEmployeeWithUserSuccessPersists() {
        EmployeeCreateDto dto = new EmployeeCreateDto();
        dto.setUserId(32L);

        Employee existingEmployee = new Employee();
        Employee savedEmployee = new Employee();
        EmployeeDto employeeDto = new EmployeeDto();
        User user = new User();
        user.setId(200L);
        user.setId(200L);

        when(employeeRepository.findById(16L)).thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.existsByUserIdAndIdNot(32L, 16L)).thenReturn(false);
        when(userRepository.findById(32L)).thenReturn(Optional.of(user));
        when(employeeRepository.save(existingEmployee)).thenReturn(savedEmployee);
        when(employeeMapper.toDto(savedEmployee)).thenReturn(employeeDto);

        EmployeeDto result = employeeService.updateEmployee(16L, dto);

        assertEquals(employeeDto, result);
        assertSame(user, existingEmployee.getUser());
    }

    @Test
    void updateEmployeeSuccessPersistsAndMapsResult() {
        EmployeeCreateDto dto = new EmployeeCreateDto();
        Employee existingEmployee = new Employee();
        Employee savedEmployee = new Employee();
        EmployeeDto out = new EmployeeDto();

        when(employeeRepository.findById(11L)).thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.save(existingEmployee)).thenReturn(savedEmployee);
        when(employeeMapper.toDto(savedEmployee)).thenReturn(out);

        EmployeeDto result = employeeService.updateEmployee(11L, dto);

        assertEquals(out, result);
        verify(employeeMapper).updateEmployeeFromDto(dto, existingEmployee);
        verify(employeeSearchCache).invalidateAll();
    }

    @Test
    void patchEmployeeWithAssignedUserConflictThrows() {
        EmployeePatchDto patchDto = new EmployeePatchDto();
        patchDto.setUserId(8L);
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(new Employee()));
        when(employeeRepository.existsByUserIdAndIdNot(8L, 2L)).thenReturn(true);

        assertThrows(ResourceConflictException.class, () -> employeeService.patchEmployee(2L, patchDto));
    }

    @Test
    void patchEmployeeNotFoundThrows() {
        when(employeeRepository.findById(222L)).thenReturn(Optional.empty());
        EmployeePatchDto patchDto = new EmployeePatchDto();

        assertThrows(ResourceNotFoundException.class,
                () -> employeeService.patchEmployee(222L, patchDto));
    }

    @Test
    void patchEmployeeSuccessPersistsAndMapsResult() {
        EmployeePatchDto patchDto = new EmployeePatchDto();
        Employee existingEmployee = new Employee();
        Employee savedEmployee = new Employee();
        EmployeeDto out = new EmployeeDto();

        when(employeeRepository.findById(12L)).thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.save(existingEmployee)).thenReturn(savedEmployee);
        when(employeeMapper.toDto(savedEmployee)).thenReturn(out);

        EmployeeDto result = employeeService.patchEmployee(12L, patchDto);

        assertEquals(out, result);
        verify(employeeMapper).updateEmployeeFromPatchDto(patchDto, existingEmployee);
        verify(employeeSearchCache).invalidateAll();
    }

    @Test
    void patchEmployeeWithUserWithoutConflictSucceeds() {
        EmployeePatchDto patchDto = new EmployeePatchDto();
        patchDto.setUserId(77L);
        Employee existingEmployee = new Employee();
        Employee savedEmployee = new Employee();
        EmployeeDto out = new EmployeeDto();

        when(employeeRepository.findById(17L)).thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.existsByUserIdAndIdNot(77L, 17L)).thenReturn(false);
        when(employeeRepository.save(existingEmployee)).thenReturn(savedEmployee);
        when(employeeMapper.toDto(savedEmployee)).thenReturn(out);

        EmployeeDto result = employeeService.patchEmployee(17L, patchDto);

        assertEquals(out, result);
        verify(employeeMapper).updateEmployeeFromPatchDto(patchDto, existingEmployee);
    }

    @Test
    void getEmployeeDtoByIdNotFoundThrows() {
        when(employeeRepository.findById(200L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.getEmployeeDtoById(200L));
    }

    @Test
    void getEmployeeByIdReturnsOptionalFromRepository() {
        Employee employee = new Employee();
        when(employeeRepository.findById(201L)).thenReturn(Optional.of(employee));

        Optional<Employee> result = employeeService.getEmployeeById(201L);

        assertEquals(Optional.of(employee), result);
    }

    @Test
    void getEmployeeDtoByIdSuccessMapsEntity() {
        Employee employee = new Employee();
        EmployeeDto employeeDto = new EmployeeDto();
        when(employeeRepository.findById(202L)).thenReturn(Optional.of(employee));
        when(employeeMapper.toDto(employee)).thenReturn(employeeDto);

        EmployeeDto result = employeeService.getEmployeeDtoById(202L);

        assertEquals(employeeDto, result);
    }

    @Test
    void getAllEmployeesMapsAllEntities() {
        Employee employee = new Employee();
        EmployeeDto employeeDto = new EmployeeDto();
        when(employeeRepository.findAll()).thenReturn(List.of(employee));
        when(employeeMapper.toDto(employee)).thenReturn(employeeDto);

        List<EmployeeDto> result = employeeService.getAllEmployees();

        assertEquals(1, result.size());
    }

    @Test
    void getEmployeesBySalaryRangeWithoutLimitsUsesFindAll() {
        when(employeeRepository.findAll()).thenReturn(List.of(new Employee()));
        when(employeeMapper.toDto(anyList())).thenReturn(List.of(new EmployeeDto()));

        List<EmployeeDto> result = employeeService.getEmployeesBySalaryRange(null, null);

        assertEquals(1, result.size());
        verify(employeeRepository).findAll();
    }

    @Test
    void getEmployeesBySalaryRangeWithOnlyMinUsesGreaterThanEqualQuery() {
        BigDecimal min = BigDecimal.valueOf(1000);
        when(employeeRepository.findBySalaryGreaterThanEqual(min)).thenReturn(List.of(new Employee()));
        when(employeeMapper.toDto(anyList())).thenReturn(List.of(new EmployeeDto()));

        List<EmployeeDto> result = employeeService.getEmployeesBySalaryRange(min, null);

        assertEquals(1, result.size());
        verify(employeeRepository).findBySalaryGreaterThanEqual(min);
    }

    @Test
    void getEmployeesBySalaryRangeWithOnlyMaxUsesLessThanEqualQuery() {
        BigDecimal max = BigDecimal.valueOf(3000);
        when(employeeRepository.findBySalaryLessThanEqual(max)).thenReturn(List.of(new Employee()));
        when(employeeMapper.toDto(anyList())).thenReturn(List.of(new EmployeeDto()));

        List<EmployeeDto> result = employeeService.getEmployeesBySalaryRange(null, max);

        assertEquals(1, result.size());
        verify(employeeRepository).findBySalaryLessThanEqual(max);
    }

    @Test
    void getEmployeesBySalaryRangeWithBothBoundsUsesBetweenQuery() {
        BigDecimal min = BigDecimal.valueOf(1000);
        BigDecimal max = BigDecimal.valueOf(5000);
        when(employeeRepository.findBySalaryBetween(min, max)).thenReturn(List.of(new Employee()));
        when(employeeMapper.toDto(anyList())).thenReturn(List.of(new EmployeeDto()));

        List<EmployeeDto> result = employeeService.getEmployeesBySalaryRange(min, max);

        assertEquals(1, result.size());
        verify(employeeRepository).findBySalaryBetween(min, max);
    }

    @Test
    void getEmployeesByDepartmentIdMapsResults() {
        Employee employee = new Employee();
        EmployeeDto employeeDto = new EmployeeDto();
        when(employeeRepository.findByDepartmentId(3L)).thenReturn(List.of(employee));
        when(employeeMapper.toDto(employee)).thenReturn(employeeDto);

        List<EmployeeDto> result = employeeService.getEmployeesByDepartmentId(3L);

        assertEquals(1, result.size());
    }

    @Test
    void getEmployeesByPositionIdMapsResults() {
        Employee employee = new Employee();
        EmployeeDto employeeDto = new EmployeeDto();
        when(employeeRepository.findByPositionId(4L)).thenReturn(List.of(employee));
        when(employeeMapper.toDto(employee)).thenReturn(employeeDto);

        List<EmployeeDto> result = employeeService.getEmployeesByPositionId(4L);

        assertEquals(1, result.size());
    }

    @Test
    void searchEmployeesWithNestedFilterJpqlReturnsCachedPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<EmployeeDto> cached = new PageImpl<>(List.of(new EmployeeDto()));
        when(employeeSearchCache.get(any())).thenReturn(cached);

        Page<EmployeeDto> result =
                employeeService.searchEmployeesWithNestedFilterJpql("IT", "ADMIN", true, pageable);

        assertEquals(1, result.getContent().size());
        verify(employeeRepository, never()).searchWithNestedFiltersJpql(any(), any(), any(), any());
    }

    @Test
    void searchEmployeesWithNestedFilterJpqlCacheMissReadsRepositoryAndCachesResult() {
        Pageable pageable = PageRequest.of(0, 10);
        Employee employee = new Employee();
        EmployeeDto employeeDto = new EmployeeDto();
        Page<Employee> page = new PageImpl<>(List.of(employee));

        when(employeeSearchCache.get(any())).thenReturn(null);
        when(employeeRepository.searchWithNestedFiltersJpql("it", "user", true, pageable))
                .thenReturn(page);
        when(employeeMapper.toDto(employee)).thenReturn(employeeDto);

        Page<EmployeeDto> result =
                employeeService.searchEmployeesWithNestedFilterJpql("IT", "USER", true, pageable);

        assertEquals(1, result.getContent().size());
        verify(employeeSearchCache).put(any(), any());
    }

    @Test
    void searchEmployeesWithAllFiltersJpqlNormalizesAndMaps() {
        Pageable pageable = PageRequest.of(1, 5);
        Employee employee = new Employee();
        EmployeeDto employeeDto = new EmployeeDto();
        Page<Employee> page = new PageImpl<>(List.of(employee));

        when(employeeRepository.searchWithAllFiltersJpql(
                "alice",
                "",
                "manager",
                true,
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(5000),
                pageable)).thenReturn(page);
        when(employeeMapper.toDto(employee)).thenReturn(employeeDto);

        Page<EmployeeDto> result = employeeService.searchEmployeesWithAllFiltersJpql(
                "  ALICE ",
                "   ",
                " MANAGER ",
                true,
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(5000),
                pageable);

        assertEquals(1, result.getContent().size());
    }

    @Test
    void searchEmployeesWithAllFiltersJpqlWithNullFiltersUsesEmptyValues() {
        Pageable pageable = PageRequest.of(0, 5);
        when(employeeRepository.searchWithAllFiltersJpql(
                "",
                "",
                "",
                null,
                null,
                null,
                pageable)).thenReturn(Page.empty(pageable));

        Page<EmployeeDto> result = employeeService.searchEmployeesWithAllFiltersJpql(
                null, null, null, null, null, null, pageable);

        assertEquals(0, result.getTotalElements());
    }

    @Test
    void unlinkUserFromEmployeeNotFoundThrows() {
        when(employeeRepository.findById(901L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> employeeService.unlinkUserFromEmployee(901L));
    }

    @Test
    void unlinkUserFromEmployeeUnlinksBothSidesWhenBackReferenceMatches() {
        Employee employee = new Employee();
        employee.setId(902L);
        User linkedUser = new User();
        Employee backReference = new Employee();
        backReference.setId(902L);
        linkedUser.setEmployee(backReference);
        employee.setUser(linkedUser);
        EmployeeDto employeeDto = new EmployeeDto();

        when(employeeRepository.findById(902L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(employee)).thenReturn(employee);
        when(employeeMapper.toDto(employee)).thenReturn(employeeDto);

        EmployeeDto result = employeeService.unlinkUserFromEmployee(902L);

        assertSame(employeeDto, result);
        assertNull(employee.getUser());
        assertNull(linkedUser.getEmployee());
        verify(employeeSearchCache).invalidateAll();
    }

    @Test
    void unlinkUserFromEmployeeKeepsBackReferenceWhenDifferentEmployee() {
        Employee employee = new Employee();
        employee.setId(903L);
        User linkedUser = new User();
        Employee backReference = new Employee();
        backReference.setId(904L);
        linkedUser.setEmployee(backReference);
        employee.setUser(linkedUser);
        EmployeeDto employeeDto = new EmployeeDto();

        when(employeeRepository.findById(903L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(employee)).thenReturn(employee);
        when(employeeMapper.toDto(employee)).thenReturn(employeeDto);

        employeeService.unlinkUserFromEmployee(903L);

        assertNull(employee.getUser());
        assertSame(backReference, linkedUser.getEmployee());
    }

    @Test
    void unlinkUserFromEmployeeWithNullBackReferenceKeepsNullAndSaves() {
        Employee employee = new Employee();
        employee.setId(906L);
        User linkedUser = new User();
        linkedUser.setEmployee(null);
        employee.setUser(linkedUser);
        EmployeeDto employeeDto = new EmployeeDto();

        when(employeeRepository.findById(906L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(employee)).thenReturn(employee);
        when(employeeMapper.toDto(employee)).thenReturn(employeeDto);

        EmployeeDto result = employeeService.unlinkUserFromEmployee(906L);

        assertSame(employeeDto, result);
        assertNull(employee.getUser());
        assertNull(linkedUser.getEmployee());
    }

    @Test
    void unlinkUserFromEmployeeWithNoLinkedUserStillSavesEmployee() {
        Employee employee = new Employee();
        employee.setId(905L);
        employee.setUser(null);
        EmployeeDto employeeDto = new EmployeeDto();
        when(employeeRepository.findById(905L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(employee)).thenReturn(employee);
        when(employeeMapper.toDto(employee)).thenReturn(employeeDto);

        EmployeeDto result = employeeService.unlinkUserFromEmployee(905L);

        assertSame(employeeDto, result);
        verify(employeeRepository).save(employee);
    }

    @Test
    void createEmployeesBulkWithUserPresentAndFoundSavesEmployee() {
        EmployeeCreateDto dto = new EmployeeCreateDto();
        dto.setUserId(200L);
        Employee employee = new Employee();
        Employee savedEmployee = new Employee();
        EmployeeDto employeeDto = new EmployeeDto();
        User user = new User();
        user.setId(200L);

        when(employeeRepository.findAllByUserIdIn(List.of(200L))).thenReturn(List.of());
        when(userRepository.findAllById(List.of(200L))).thenReturn(List.of(user));
        when(employeeMapper.toEntity(dto)).thenReturn(employee);
        when(employeeRepository.saveAll(List.of(employee))).thenReturn(List.of(savedEmployee));
        when(employeeMapper.toDto(savedEmployee)).thenReturn(employeeDto);

        List<EmployeeDto> result = employeeService.createEmployeesBulk(List.of(dto));

        assertEquals(1, result.size());
        assertSame(user, employee.getUser());
        verify(employeeSearchCache).invalidateAll();
    }

    @Test
    void createEmployeesBulkWithUserNotFoundThrows() {
        EmployeeCreateDto dto = new EmployeeCreateDto();
        dto.setUserId(201L);
        List<EmployeeCreateDto> employeeDtos = List.of(dto);

        when(employeeRepository.findAllByUserIdIn(List.of(201L))).thenReturn(List.of());
        when(userRepository.findAllById(List.of(201L))).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class,
                () -> employeeService.createEmployeesBulk(employeeDtos));
        verify(employeeSearchCache, never()).invalidateAll();
    }

    @Test
    void deleteEmployeeNotFoundThrows() {
        when(employeeRepository.existsById(71L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> employeeService.deleteEmployee(71L));
    }

    @Test
    void deleteEmployeeSuccessDeletesAndInvalidatesCache() {
        when(employeeRepository.existsById(72L)).thenReturn(true);

        employeeService.deleteEmployee(72L);

        verify(employeeRepository).deleteById(72L);
        verify(employeeSearchCache).invalidateAll();
    }

    @Test
    void updateEmployeeWithoutDtoSavesAndInvalidatesCache() {
        Employee employee = new Employee();
        Employee savedEmployee = new Employee();
        when(employeeRepository.save(employee)).thenReturn(savedEmployee);

        Employee result = employeeService.updateEmployeeWithoutDto(employee);

        assertSame(savedEmployee, result);
        verify(employeeSearchCache).invalidateAll();
    }

    @Test
    void getEmployeeByUserIdReturnsRepositoryValue() {
        Employee employee = new Employee();
        when(employeeRepository.findByUserId(500L)).thenReturn(employee);

        Employee result = employeeService.getEmployeeByUserId(500L);

        assertSame(employee, result);
    }

    @Test
    void prepareEmployeeForCreateWithNullDtoThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> invokePrepareEmployeeForCreate(null, Map.of()));
    }

    @Test
    void prepareEmployeeForCreateWithEmptyUsersMapAndAlreadyAssignedUserThrowsConflict() {
        EmployeeCreateDto dto = new EmployeeCreateDto();
        dto.setUserId(320L);
        when(employeeMapper.toEntity(dto)).thenReturn(new Employee());
        when(employeeRepository.existsByUserId(320L)).thenReturn(true);

        assertThrows(ResourceConflictException.class,
                () -> invokePrepareEmployeeForCreate(dto, Map.of()));
    }

    @Test
    void prepareEmployeeForCreateWithEmptyUsersMapAndMissingUserThrowsNotFound() {
        EmployeeCreateDto dto = new EmployeeCreateDto();
        dto.setUserId(321L);
        when(employeeMapper.toEntity(dto)).thenReturn(new Employee());
        when(employeeRepository.existsByUserId(321L)).thenReturn(false);
        when(userRepository.findById(321L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> invokePrepareEmployeeForCreate(dto, Map.of()));
    }

    @Test
    void prepareEmployeeForCreateWithEmptyUsersMapAndExistingUserSetsUser() {
        EmployeeCreateDto dto = new EmployeeCreateDto();
        dto.setUserId(322L);
        Employee employee = new Employee();
        User user = new User();
        user.setId(322L);
        when(employeeMapper.toEntity(dto)).thenReturn(employee);
        when(employeeRepository.existsByUserId(322L)).thenReturn(false);
        when(userRepository.findById(322L)).thenReturn(Optional.of(user));

        Employee result = invokePrepareEmployeeForCreate(dto, Map.of());

        assertSame(employee, result);
        assertSame(user, result.getUser());
    }

    private Employee invokePrepareEmployeeForCreate(EmployeeCreateDto dto, Map<Long, User> usersById) {
        try {
            Method method = EmployeeService.class.getDeclaredMethod(
                    "prepareEmployeeForCreate", EmployeeCreateDto.class, Map.class);
            method.setAccessible(true);
            return (Employee) method.invoke(employeeService, dto, usersById);
        } catch (InvocationTargetException exception) {
            Throwable cause = exception.getCause();
            if (cause instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new RuntimeException(cause);
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException(exception);
        }
    }
}
