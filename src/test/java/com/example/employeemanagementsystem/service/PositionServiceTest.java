package com.example.employeemanagementsystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    void getPositionByIdNotFoundThrows() {
        when(positionRepository.findById(123L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> positionService.getPositionById(123L));
    }

    @Test
    void getPositionByIdSuccessReturnsMappedDto() {
        Position position = new Position();
        PositionDto dto = new PositionDto();
        when(positionRepository.findById(124L)).thenReturn(Optional.of(position));
        when(positionMapper.toDto(position)).thenReturn(dto);

        PositionDto result = positionService.getPositionById(124L);

        assertEquals(dto, result);
    }

    @Test
    void getAllPositionsMapsEachEntity() {
        Position position = new Position();
        PositionDto dto = new PositionDto();
        when(positionRepository.findAll()).thenReturn(List.of(position));
        when(positionMapper.toDto(position)).thenReturn(dto);

        List<PositionDto> result = positionService.getAllPositions();

        assertEquals(1, result.size());
        verify(positionMapper).toDto(position);
    }

    @Test
    void updatePositionSuccess() {
        PositionCreateDto updateDto = new PositionCreateDto();
        Position existingPosition = new Position();
        Position savedPosition = new Position();
        PositionDto dto = new PositionDto();

        when(positionRepository.findById(7L)).thenReturn(Optional.of(existingPosition));
        when(positionRepository.save(existingPosition)).thenReturn(savedPosition);
        when(positionMapper.toDto(savedPosition)).thenReturn(dto);

        PositionDto result = positionService.updatePosition(7L, updateDto);

        assertEquals(dto, result);
        verify(positionMapper).updatePositionFromDto(updateDto, existingPosition);
        verify(employeeSearchCache).invalidateAll();
    }

    @Test
    void updatePositionNotFoundThrows() {
        when(positionRepository.findById(70L)).thenReturn(Optional.empty());
        PositionCreateDto createDto = new PositionCreateDto();

        assertThrows(ResourceNotFoundException.class,
                () -> positionService.updatePosition(70L, createDto));
    }

    @Test
    void patchPositionSuccess() {
        PositionPatchDto patchDto = new PositionPatchDto();
        Position existingPosition = new Position();
        Position savedPosition = new Position();
        PositionDto dto = new PositionDto();

        when(positionRepository.findById(8L)).thenReturn(Optional.of(existingPosition));
        when(positionRepository.save(existingPosition)).thenReturn(savedPosition);
        when(positionMapper.toDto(savedPosition)).thenReturn(dto);

        PositionDto result = positionService.patchPosition(8L, patchDto);

        assertEquals(dto, result);
        verify(positionMapper).updatePositionFromPatchDto(patchDto, existingPosition);
        verify(employeeSearchCache).invalidateAll();
    }

    @Test
    void patchPositionNotFoundThrows() {
        when(positionRepository.findById(71L)).thenReturn(Optional.empty());
        PositionPatchDto patchDto = new PositionPatchDto();

        assertThrows(ResourceNotFoundException.class,
                () -> positionService.patchPosition(71L, patchDto));
    }

    @Test
    void deletePositionWithAssignedEmployeesThrowsConflict() {
        when(positionRepository.existsById(5L)).thenReturn(true);
        when(employeeRepository.existsByPositionId(5L)).thenReturn(true);

        assertThrows(ResourceConflictException.class, () -> positionService.deletePosition(5L));

        verify(positionRepository, never()).deleteById(5L);
    }

    @Test
    void deletePositionNotFoundThrows() {
        when(positionRepository.existsById(6L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> positionService.deletePosition(6L));
        verify(positionRepository, never()).deleteById(6L);
    }

    @Test
    void deletePositionSuccessDeletesAndInvalidatesCache() {
        when(positionRepository.existsById(9L)).thenReturn(true);
        when(employeeRepository.existsByPositionId(9L)).thenReturn(false);

        positionService.deletePosition(9L);

        verify(positionRepository).deleteById(9L);
        verify(employeeSearchCache).invalidateAll();
    }
}
