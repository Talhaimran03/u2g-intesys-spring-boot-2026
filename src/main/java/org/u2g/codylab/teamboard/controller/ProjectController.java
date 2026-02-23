package org.u2g.codylab.teamboard.controller;

import org.springframework.web.bind.annotation.*;
import org.u2g.codylab.teamboard.entity.Project;
import org.u2g.codylab.teamboard.service.ProjectService;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    List<Project> projectList = new ArrayList<>();

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public List<Project> getAllProjects() {
        return projectService.getAllProjects();
    }

    @PostMapping
    public List<Project> createProject(@RequestBody Project project) {
        projectList.add(project);
        return new ArrayList<>(projectList);
    }

    @DeleteMapping
    public List<Project> deleteProjects() {
        projectList.clear();
        return new ArrayList<>(projectList);
    }
}
