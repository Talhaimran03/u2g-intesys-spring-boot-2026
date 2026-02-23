package org.u2g.codylab.teamboard.service;

import org.springframework.stereotype.Service;
import org.u2g.codylab.teamboard.entity.Project;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectService {

    List<Project> projectList = new ArrayList<>(List.of(new Project(1L, "Fist object", "Demo description")));

    public List<Project> getAllProjects() {
        return projectList;
    }

    public List<Project> addProject(Project project) {
        projectList.add(project);
        return new ArrayList<>(projectList);
    }

    public List<Project> deleteProjects() {
        projectList.clear();
        return new ArrayList<>(projectList);
    }

    public Project getProjectById(Long id) {
        return projectList.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElseGet(() -> projectList.stream()
                        .filter(p -> p.getId().equals(id))
                        .findFirst()
                        .orElse(null));
    }
}
