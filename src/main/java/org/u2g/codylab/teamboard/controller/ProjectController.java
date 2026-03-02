package org.u2g.codylab.teamboard.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.u2g.codylab.teamboard.api.ProjectApi;
import org.u2g.codylab.teamboard.dto.CreateProjectRequestApiDTO;
import org.u2g.codylab.teamboard.dto.ProjectResponseApiDTO;
import org.u2g.codylab.teamboard.dto.UpdateProjectRequestApiDTO;
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
    public ResponseEntity<ProjectResponseApiDTO> createProject(CreateProjectRequestApiDTO projectApiDTO) {
        ProjectResponseApiDTO response = projectService.addProject(projectApiDTO);
        return ResponseEntity.status(201).body(response);
    }

    @Override
    public ResponseEntity<Void> deleteProjectById(Long id) {
        projectService.deleteProjectById(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<ProjectResponseApiDTO> getProjectById(Long id) {
        ProjectResponseApiDTO response = projectService.getProjectById(id);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ProjectResponseApiDTO> updateProject(Long id, UpdateProjectRequestApiDTO updateProjectRequestApiDTO) {
        ProjectResponseApiDTO response = projectService.updateProjectById(id, updateProjectRequestApiDTO);
        return ResponseEntity.ok(response);
    }
}
