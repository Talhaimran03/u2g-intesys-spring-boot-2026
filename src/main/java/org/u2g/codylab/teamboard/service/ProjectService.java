package org.u2g.codylab.teamboard.service;

import org.springframework.stereotype.Service;
import org.u2g.codylab.teamboard.dto.ProjectApiDTO;
import org.u2g.codylab.teamboard.entity.Project;
import org.u2g.codylab.teamboard.mapper.ProjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    private final ProjectMapper projectMapper;
    List<Project> projectList = new ArrayList<>(List.of(new Project(1L, "Fist object", "Demo description")));

    public ProjectService(ProjectMapper projectMapper) {
        this.projectMapper = projectMapper;
    }

    public List<ProjectApiDTO> getAllProjects() {
        return projectList.stream().map(projectMapper::toApiDTO).toList();
    }

    public ProjectApiDTO addProject(ProjectApiDTO project) {
        Project projectEntity = projectMapper.toEntity(project);
        projectList.add(projectEntity);
        return projectMapper.toApiDTO(projectEntity);
    }

    public List<ProjectApiDTO> deleteProjects() {
        projectList.clear();
        return projectList.stream().map(projectMapper::toApiDTO).toList();
    }

    public ProjectApiDTO getProjectById(Long id) {
        Project project = projectList.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);

        return project == null ? null : projectMapper.toApiDTO(project);
    }

    public Project getOneProjects(long id){
        List<Project> projects = getAllProjects();
        Optional<Project> project = projects.stream().filter(p -> p.getId() == id).findFirst();
        return project.orElse(null);
    }
}
