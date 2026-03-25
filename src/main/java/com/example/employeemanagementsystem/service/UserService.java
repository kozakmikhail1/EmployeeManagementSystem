package com.example.employeemanagementsystem.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

@Service
public class UserService {

    private static final String USER_NOT_FOUND_WITH_ID_MESSAGE = "User not found with id ";
    private static final String ROLE_NOT_FOUND_WITH_ID_MESSAGE = "Role not found with id ";
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final EmployeeService employeeService;
    private final EmployeeSearchCache employeeSearchCache;

    @Autowired
    public UserService(
        UserRepository userRepository,
        UserMapper userMapper,
        RoleRepository roleRepository,
        PasswordEncoder passwordEncoder,
        RoleService roleService,
        EmployeeService employeeService,
        EmployeeSearchCache employeeSearchCache) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.employeeService = employeeService;
        this.employeeSearchCache = employeeSearchCache;
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        return userRepository.findById(id)
            .map(userMapper::toDto)
            .orElseThrow(
                () -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID_MESSAGE + id));
    }

    @Transactional(readOnly = true)
    public UserDto getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .map(userMapper::toDto)
            .orElseThrow(
                () -> new ResourceNotFoundException(
                    USER_NOT_FOUND_WITH_ID_MESSAGE + username));
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toDto).toList();
    }

    @Transactional
    public UserDto createUser(UserCreateDto userCreateDto) {
        User user = userMapper.toEntity(userCreateDto);

        user.setPassword(passwordEncoder.encode(userCreateDto.getPassword()));

        Set<Role> roles = new HashSet<>();
        if (userCreateDto.getRolesId() != null && !userCreateDto.getRolesId().isEmpty()) {
            userCreateDto.getRolesId().forEach(
                roleId -> {
                    Role role =
                        roleRepository.findById(roleId)
                            .orElseThrow(() -> new ResourceNotFoundException(
                                ROLE_NOT_FOUND_WITH_ID_MESSAGE + roleId));
                    roles.add(role);
                });
        } else {
            roles.add(roleService.findRoleByName("USER"));
        }
        user.setRoles(roles);

        User savedUser = userRepository.save(user);
        employeeSearchCache.invalidateAll();
        return userMapper.toDto(savedUser);
    }

    @Transactional
    public UserDto updateUser(Long id, UserCreateDto userCreateDto) {
        User user =
            userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    USER_NOT_FOUND_WITH_ID_MESSAGE + id));

        applyUserUpdates(user, userCreateDto.getUsername(),
                userCreateDto.getPassword(), userCreateDto.getRolesId());

        User updatedUser = userRepository.save(user);
        employeeSearchCache.invalidateAll();
        return userMapper.toDto(updatedUser);
    }

    @Transactional
    public UserDto patchUser(Long id, UserPatchDto userCreateDto) {
        User user =
            userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    USER_NOT_FOUND_WITH_ID_MESSAGE + id));

        applyUserUpdates(user, userCreateDto.getUsername(),
                userCreateDto.getPassword(), userCreateDto.getRolesId());

        User updatedUser = userRepository.save(user);
        employeeSearchCache.invalidateAll();
        return userMapper.toDto(updatedUser);
    }

    private void applyUserUpdates(
            User user,
            String username,
            String password,
            Set<Long> rolesId) {
        if (username != null) {
            user.setUsername(username);
        }

        if (password != null && !password.isEmpty()) {
            user.setPassword(passwordEncoder.encode(password));
        }

        if (rolesId != null) {
            Set<Role> newRoles = new HashSet<>();
            for (Long roleId : rolesId) {
                Role role =
                    roleRepository.findById(roleId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                            ROLE_NOT_FOUND_WITH_ID_MESSAGE + roleId));
                newRoles.add(role);
            }
            user.setRoles(newRoles);
        }
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.findById(id)
            .orElseThrow(
                () -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID_MESSAGE + id));
        Employee employee = employeeService.getEmployeeByUserId(id);
        if (employee != null) {
            employee.setUser(null);
        }
        userRepository.deleteById(id);
        employeeSearchCache.invalidateAll();
    }
}
