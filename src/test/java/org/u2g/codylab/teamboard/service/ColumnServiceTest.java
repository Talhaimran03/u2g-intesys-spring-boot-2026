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



    @Test
    void shouldGetAllColumnsSuccessfully() {
        // Arrange
        Column column = new Column().setTitle("To Do").setPosition(1);
        when(columnRepository.findAll()).thenReturn(List.of(column));
        when(columnMapper.toResponse(any(Column.class)))
                .thenReturn(new ColumnResponseApiDTO().id(1L).title("To Do"));

        // Act
        List<ColumnResponseApiDTO> result = columnService.getAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(columnRepository).findAll();
    }



    @Test
    void shouldGetColumnByIdSuccessfully() {
        // Arrange
        Column column = new Column().setId(1L).setTitle("To Do");
        when(columnRepository.findById(1L)).thenReturn(Optional.of(column));
        when(columnMapper.toResponse(column))
                .thenReturn(new ColumnResponseApiDTO().id(1L).title("To Do"));

        // Act
        ColumnResponseApiDTO result = columnService.getById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void shouldThrowExceptionWhenColumnNotFoundById() {
        // Arrange
        when(columnRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomNotFoundException.class, () -> columnService.getById(99L));
    }



    @Test
    void shouldCreateColumnSuccessfully() {
        // Arrange
        Project project = new Project().setId(1L).setTitle("My Project");
        Column column = new Column().setTitle("To Do").setPosition(1);

        CreateColumnRequestApiDTO request = new CreateColumnRequestApiDTO()
                .title("To Do")
                .position(1)
                .projectId(1L);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(columnMapper.toEntity(any(CreateColumnRequestApiDTO.class))).thenReturn(column);
        when(columnRepository.save(any(Column.class))).thenReturn(column);
        when(columnMapper.toResponse(any(Column.class)))
                .thenReturn(new ColumnResponseApiDTO().id(1L).title("To Do"));

        // Act
        ColumnResponseApiDTO result = columnService.create(request);

        // Assert
        assertNotNull(result);
        verify(columnRepository).save(any(Column.class));
    }

    @Test
    void shouldThrowExceptionWhenProjectNotFoundOnCreate() {
        // Arrange
        CreateColumnRequestApiDTO request = new CreateColumnRequestApiDTO().projectId(99L);
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomNotFoundException.class, () -> columnService.create(request));
        verify(columnRepository, never()).save(any());
    }



    @Test
    void shouldUpdateColumnSuccessfully() {
        // Arrange
        Column column = new Column().setId(1L).setTitle("To Do").setPosition(1);

        UpdateColumnRequestApiDTO request = new UpdateColumnRequestApiDTO()
                .title("In Progress")
                .position(2);

        when(columnRepository.findById(1L)).thenReturn(Optional.of(column));
        when(columnRepository.save(any(Column.class))).thenReturn(column);
        when(columnMapper.toResponse(any(Column.class)))
                .thenReturn(new ColumnResponseApiDTO().id(1L).title("In Progress"));

        // Act
        ColumnResponseApiDTO result = columnService.update(1L, request);

        // Assert
        assertNotNull(result);
        assertEquals("In Progress", result.getTitle());
        verify(columnRepository).save(any(Column.class));
    }

    @Test
    void shouldThrowExceptionWhenColumnNotFoundOnUpdate() {
        // Arrange
        when(columnRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomNotFoundException.class,
                () -> columnService.update(99L, new UpdateColumnRequestApiDTO()));
    }



    @Test
    void shouldDeleteColumnSuccessfully() {
        // Arrange
        when(columnRepository.existsById(1L)).thenReturn(true);

        // Act & Assert
        assertDoesNotThrow(() -> columnService.delete(1L));
        verify(columnRepository).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenColumnNotFoundOnDelete() {
        // Arrange
        when(columnRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThrows(CustomNotFoundException.class, () -> columnService.delete(99L));
        verify(columnRepository, never()).deleteById(any());
    }
}
