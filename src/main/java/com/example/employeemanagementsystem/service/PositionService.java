package com.example.employeemanagementsystem.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.employeemanagementsystem.dto.create.PositionCreateDto;
import com.example.employeemanagementsystem.dto.get.PositionDto;
import com.example.employeemanagementsystem.exception.ResourceNotFoundException;
import com.example.employeemanagementsystem.mapper.PositionMapper;
import com.example.employeemanagementsystem.model.Position;
import com.example.employeemanagementsystem.repository.PositionRepository;

@Service
public class PositionService {

    private static final String POSITION_NOT_FOUND_MESSAGE = "Position not found with id ";

    private final PositionRepository positionRepository;
    private final PositionMapper positionMapper;

    @Autowired
    public PositionService(PositionRepository positionRepository, PositionMapper positionMapper) {
        this.positionRepository = positionRepository;
        this.positionMapper = positionMapper;
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
                .toList(); // Or .toList() for Java 16+ unmodifiable list
    }

    @Transactional
    public PositionDto createPosition(PositionCreateDto positionCreateDto) {
        Position position = positionMapper.toEntity(positionCreateDto);
        Position savedPosition = positionRepository.save(position);
        return positionMapper.toDto(savedPosition);
    }

    @Transactional
    public PositionDto updatePosition(Long id, PositionCreateDto positionCreateDto) {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(POSITION_NOT_FOUND_MESSAGE + id));
        positionMapper.updatePositionFromDto(positionCreateDto, position);
        Position updatedPosition = positionRepository.save(position);
        return positionMapper.toDto(updatedPosition);
    }

    @Transactional
    public void deletePosition(Long id) {
        if (!positionRepository.existsById(id)) {
            throw new ResourceNotFoundException(POSITION_NOT_FOUND_MESSAGE + id);
        }
        positionRepository.deleteById(id);
    }
}