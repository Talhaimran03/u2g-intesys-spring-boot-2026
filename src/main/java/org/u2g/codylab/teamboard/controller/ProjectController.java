package org.u2g.codylab.teamboard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.u2g.codylab.teamboard.api.ProjectApi;
import org.u2g.codylab.teamboard.dto.ProjectApiDTO;
import org.u2g.codylab.teamboard.service.ProjectService;

import java.util.List;

@RestController
public class ProjectController implements ProjectApi {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Override
    public ResponseEntity<List<ProjectApiDTO>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @Override
    public ResponseEntity<ProjectApiDTO> createProject(ProjectApiDTO projectApiDTO) {
        return ResponseEntity.ok(projectService.addProject(projectApiDTO));
    }

    @Override
    public ResponseEntity<List<ProjectApiDTO>> deleteAllProjects() {
        return  ResponseEntity.ok(projectService.deleteProjects());
    }

    @Override
    public ResponseEntity<ProjectApiDTO> getProjectById(Long id) {
        return  ResponseEntity.ok(projectService.getProjectById(id));
    }

}
