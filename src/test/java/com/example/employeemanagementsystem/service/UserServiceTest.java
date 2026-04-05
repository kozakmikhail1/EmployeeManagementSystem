package com.example.employeemanagementsystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
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
import com.example.employeemanagementsystem.dto.patch.UserPatchDto;
import com.example.employeemanagementsystem.exception.ResourceNotFoundException;
import com.example.employeemanagementsystem.mapper.UserMapper;
import com.example.employeemanagementsystem.model.Employee;
import com.example.employeemanagementsystem.model.Role;
import com.example.employeemanagementsystem.model.User;
import com.example.employeemanagementsystem.repository.RoleRepository;
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
    private RoleRepository roleRepository;

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

    @Test
    void getUserByIdNotFoundThrows() {
        when(userRepository.findById(55L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(55L));
    }

    @Test
    void getUserByIdSuccessReturnsMappedDto() {
        User user = new User();
        UserDto dto = new UserDto();
        when(userRepository.findById(56L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(dto);

        UserDto result = userService.getUserById(56L);

        assertEquals(dto, result);
    }

    @Test
    void getUserByUsernameNotFoundThrows() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserByUsername("missing"));
    }

    @Test
    void getUserByUsernameSuccessReturnsMappedDto() {
        User user = new User();
        UserDto dto = new UserDto();
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(dto);

        UserDto result = userService.getUserByUsername("john");

        assertEquals(dto, result);
    }

    @Test
    void getAllUsersMapsResults() {
        User user = new User();
        UserDto dto = new UserDto();
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toDto(user)).thenReturn(dto);

        List<UserDto> result = userService.getAllUsers();

        assertEquals(1, result.size());
        verify(userMapper).toDto(user);
    }

    @Test
    void createUserWithRolesLoadsRolesById() {
        UserCreateDto createDto = new UserCreateDto();
        createDto.setPassword("secret");
        createDto.setRolesId(Set.of(1L));

        User user = new User();
        Role role = new Role();
        User savedUser = new User();
        UserDto out = new UserDto();

        when(userMapper.toEntity(createDto)).thenReturn(user);
        when(passwordEncoder.encode("secret")).thenReturn("encoded-secret");
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(out);

        UserDto result = userService.createUser(createDto);

        assertEquals(out, result);
        assertEquals(1, user.getRoles().size());
        verify(roleService, never()).findRoleByName("USER");
        verify(employeeSearchCache).invalidateAll();
    }

    @Test
    void createUserWithEmptyRolesUsesDefaultRole() {
        UserCreateDto createDto = new UserCreateDto();
        createDto.setPassword("secret");
        createDto.setRolesId(Set.of());

        User user = new User();
        Role defaultRole = new Role();
        User savedUser = new User();
        UserDto out = new UserDto();

        when(userMapper.toEntity(createDto)).thenReturn(user);
        when(passwordEncoder.encode("secret")).thenReturn("encoded-secret");
        when(roleService.findRoleByName("USER")).thenReturn(defaultRole);
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(out);

        UserDto result = userService.createUser(createDto);

        assertEquals(out, result);
        assertEquals(1, user.getRoles().size());
    }

    @Test
    void createUserWithMissingRoleThrowsNotFound() {
        UserCreateDto createDto = new UserCreateDto();
        createDto.setPassword("secret");
        createDto.setRolesId(Set.of(2L));
        User user = new User();

        when(userMapper.toEntity(createDto)).thenReturn(user);
        when(passwordEncoder.encode("secret")).thenReturn("encoded-secret");
        when(roleRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.createUser(createDto));
    }

    @Test
    void updateUserNotFoundThrows() {
        when(userRepository.findById(60L)).thenReturn(Optional.empty());
        UserCreateDto createDto = new UserCreateDto();

        assertThrows(ResourceNotFoundException.class,
                () -> userService.updateUser(60L, createDto));
    }

    @Test
    void updateUserWithPasswordAndRolesAppliesAllUpdates() {
        UserCreateDto updateDto = new UserCreateDto();
        updateDto.setUsername("new-name");
        updateDto.setPassword("new-password");
        updateDto.setRolesId(Set.of(5L));

        User user = new User();
        Role role = new Role();
        User savedUser = new User();
        UserDto dto = new UserDto();

        when(userRepository.findById(21L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("new-password")).thenReturn("encoded-new-password");
        when(roleRepository.findById(5L)).thenReturn(Optional.of(role));
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(dto);

        UserDto result = userService.updateUser(21L, updateDto);

        assertEquals(dto, result);
        assertEquals("new-name", user.getUsername());
        assertEquals("encoded-new-password", user.getPassword());
        assertEquals(1, user.getRoles().size());
        verify(employeeSearchCache).invalidateAll();
    }

    @Test
    void patchUserWithNullRolesKeepsExistingRoles() {
        UserPatchDto patchDto = new UserPatchDto();
        patchDto.setUsername("patched");
        patchDto.setRolesId(null);

        Role existingRole = new Role();
        User user = new User();
        user.setRoles(Set.of(existingRole));

        User savedUser = new User();
        UserDto dto = new UserDto();

        when(userRepository.findById(33L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(dto);

        UserDto result = userService.patchUser(33L, patchDto);

        assertEquals(dto, result);
        assertEquals("patched", user.getUsername());
        assertEquals(1, user.getRoles().size());
        verify(employeeSearchCache).invalidateAll();
    }

    @Test
    void patchUserWithMissingRoleThrows() {
        UserPatchDto patchDto = new UserPatchDto();
        patchDto.setRolesId(Set.of(999L));

        User user = new User();
        when(userRepository.findById(34L)).thenReturn(Optional.of(user));
        when(roleRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.patchUser(34L, patchDto));
    }

    @Test
    void patchUserWithEmptyPasswordDoesNotEncode() {
        UserPatchDto patchDto = new UserPatchDto();
        patchDto.setPassword("");

        User user = new User();
        User savedUser = new User();
        UserDto dto = new UserDto();
        when(userRepository.findById(35L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(dto);

        UserDto result = userService.patchUser(35L, patchDto);

        assertEquals(dto, result);
        verify(passwordEncoder, never()).encode(any(String.class));
    }

    @Test
    void patchUserNotFoundThrows() {
        when(userRepository.findById(61L)).thenReturn(Optional.empty());
        UserPatchDto patchDto = new UserPatchDto();

        assertThrows(ResourceNotFoundException.class,
                () -> userService.patchUser(61L, patchDto));
    }

    @Test
    void deleteUserWithoutLinkedEmployeeDeletesOnlyUser() {
        User user = new User();
        when(userRepository.findById(70L)).thenReturn(Optional.of(user));
        when(employeeService.getEmployeeByUserId(70L)).thenReturn(null);

        userService.deleteUser(70L);

        verify(userRepository).deleteById(70L);
        verify(employeeSearchCache).invalidateAll();
    }

    @Test
    void deleteUserNotFoundThrows() {
        when(userRepository.findById(71L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(71L));
    }
}
