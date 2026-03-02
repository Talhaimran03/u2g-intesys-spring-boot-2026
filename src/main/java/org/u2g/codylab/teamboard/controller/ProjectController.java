package org.u2g.codylab.teamboard.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.u2g.codylab.teamboard.api.ProjectApi;
import org.u2g.codylab.teamboard.dto.ProjectRequestApiDTO;
import org.u2g.codylab.teamboard.dto.ProjectResponseApiDTO;
import org.u2g.codylab.teamboard.service.ProjectService;
import org.u2g.codylab.teamboard.util.PageUtils;

import java.util.List;

@RestController
public class ProjectController implements ProjectApi {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Override
    public ResponseEntity<List<ProjectResponseApiDTO>> getAllProjects(Integer page, Integer size, String sort) {

        List<String> sortingFields = List.of("createdAt", "id", "title");

        sort = PageUtils.checkSort(sort, sortingFields);
        Pageable pageData = PageUtils.buildPageable(page, size, sort);

        Page<ProjectResponseApiDTO> projectResponse = projectService.getAllProjects(pageData);

        return new ResponseEntity<>(projectResponse.getContent(),
                PageUtils.generatePaginationHttpHeaders(projectResponse),
                HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ProjectResponseApiDTO> createProject(ProjectRequestApiDTO projectApiDTO) {
        return ResponseEntity.ok(projectService.addProject(projectApiDTO));
    }

    @Override
    public ResponseEntity<Void> deleteProjectById(Long id) {
        return ResponseEntity.ok(projectService.deleteProjectById(id));
    }

    @Override
    public ResponseEntity<ProjectResponseApiDTO> getProjectById(Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @Override
    public ResponseEntity<ProjectResponseApiDTO> updateProjectById(Long id, ProjectRequestApiDTO projectRequestApiDTO) {
        return ResponseEntity.ok(projectService.updateProjectById(id, projectRequestApiDTO));
    }
}
