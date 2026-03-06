package org.u2g.codylab.teamboard.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

@Slf4j
@Transactional
@Service
public class ColumnService {
    private final ColumnRepository columnRepository;
    private final ProjectRepository projectRepository;
    private final ColumnMapper columnMapper;

    public ColumnService(ColumnRepository columnRepository, ProjectRepository projectRepository, ColumnMapper columnMapper) {
        this.columnRepository = columnRepository;
        this.projectRepository = projectRepository;
        this.columnMapper = columnMapper;
    }

    public List<ColumnResponseApiDTO> getColumnsByProjectId(Long projectId) {
        log.info("Getting all columns");
        projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomNotFoundException("Project not found"));

        List<Column> columns = columnRepository.findByProjectId(projectId);
        return columns.stream().map(columnMapper::toResponse).toList();
    }

    public ColumnResponseApiDTO getById(Long id) {
        log.info("Getting column with id: {}", id);
        return columnRepository.findById(id).map(columnMapper::toResponse)
                .orElseThrow(() -> new CustomNotFoundException("Column not found"));
    }

    public ColumnResponseApiDTO create(CreateColumnRequestApiDTO request) {
        log.info("Creating column: {}", request);
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new CustomIllegalArgumentException("Title is required");
        }
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new CustomNotFoundException("Project not found"));
        Column column = columnMapper.toEntity(request);
        column.setProject(project);
        Column saved = columnRepository.save(column);
        log.info("Column created: {}", saved.getId());
        return columnMapper.toResponse(saved);
    }

    public ColumnResponseApiDTO update(Long id, UpdateColumnRequestApiDTO request) {
        log.info("Updating column with id: {}", id);
        Column column = columnRepository.findById(id)
                .orElseThrow(() -> new CustomNotFoundException("Column not found"));
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new CustomIllegalArgumentException("Title is required");
        }
        column.setTitle(request.getTitle());
        column.setPosition(request.getPosition());
        Column updated = columnRepository.save(column);
        log.info("Column updated: {}", updated.getId());
        return columnMapper.toResponse(updated);
    }

    public void delete(Long id) {
        log.info("Deleting column with id: {}", id);
        if (!columnRepository.existsById(id)) {
            throw new CustomNotFoundException("Column not found");
        }
        columnRepository.deleteById(id);
        log.info("Column deleted: {}", id);
    }
}
