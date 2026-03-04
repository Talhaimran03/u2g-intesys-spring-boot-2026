package org.u2g.codylab.teamboard.service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.u2g.codylab.teamboard.dto.CardApiDTO;
import org.u2g.codylab.teamboard.exception.CustomNotFoundException;
import org.u2g.codylab.teamboard.mapper.ColumnMapper;
import org.u2g.codylab.teamboard.repository.ColumnRepository;
import org.u2g.codylab.teamboard.repository.ProjectRepository;
import org.u2g.codylab.teamboard.entity.Column;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ColumnServiceTest {
    @Mock
    private ColumnMapper columnMapper;
    @Mock
    private ColumnRepository columnRepository;
    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ColumnService columnService;

    @Test
    void shouldReturnCardSuccessfully_whenCardExists() {

    //  Arrange
        when(columnRepository.findById(1L)).thenReturn(Optional.empty());
    // Act e Asset
        assertThrows(CustomNotFoundException.class, () -> columnService.getById(1L));
    }

}
