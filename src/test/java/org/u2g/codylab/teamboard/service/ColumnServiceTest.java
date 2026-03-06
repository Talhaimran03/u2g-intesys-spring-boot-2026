package org.u2g.codylab.teamboard.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.u2g.codylab.teamboard.dto.*;
import org.u2g.codylab.teamboard.entity.Column;
import org.u2g.codylab.teamboard.entity.Project;
import org.u2g.codylab.teamboard.exception.CustomIllegalArgumentException;
import org.u2g.codylab.teamboard.mapper.ColumnMapper;
import org.u2g.codylab.teamboard.repository.ColumnRepository;
import org.u2g.codylab.teamboard.repository.ProjectRepository;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ColumnServiceTest {

    @Mock
    private ColumnRepository columnRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ColumnMapper columnMapper;
    @InjectMocks
    private ColumnService columnService;

    @Test
    void shouldCreateColumnSuccessfully() {

        // Arrange
        CreateColumnRequestApiDTO dto = new CreateColumnRequestApiDTO().title("Colonna").projectId(1L);
        Project project = new Project();
        Column column = new Column();
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(columnMapper.toEntity(any(CreateColumnRequestApiDTO.class))).thenReturn(column);
        when(columnRepository.save(any())).thenReturn(column);
        when(columnMapper.toResponse(any())).thenReturn(new ColumnResponseApiDTO());

        // Act & Assert
        assertDoesNotThrow(() -> columnService.create(dto));
        verify(columnRepository).save(any(Column.class));
    }

    @Test
    void shouldThrowWhenProjectNotFound() {

        // Arrange
        CreateColumnRequestApiDTO dto = new CreateColumnRequestApiDTO().title("Colonna").projectId(1L);
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomIllegalArgumentException.class, () -> columnService.create(dto));
    }

    @Test
    void shouldUpdateColumnSuccessfully() {

        // Arrange
        UpdateColumnRequestApiDTO dto = new UpdateColumnRequestApiDTO().title("Nuovo titolo");
        Column column = new Column();
        when(columnRepository.findById(1L)).thenReturn(Optional.of(column));
        when(columnRepository.save(any())).thenReturn(column);
        when(columnMapper.toResponse(any())).thenReturn(new ColumnResponseApiDTO());

        // Act & Assert
        assertDoesNotThrow(() -> columnService.update(1L, dto));
        verify(columnRepository).save(any(Column.class));
    }

    @Test
    void shouldThrowWhenColumnNotFoundOnUpdate() {

        // Arrange
        UpdateColumnRequestApiDTO dto = new UpdateColumnRequestApiDTO().title("Nuovo titolo");
        when(columnRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomIllegalArgumentException.class, () -> columnService.update(1L, dto));
    }

    @Test
    void shouldDeleteColumnSuccessfully() {

        // Arrange
        when(columnRepository.existsById(1L)).thenReturn(true);
        doNothing().when(columnRepository).deleteById(1L);

        // Act & Assert
        assertDoesNotThrow(() -> columnService.delete(1L));
        verify(columnRepository).deleteById(1L);
    }

    @Test
    void shouldThrowWhenColumnNotFoundOnDelete() {

        // Arrange
        when(columnRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(CustomIllegalArgumentException.class, () -> columnService.delete(1L));
    }
}
