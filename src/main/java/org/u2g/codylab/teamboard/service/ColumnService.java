package org.u2g.codylab.teamboard.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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

    public List<ColumnResponseApiDTO> getAll() {
        log.info("Getting all columns");
        List<ColumnResponseApiDTO> columns = columnRepository.findAll().stream()
                .map(columnMapper::toResponse).toList();
        log.info("Retrieved {} columns", columns.size());
        return columns;
    }

    public ColumnResponseApiDTO getById(Long id) {
        log.info("Getting column with id: {}", id);
        ColumnResponseApiDTO column = columnRepository.findById(id)
                .map(columnMapper::toResponse)
                .orElseThrow(() -> new CustomNotFoundException("Column not found with id: " + id));
        log.info("Retrieved column: {}", column);
        return column;
    }

    public ColumnResponseApiDTO create(CreateColumnRequestApiDTO request) {
        log.info("Creating column: {}", request);

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new CustomNotFoundException("Project not found with id: " + request.getProjectId()));

        Column column = columnMapper.toEntity(request);
        column.setProject(project);
        Column saved = columnRepository.save(column);

        log.info("Column created with id: {}", saved.getId());
        return columnMapper.toResponse(saved);
    }

    public ColumnResponseApiDTO update(Long id, UpdateColumnRequestApiDTO request) {
        log.info("Updating column with id: {}", id);

        Column column = columnRepository.findById(id)
                .orElseThrow(() -> new CustomNotFoundException("Column not found with id: " + id));

        column.setTitle(request.getTitle());
        column.setPosition(request.getPosition());
        Column updated = columnRepository.save(column);

        log.info("Column updated with id: {}", updated.getId());
        return columnMapper.toResponse(updated);
    }

    public void delete(Long id) {
        log.info("Deleting column with id: {}", id);

        if (!columnRepository.existsById(id)) {
            throw new CustomNotFoundException("Column not found with id: " + id);
        }
        columnRepository.deleteById(id);

        log.info("Column deleted with id: {}", id);
    }
}