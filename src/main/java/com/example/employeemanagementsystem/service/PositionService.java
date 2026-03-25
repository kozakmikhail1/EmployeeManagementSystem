package com.example.employeemanagementsystem.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.employeemanagementsystem.cache.EmployeeSearchCache;
import com.example.employeemanagementsystem.dto.create.PositionCreateDto;
import com.example.employeemanagementsystem.dto.get.PositionDto;
import com.example.employeemanagementsystem.dto.patch.PositionPatchDto;
import com.example.employeemanagementsystem.exception.ResourceConflictException;
import com.example.employeemanagementsystem.exception.ResourceNotFoundException;
import com.example.employeemanagementsystem.mapper.PositionMapper;
import com.example.employeemanagementsystem.model.Position;
import com.example.employeemanagementsystem.repository.EmployeeRepository;
import com.example.employeemanagementsystem.repository.PositionRepository;

@Service
public class PositionService {

    private static final String POSITION_NOT_FOUND_MESSAGE = "Position not found with id ";
    private static final String POSITION_HAS_EMPLOYEES_MESSAGE =
            "Cannot delete position with assigned employees. Position id ";

    private final PositionRepository positionRepository;
    private final EmployeeRepository employeeRepository;
    private final PositionMapper positionMapper;
    private final EmployeeSearchCache employeeSearchCache;

    @Autowired
    public PositionService(
            PositionRepository positionRepository,
            EmployeeRepository employeeRepository,
            PositionMapper positionMapper,
            EmployeeSearchCache employeeSearchCache) {
        this.positionRepository = positionRepository;
        this.employeeRepository = employeeRepository;
        this.positionMapper = positionMapper;
        this.employeeSearchCache = employeeSearchCache;
    }

    @Transactional(readOnly = true)
    public PositionDto getPositionById(Long id) {
        return positionRepository.findById(id)
            .map(positionMapper::toDto)
            .orElseThrow(() -> new ResourceNotFoundException(POSITION_NOT_FOUND_MESSAGE + id));
    }

    @Transactional(readOnly = true)
    public List<PositionDto> getAllPositions() {
        return positionRepository.findAll().stream()
            .map(positionMapper::toDto)
            .toList(); 
    }

    @Transactional
    public PositionDto createPosition(PositionCreateDto positionCreateDto) {
        Position position = positionMapper.toEntity(positionCreateDto);
        Position savedPosition = positionRepository.save(position);
        employeeSearchCache.invalidateAll();
        return positionMapper.toDto(savedPosition);
    }

    @Transactional
    public PositionDto updatePosition(Long id, PositionCreateDto positionCreateDto) {
        Position position = positionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(POSITION_NOT_FOUND_MESSAGE + id));
        positionMapper.updatePositionFromDto(positionCreateDto, position);
        Position updatedPosition = positionRepository.save(position);
        employeeSearchCache.invalidateAll();
        return positionMapper.toDto(updatedPosition);
    }

    @Transactional
    public PositionDto patchPosition(Long id, PositionPatchDto positionCreateDto) {
        Position position = positionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(POSITION_NOT_FOUND_MESSAGE + id));
        positionMapper.updatePositionFromPatchDto(positionCreateDto, position);
        Position updatedPosition = positionRepository.save(position);
        employeeSearchCache.invalidateAll();
        return positionMapper.toDto(updatedPosition);
    }

    @Transactional
    public void deletePosition(Long id) {
        if (!positionRepository.existsById(id)) {
            throw new ResourceNotFoundException(POSITION_NOT_FOUND_MESSAGE + id);
        }
        if (employeeRepository.existsByPositionId(id)) {
            throw new ResourceConflictException(POSITION_HAS_EMPLOYEES_MESSAGE + id);
        }
        positionRepository.deleteById(id);
        employeeSearchCache.invalidateAll();
    }
}
