package com.example.employeemanagementsystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.employeemanagementsystem.cache.EmployeeSearchCache;
import com.example.employeemanagementsystem.dto.create.UserCreateDto;
import com.example.employeemanagementsystem.dto.get.UserDto;
import com.example.employeemanagementsystem.mapper.UserMapper;
import com.example.employeemanagementsystem.model.Employee;
import com.example.employeemanagementsystem.model.Role;
import com.example.employeemanagementsystem.model.User;
import com.example.employeemanagementsystem.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleService roleService;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private EmployeeSearchCache employeeSearchCache;

    @InjectMocks
    private UserService userService;

    @Test
    void createUserWithoutRolesUsesDefaultRoleAndEncodesPassword() {
        UserCreateDto createDto = new UserCreateDto();
        createDto.setUsername("john");
        createDto.setPassword("password123");

        User user = new User();
        User saved = new User();
        saved.setId(1L);

        Role defaultRole = new Role();
        defaultRole.setId(7L);
        defaultRole.setName("USER");

        UserDto out = new UserDto();
        out.setId(1L);
        out.setUsername("john");

        when(userMapper.toEntity(createDto)).thenReturn(user);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(roleService.findRoleByName("USER")).thenReturn(defaultRole);
        when(userRepository.save(user)).thenReturn(saved);
        when(userMapper.toDto(saved)).thenReturn(out);

        UserDto result = userService.createUser(createDto);

        assertEquals(1L, result.getId());
        assertEquals("encoded-password", user.getPassword());
        assertEquals(1, user.getRoles().size());
        verify(employeeSearchCache).invalidateAll();
    }

    @Test
    void deleteUserWithLinkedEmployeeUnlinksAndDeletes() {
        User user = new User();
        user.setId(11L);
        when(userRepository.findById(11L)).thenReturn(Optional.of(user));

        Employee employee = new Employee();
        User employeeUser = new User();
        employeeUser.setId(11L);
        employee.setUser(employeeUser);

        when(employeeService.getEmployeeByUserId(11L)).thenReturn(employee);

        userService.deleteUser(11L);

        assertNull(employee.getUser());
        verify(userRepository).deleteById(11L);
        verify(employeeSearchCache).invalidateAll();
    }
}
