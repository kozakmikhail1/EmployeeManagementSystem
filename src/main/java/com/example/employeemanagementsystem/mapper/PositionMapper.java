
package com.example.employeemanagementsystem.mapper;

import com.example.employeemanagementsystem.dto.create.PositionCreateDto;
import com.example.employeemanagementsystem.dto.get.PositionDto;
import com.example.employeemanagementsystem.model.Position;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PositionMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employees", ignore = true)
    Position toEntity(PositionCreateDto positionCreateDto);

    PositionDto toDto(Position position);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employees", ignore = true)
    void updatePositionFromDto(PositionCreateDto dto, @MappingTarget Position entity);
}