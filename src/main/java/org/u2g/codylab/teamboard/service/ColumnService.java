package org.u2g.codylab.teamboard.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
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
        return columnRepository.findAll().stream().map(columnMapper::toResponse).toList();
    }

    public ColumnResponseApiDTO getById(Long id) {
        log.info("Getting column by id: {}", id);
        return columnRepository.findById(id).map(columnMapper::toResponse)
                .orElseThrow(() -> new CustomNotFoundException("column not found with id: " + id));
    }

    public ColumnResponseApiDTO create(CreateColumnRequestApiDTO request) {
        log.info("Creating column: {}", request);
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new CustomNotFoundException("project not found with id: " + request.getProjectId()));
        Column column = columnMapper.toEntity(request);
        column.setProject(project);
        Column saved = columnRepository.save(column);
        log.info("Column created: {}", saved);
        return columnMapper.toResponse(saved);
    }

    public ColumnResponseApiDTO update(Long id, UpdateColumnRequestApiDTO request) {
        log.info("Updating column: {}", request);
        Column column = columnRepository.findById(id)
                .orElseThrow(() -> new CustomNotFoundException("column not found with id: " + id));
        column.setTitle(request.getTitle());
        column.setPosition(request.getPosition());
        Column updated = columnRepository.save(column);
        log.info("Column updated: {}", updated);
        return columnMapper.toResponse(updated);
    }

    public void delete(Long id) {
        log.info("Deleting column: {}", id);
        if (!columnRepository.existsById(id)) {
            throw new CustomNotFoundException("column not found with id: " + id);
        }
        log.info("Column deleted: {}", id);
        columnRepository.deleteById(id);
    }
}

