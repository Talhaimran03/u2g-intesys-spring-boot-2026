package org.u2g.codylab.teamboard.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.u2g.codylab.teamboard.dto.ColumnResponseApiDTO;
import org.u2g.codylab.teamboard.dto.CreateColumnRequestApiDTO;
import org.u2g.codylab.teamboard.dto.UpdateColumnRequestApiDTO;
import org.u2g.codylab.teamboard.entity.Column;
import org.u2g.codylab.teamboard.entity.Project;
import org.u2g.codylab.teamboard.exception.CustomIllegalArgumentException;
import org.u2g.codylab.teamboard.exception.CustomNotFoundException;
import org.u2g.codylab.teamboard.mapper.ColumnMapper;
import org.u2g.codylab.teamboard.repository.ColumnRepository;
import org.u2g.codylab.teamboard.repository.ProjectRepository;

import java.util.List;
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

    // ─── GET ALL ─────────────────────────────────────────────

    @Test
    void shouldGetAllColumnsSuccessfully() {
        // Arrange
        Column column = new Column().setId(1L).setTitle("TO DO");
        ColumnResponseApiDTO dto = new ColumnResponseApiDTO().id(1L).title("TO DO");

        when(columnRepository.findAll()).thenReturn(List.of(column));
        when(columnMapper.toResponse(column)).thenReturn(dto);

        // Act
        List<ColumnResponseApiDTO> result = columnService.getAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(columnRepository).findAll();
    }

    // ─── GET BY ID ───────────────────────────────────────────

    @Test
    void shouldGetColumnByIdSuccessfully() {
        // Arrange
        Column column = new Column().setId(1L).setTitle("TO DO");
        ColumnResponseApiDTO dto = new ColumnResponseApiDTO().id(1L).title("TO DO");

        when(columnRepository.findById(1L)).thenReturn(Optional.of(column));
        when(columnMapper.toResponse(column)).thenReturn(dto);

        // Act
        ColumnResponseApiDTO result = columnService.getById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void shouldThrowWhenColumnNotFound() {
        // Arrange
        when(columnRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomNotFoundException.class, () -> columnService.getById(99L));
    }

    // ─── CREATE ──────────────────────────────────────────────

    @Test
    void shouldCreateColumnSuccessfully() {
        // Arrange
        CreateColumnRequestApiDTO request = new CreateColumnRequestApiDTO()
                .title("TO DO")
                .position(0)
                .projectId(1L);

        Project project = new Project().setId(1L).setTitle("Demo");
        Column column = new Column().setId(1L).setTitle("TO DO");
        ColumnResponseApiDTO dto = new ColumnResponseApiDTO().id(1L).title("TO DO");

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(columnMapper.toEntity(any(CreateColumnRequestApiDTO.class))).thenReturn(column);
        when(columnRepository.save(any(Column.class))).thenReturn(column);
        when(columnMapper.toResponse(column)).thenReturn(dto);

        // Act
        ColumnResponseApiDTO result = columnService.create(request);

        // Assert
        assertNotNull(result);
        verify(columnRepository).save(any(Column.class));
    }

    @Test
    void shouldThrowWhenTitleIsBlankOnCreate() {
        // Arrange
        CreateColumnRequestApiDTO request = new CreateColumnRequestApiDTO()
                .title("")
                .projectId(1L);

        // Act & Assert
        assertThrows(CustomIllegalArgumentException.class, () -> columnService.create(request));
        verify(columnRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenProjectNotFoundOnCreate() {
        // Arrange
        CreateColumnRequestApiDTO request = new CreateColumnRequestApiDTO()
                .title("TO DO")
                .projectId(99L);

        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomNotFoundException.class, () -> columnService.create(request));
        verify(columnRepository, never()).save(any());
    }

    // ─── UPDATE ──────────────────────────────────────────────

    @Test
    void shouldUpdateColumnSuccessfully() {
        // Arrange
        Column column = new Column().setId(1L).setTitle("TO DO");
        ColumnResponseApiDTO dto = new ColumnResponseApiDTO().id(1L).title("IN PROGRESS");

        UpdateColumnRequestApiDTO request = new UpdateColumnRequestApiDTO()
                .title("IN PROGRESS")
                .position(1);

        when(columnRepository.findById(1L)).thenReturn(Optional.of(column));
        when(columnRepository.save(any(Column.class))).thenReturn(column);
        when(columnMapper.toResponse(column)).thenReturn(dto);

        // Act
        ColumnResponseApiDTO result = columnService.update(1L, request);

        // Assert
        assertNotNull(result);
        verify(columnRepository).save(column);
    }

    @Test
    void shouldThrowWhenColumnNotFoundOnUpdate() {
        // Arrange
        when(columnRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomNotFoundException.class,
                () -> columnService.update(99L, new UpdateColumnRequestApiDTO().title("Title")));
    }

    @Test
    void shouldThrowWhenTitleIsBlankOnUpdate() {
        // Arrange
        Column column = new Column().setId(1L).setTitle("TO DO");

        UpdateColumnRequestApiDTO request = new UpdateColumnRequestApiDTO()
                .title("");

        when(columnRepository.findById(1L)).thenReturn(Optional.of(column));

        // Act & Assert
        assertThrows(CustomIllegalArgumentException.class, () -> columnService.update(1L, request));
        verify(columnRepository, never()).save(any());
    }

    // ─── DELETE ──────────────────────────────────────────────

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
    void shouldThrowWhenColumnToDeleteNotFound() {
        // Arrange
        when(columnRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThrows(CustomNotFoundException.class, () -> columnService.delete(99L));
        verify(columnRepository, never()).deleteById(any());
    }
}
