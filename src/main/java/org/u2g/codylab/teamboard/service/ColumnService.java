package org.u2g.codylab.teamboard.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.u2g.codylab.teamboard.dto.ColumnResponseApiDTO;
import org.u2g.codylab.teamboard.dto.CreateColumnRequestApiDTO;
import org.u2g.codylab.teamboard.dto.UpdateColumnRequestApiDTO;
import org.u2g.codylab.teamboard.entity.Column;
import org.u2g.codylab.teamboard.entity.Project;
import org.u2g.codylab.teamboard.mapper.ColumnMapper;
import org.u2g.codylab.teamboard.repository.ColumnRepository;
import org.u2g.codylab.teamboard.repository.ProjectRepository;

import java.util.List;

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
        return columnRepository.findAll().stream().map(columnMapper::toResponse).toList();
    }

    public ColumnResponseApiDTO getById(Long id) {
        return columnRepository.findById(id).map(columnMapper::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public ColumnResponseApiDTO create(CreateColumnRequestApiDTO request) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Column column = columnMapper.toEntity(request);
        column.setProject(project);
        Column saved = columnRepository.save(column);
        return columnMapper.toResponse(saved);
    }

    public ColumnResponseApiDTO update(Long id, UpdateColumnRequestApiDTO request) {
        Column column = columnRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        column.setTitle(request.getTitle());
        column.setPosition(request.getPosition());
        Column updated = columnRepository.save(column);
        return columnMapper.toResponse(updated);
    }

    public void delete(Long id) {
        if (!columnRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        columnRepository.deleteById(id);
    }
}

