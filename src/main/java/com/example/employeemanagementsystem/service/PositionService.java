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
import com.example.employeemanagementsystem.repository.PositionDao;

@Service
public class PositionService {

    private static final String POSITION_NOT_FOUND_MESSAGE = "Position not found with id ";

    private final PositionDao positionDao;
    private final PositionMapper positionMapper;

    @Autowired
    public PositionService(PositionDao positionDao, PositionMapper positionMapper) {
        this.positionDao = positionDao;
        this.positionMapper = positionMapper;
    }

    @Transactional(readOnly = true)
    public PositionDto getPositionById(Long id) {
        return positionDao.findById(id)
                .map(positionMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(POSITION_NOT_FOUND_MESSAGE + id));
    }

    @Transactional(readOnly = true)
    public List<PositionDto> getAllPositions() {
        return positionDao.findAll().stream()
                .map(positionMapper::toDto)
                .toList(); // Or .toList() for Java 16+ unmodifiable list
    }

    @Transactional
    public PositionDto createPosition(PositionCreateDto positionCreateDto) {
        Position position = positionMapper.toEntity(positionCreateDto);
        Position savedPosition = positionDao.save(position);
        return positionMapper.toDto(savedPosition);
    }

    @Transactional
    public PositionDto updatePosition(Long id, PositionCreateDto positionCreateDto) {
        Position position = positionDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(POSITION_NOT_FOUND_MESSAGE + id));
        positionMapper.updatePositionFromDto(positionCreateDto, position);
        Position updatedPosition = positionDao.save(position);
        return positionMapper.toDto(updatedPosition);
    }

    @Transactional
    public void deletePosition(Long id) {
        if (!positionDao.existsById(id)) {
            throw new ResourceNotFoundException(POSITION_NOT_FOUND_MESSAGE + id);
        }
        positionDao.deleteById(id);
    }
}