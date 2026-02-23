
package com.example.employeemanagementsystem.mapper;

import com.example.employeemanagementsystem.dto.create.DepartmentCreateDto;
import com.example.employeemanagementsystem.dto.get.DepartmentDto;
import com.example.employeemanagementsystem.model.Department;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DepartmentMapper {

    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employees", ignore = true) 
    Department toEntity(DepartmentCreateDto dto);

    
    DepartmentDto toDto(Department entity);

    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employees", ignore = true)
    void updateDepartmentFromDto(DepartmentCreateDto dto, @MappingTarget Department entity);

}