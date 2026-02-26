package org.u2g.codylab.teamboard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.u2g.codylab.teamboard.api.ProjectApi;
import org.u2g.codylab.teamboard.dto.ProjectPageResponseApiDTO;
import org.u2g.codylab.teamboard.dto.ProjectRequestApiDTO;
import org.u2g.codylab.teamboard.dto.ProjectResponseApiDTO;
import org.u2g.codylab.teamboard.service.ProjectService;

import java.util.List;

@RestController
public class ProjectController implements ProjectApi {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }


    @GetMapping("/projects")
    public ResponseEntity<ProjectPageResponseApiDTO> getAllProjects(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false, defaultValue = "title") String sort,
            @RequestParam(required = false) String title) {
        return ResponseEntity.ok(projectService.getAllProjects(page, size, sort, title));
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
