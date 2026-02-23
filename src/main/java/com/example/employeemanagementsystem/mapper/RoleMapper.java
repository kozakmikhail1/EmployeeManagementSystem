
package com.example.employeemanagementsystem.mapper;

import com.example.employeemanagementsystem.dto.create.RoleCreateDto;
import com.example.employeemanagementsystem.dto.get.RoleDto;
import com.example.employeemanagementsystem.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "users", ignore = true) 
    Role toEntity(RoleCreateDto dto);

    RoleDto toDto(Role entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "users", ignore = true)
    void updateRoleFromDto(RoleCreateDto dto, @MappingTarget Role entity);
}