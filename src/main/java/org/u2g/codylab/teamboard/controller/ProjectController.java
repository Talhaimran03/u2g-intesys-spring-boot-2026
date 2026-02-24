package org.u2g.codylab.teamboard.controller;

import org.springframework.web.bind.annotation.*;
import org.u2g.codylab.teamboard.entity.Project;
import org.u2g.codylab.teamboard.service.ProjectService;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public List<Project> getAllProjects() {
        return projectService.getAllProjects();
    }

    @GetMapping("/{id}")
    public Project getProjectById(@PathVariable Long id) {return projectService.getProjectById(id);}
    @PostMapping
    public List<Project> createProject(@RequestBody Project project) {
        return projectService.addProject(project);
    }

    @DeleteMapping
    public List<Project> deleteProjects() {
        return projectService.deleteProjects();
    }

}
