package com.example.employeemanagementsystem.mapper;

import com.example.employeemanagementsystem.dto.create.UserCreateDto;
import com.example.employeemanagementsystem.dto.get.UserDto;
import com.example.employeemanagementsystem.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "roles", ignore = true)
    public abstract User toEntity(UserCreateDto userCreateDto);

    @Mapping(target = "employee", ignore = true)
    public abstract UserDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "roles", ignore = true)
    public abstract void updateUserFromDto(UserCreateDto dto, @MappingTarget User entity);
}