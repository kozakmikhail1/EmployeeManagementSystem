package com.example.employeemanagementsystem.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.employeemanagementsystem.dto.create.EmployeeCreateDto;
import com.example.employeemanagementsystem.dto.get.EmployeeDto;
import com.example.employeemanagementsystem.exception.ResourceNotFoundException;
import com.example.employeemanagementsystem.model.Department;
import com.example.employeemanagementsystem.model.Employee;
import com.example.employeemanagementsystem.model.Position;
import com.example.employeemanagementsystem.model.User;
import com.example.employeemanagementsystem.repository.DepartmentDao;
import com.example.employeemanagementsystem.repository.PositionDao;
import com.example.employeemanagementsystem.repository.UserDao;

@Mapper(componentModel = "spring", uses = {DepartmentMapper.class,
        PositionMapper.class, UserMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public abstract class EmployeeMapper {

    @Autowired
    protected DepartmentDao departmentDao;

    @Autowired
    protected PositionDao positionDao;

    @Autowired
    protected UserDao userDao;

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "departmentId", target = "department")
    @Mapping(source = "positionId", target = "position")
    @Mapping(source = "userId", target = "user")
    public abstract Employee toEntity(EmployeeCreateDto dto);

    public abstract EmployeeDto toDto(Employee entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "departmentId", target = "department")
    @Mapping(source = "positionId", target = "position")
    @Mapping(source = "userId", target = "user")
    public abstract void updateEmployeeFromDto(EmployeeCreateDto dto,
                                               @MappingTarget Employee entity);


    protected Department departmentFromId(Long departmentId) {
        return departmentId == null ? null
                : departmentDao.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id "
                        + departmentId));
    }

    protected Position positionFromId(Long positionId) {
        return positionId == null ? null
                : positionDao.findById(positionId)
                .orElseThrow(() -> new ResourceNotFoundException("Position not found with id "
                        + positionId));
    }

    protected User userFromId(Long userId) {
        return userId == null ? null
                : userDao.findById(userId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("User not found with id " + userId));
    }
}