package com.example.employeemanagementsystem.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.example.employeemanagementsystem.dto.create.PositionCreateDto;
import com.example.employeemanagementsystem.dto.patch.PositionPatchDto;
import com.example.employeemanagementsystem.dto.get.PositionDto;
import com.example.employeemanagementsystem.model.Position;

@Mapper(componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PositionMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employees", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Position toEntity(PositionCreateDto positionCreateDto);

    PositionDto toDto(Position position);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employees", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updatePositionFromDto(PositionCreateDto dto, @MappingTarget Position entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employees", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updatePositionFromPatchDto(PositionPatchDto dto, @MappingTarget Position entity);
}
