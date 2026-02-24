package org.u2g.codylab.teamboard.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
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

    @GetMapping("/{id}")
    public Project getOneProject(@PathVariable Long id) throws ResponseStatusException  {
        Project project = projectService.getOneProjects(id);
        if(project==null){
//            throw new RuntimeException("Project not found with id:" +id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found");
        }
        return project;
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
