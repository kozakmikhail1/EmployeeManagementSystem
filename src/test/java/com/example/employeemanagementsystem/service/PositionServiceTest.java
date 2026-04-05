package com.example.employeemanagementsystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.employeemanagementsystem.cache.EmployeeSearchCache;
import com.example.employeemanagementsystem.dto.create.PositionCreateDto;
import com.example.employeemanagementsystem.dto.get.PositionDto;
import com.example.employeemanagementsystem.exception.ResourceConflictException;
import com.example.employeemanagementsystem.mapper.PositionMapper;
import com.example.employeemanagementsystem.model.Position;
import com.example.employeemanagementsystem.repository.EmployeeRepository;
import com.example.employeemanagementsystem.repository.PositionRepository;

@ExtendWith(MockitoExtension.class)
class PositionServiceTest {

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PositionMapper positionMapper;

    @Mock
    private EmployeeSearchCache employeeSearchCache;

    @InjectMocks
    private PositionService positionService;

    @Test
    void createPositionSuccess() {
        PositionCreateDto createDto = new PositionCreateDto();
        createDto.setName("Developer");

        Position entity = new Position();
        Position saved = new Position();
        saved.setId(1L);
        saved.setName("Developer");

        PositionDto dto = new PositionDto();
        dto.setId(1L);
        dto.setName("Developer");

        when(positionMapper.toEntity(createDto)).thenReturn(entity);
        when(positionRepository.save(entity)).thenReturn(saved);
        when(positionMapper.toDto(saved)).thenReturn(dto);

        PositionDto result = positionService.createPosition(createDto);

        assertEquals(1L, result.getId());
        verify(employeeSearchCache).invalidateAll();
    }

    @Test
    void deletePositionWithAssignedEmployeesThrowsConflict() {
        when(positionRepository.existsById(5L)).thenReturn(true);
        when(employeeRepository.existsByPositionId(5L)).thenReturn(true);

        assertThrows(ResourceConflictException.class, () -> positionService.deletePosition(5L));

        verify(positionRepository, never()).deleteById(5L);
    }
}
