package com.example.employeemanagementsystem.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.employeemanagementsystem.dto.create.UserCreateDto;
import com.example.employeemanagementsystem.dto.get.UserDto;
import com.example.employeemanagementsystem.exception.ResourceNotFoundException;
import com.example.employeemanagementsystem.mapper.UserMapper;
import com.example.employeemanagementsystem.model.Role;
import com.example.employeemanagementsystem.model.User;
import com.example.employeemanagementsystem.repository.RoleRepository;
import com.example.employeemanagementsystem.repository.UserRepository;

@Service
public class UserService {

    private static final String USER_NOT_FOUND_WITH_ID_MESSAGE = "User not found with id ";
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    @Autowired
    public UserService(
            UserRepository userRepository,
            UserMapper userMapper,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            RoleService roleService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
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
        if (userCreateDto.getRoleIds() != null && !userCreateDto.getRoleIds().isEmpty()) {
            userCreateDto.getRoleIds().forEach(
                    roleId -> {
                        Role role =
                                roleRepository.findById(roleId)
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                "Role not found with id " + roleId));
                        roles.add(role);
                    });
        } else {
            roles.add(roleService.findRoleByName("USER"));
        }
        user.setRoles(roles);

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Transactional
    public UserDto updateUser(Long id, UserCreateDto userCreateDto) {
        User user =
                userRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                USER_NOT_FOUND_WITH_ID_MESSAGE + id));

        userMapper.updateUserFromDto(userCreateDto, user);

        if (userCreateDto.getPassword() != null && !userCreateDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userCreateDto.getPassword()));
        }

        if (userCreateDto.getRoleIds() != null) {
            Set<Role> newRoles = new HashSet<>();
            for (Long roleId : userCreateDto.getRoleIds()) {
                Role role =
                        roleRepository.findById(roleId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                        "Role not found with id " + roleId));
                newRoles.add(role);
            }
            user.setRoles(newRoles);
        }

        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID_MESSAGE + id));
        userRepository.deleteById(id);
    }
}